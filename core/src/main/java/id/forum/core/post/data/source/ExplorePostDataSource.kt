package id.forum.core.post.data.source

import android.util.Log
import androidx.paging.PagingSource
import id.forum.core.data.Status
import id.forum.core.post.data.service.PostApolloService
import id.forum.core.post.domain.model.Post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
class ExplorePostDataSource(private val isRefresh: Boolean) : PagingSource<Date, Post>(),
    KoinComponent {
    override suspend fun load(params: LoadParams<Date>): LoadResult<Date, Post> {
        val position = params.key ?: Calendar.getInstance().time
        val service: PostApolloService by inject()
        val dateString = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(position)
        val response = service.posts(dateString, isRefresh)

        val calendar = Calendar.getInstance()
        calendar.time = position
        calendar.add(Calendar.MONTH, -1)
        val nextValue = calendar.time

        Log.d(
            "ExplorePostDataSource",
            "date query: $dateString, response lastitemdate: ${
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(nextValue)
            }"
        )

        return when (response.status) {
            Status.SUCCESS -> LoadResult.Page(
                data = response.data ?: emptyList(),
                prevKey = null,
                nextKey = if ((response.data ?: emptyList()).isEmpty()) null else nextValue
            )
            else -> LoadResult.Error(throwable = Throwable(message = response.message))
        }
    }
}