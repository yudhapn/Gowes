package id.forum.post.data.service

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import id.forum.core.CommunityQuery
import id.forum.core.CreatePostMutation
import id.forum.core.PostsByCommunitiesIdQuery
import id.forum.core.UserQuery
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.media.domain.model.Image
import id.forum.core.post.data.mapper.bindInputPost
import id.forum.core.post.data.mapper.mapToDomain
import id.forum.core.post.domain.model.Attachment
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class PostApolloService(
    private val token: Token,
    private val currentUser: User,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun createPost(post: Post, mediaList: List<Image>): MutableLiveData<Resource<Post>> {
        val result = MutableLiveData<Resource<Post>>()
        result.postValue(Resource.loading(post))
        val storageRef = storage.getReference("gowes_forum")
        if (mediaList.isNotEmpty()) {
            auth.signInAnonymously().addOnCompleteListener { taskAuth ->
                if (taskAuth.isSuccessful) {
                    val attachments = mutableListOf<Attachment>()
                    mediaList.forEach { media ->
                        val pathString = media.name
                        val fileReference = storageRef.child(pathString)
                        val uploadTask = fileReference.putBytes(media.bitmapData!!)
                        uploadTask.continueWithTask {
                            if (!it.isSuccessful) throw it.exception?.cause!!
                            fileReference.downloadUrl
                        }.addOnCompleteListener { taskUri ->
                            if (taskUri.isSuccessful) {
                                val imageUrl = taskUri.result.toString()
                                attachments.add(Attachment(imageUrl, pathString))
                                if (mediaList.size == attachments.size) {
                                    post.attachments = attachments
                                    CoroutineScope(Dispatchers.IO).launch {
                                        result.postValue(insertPost(post))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                result.postValue(insertPost(post))
            }
        }
        return result
    }

    private suspend fun insertPost(post: Post): Resource<Post> {
        val postHomeQuery =
            PostsByCommunitiesIdQuery(Input.fromNullable(currentUser.communities.map { it.id }))
        val userQuery =
            UserQuery(Input.fromNullable(currentUser.id))
        val communityQuery =
            CommunityQuery(Input.fromNullable(post.community.id))
        val mutation = CreatePostMutation(bindInputPost(post))

        try {
            val response = apolloClient
                .mutate(mutation)
                .refetchQueries(postHomeQuery, userQuery, communityQuery)
                .toDeferred().await()

            if (response.hasErrors()) {
                return Resource.error("${response.errors?.get(0)?.message}", post)
            }
            response.data?.insertOnePost?.fragments?.postDetails?.let {
                val updatedPost = it.mapToDomain(currentUser)
                return Resource.success(updatedPost)
            } ?: run {
                throw IllegalStateException("unknown error in PostApolloService")
            }
        } catch (e: ApolloException) {
            throw Exception("PostApolloService, error happened: ${e.message}")
        }
    }
}