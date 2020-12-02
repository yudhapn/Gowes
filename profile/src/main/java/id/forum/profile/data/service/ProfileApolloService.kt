package id.forum.profile.data.service

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.google.firebase.storage.FirebaseStorage
import id.forum.core.UpdateUserByIdMutation
import id.forum.core.UserQuery
import id.forum.core.account.data.mapper.mapToDomain
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.post.data.mapper.mapToDomain
import id.forum.core.post.domain.model.Post
import id.forum.core.type.UserQueryInput
import id.forum.core.type.UserUpdateInput
import id.forum.core.user.domain.model.User
import id.forum.core.util.apolloResponseFetchers
import id.forum.profile.data.mapToDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class ProfileApolloService(
    private val currentUser: User,
    private val token: Token,
    private val storage: FirebaseStorage
) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun getUserProfile(userId: String, currentUserId: String, isRefresh: Boolean): Flow<User> =
        apolloClient
            .query(
                UserQuery(
                    Input.fromNullable(userId),
                    chatQuery = Input.fromNullable(
                        listOf(UserQueryInput(_id = Input.fromNullable(userId)))
                    )
                )
            )
            .responseFetcher(isRefresh.apolloResponseFetchers())
            .watcher()
            .toFlow()
            .map { response ->
                val user = response.data?.user?.fragments?.userDetails?.mapToDomain() ?: User()
                user.chats = response.data?.chats?.mapToDomain(currentUserId) ?: emptyList()
                user.communities = response.data?.members?.map { communityData ->
                    communityData?.fragments?.userCommunityDetails?.mapToDomain() ?: Community()
                } ?: emptyList()

                user.posts = response.data?.posts?.map { postData ->
                    postData?.fragments?.postDetails?.mapToDomain(currentUser) ?: Post()
                } ?: emptyList()

                user
            }

    fun updateProfile(user: User, imageUri: Uri?): LiveData<Resource<User>> {
        Log.d("ProfileApolloService", "user avatar: ${user.profile.avatar}")
        val result = MutableLiveData<Resource<User>>()
        result.postValue(Resource.loading(null))
        val storageRef = storage.getReference("gowes_forum")
        val fileReference = storageRef.child(user.id + "-avatar")
        if (imageUri != null) {
            val uploadTask = fileReference.putFile(imageUri)
            uploadTask.continueWithTask {
                if (!it.isSuccessful) throw it.exception?.cause!!
                fileReference.downloadUrl
            }.addOnCompleteListener { taskUri ->
                if (taskUri.isSuccessful) {
                    val imageUrl = taskUri.result.toString()
                    user.profile.avatar = imageUrl
                    CoroutineScope(Dispatchers.IO).launch {
                        result.postValue(updateProfile(user))
                    }

                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                result.postValue(updateProfile(user))
            }
        }
        return result
    }

    private suspend fun updateProfile(user: User): Resource<User> {
        val mutation = UpdateUserByIdMutation(
            Input.fromNullable(user.id),
            UserUpdateInput(
                avatar = Input.fromNullable(user.profile.avatar),
                name = Input.fromNullable(user.profile.name),
                biodata = Input.fromNullable(user.profile.biodata)
            )
        )
        try {
            val response = apolloClient
                .mutate(mutation)
                .toDeferred().await()
            if (response.hasErrors()) {
                return Resource.error("${response.errors}", user)
            }
            response.data?.updateOneUser?.fragments?.userDetails?.let {
                return Resource.success(user)
            } ?: run {
                throw IllegalStateException("unknown error in ProfileApolloService")
            }
        } catch (e: ApolloException) {
            throw Exception("ProfileApolloService, error happened: ${e.message}")
        }
    }
}