package id.forum.post.data.repository

import androidx.lifecycle.MutableLiveData
import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image
import id.forum.post.data.service.PostApolloService
import id.forum.core.post.domain.model.Post
import id.forum.post.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject


@ExperimentalCoroutinesApi
class PostRepositoryImpl : PostRepository, KoinComponent {

    override fun createPost(post: Post, mediaList: List<Image>): MutableLiveData<Resource<Post>> {
        val postService: PostApolloService by inject()
        return postService.createPost(post, mediaList)
    }
}