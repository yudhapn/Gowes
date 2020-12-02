package id.forum.core.account.data.service

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import id.forum.core.UpdateUserByAccountIdMutation
import id.forum.core.UserAccountCommunitiesQuery
import id.forum.core.account.data.mapper.mapToDomain
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.data.UserData
import id.forum.core.network.getFirebaseAccessToken
import id.forum.core.network.getTokenStitchRequest
import id.forum.core.type.UserUpdateInput
import id.forum.core.user.domain.model.User
import id.forum.core.util.SHARED_TKN
import id.forum.core.util.SHARED_USR
import io.realm.mongodb.App
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class AccountApolloService(
    private val auth: FirebaseAuth,
    private val realmAppClient: App
) : KoinComponent {

    suspend fun getUserAccount(email: String, password: String): Resource<User> {
        Log.d("AccountApolloService", "start getting token")
        val response = getTokenStitchRequest(email, password).execute()
        val token = Gson().fromJson(response.body?.string(), Token::class.java)
        Log.d("AccountApolloService", "Token: ${token.accessToken} \n userId: ${token.userId}")
        token.firebaseToken = getFirebaseAccessToken()
        saveTokenCache(token)
        val apolloClient: ApolloClient by inject { parametersOf(token) }
        try {
            val mutation =
                UpdateUserByAccountIdMutation(
                    Input.fromNullable(token.userId),
                    UserUpdateInput(notificationToken = Input.fromNullable(token.firebaseToken))
                )
            val userData = apolloClient.mutate(mutation).await()
            return userData.data?.updateOneUser?.fragments?.userDetails?.let {
                val user = it.mapToDomain()
                Log.d("AccountApolloService", "user: $user")
                val communitiesData =
                    apolloClient.query(UserAccountCommunitiesQuery(Input.fromNullable(user.id)))
                        .toDeferred().await()
                communitiesData.data.let { data ->
                    val communities = data?.members?.map { members ->
                        members?.fragments?.userCommunityDetails?.mapToDomain() ?: Community()
                    } ?: emptyList()
                    val bookmarkedPosts = data?.bookmarks?.map { bookmark ->
                        bookmark?.post?._id ?: ""
                    } ?: emptyList()
                    val postUpVotes = data?.postUpVotes?.map { upVote ->
                        upVote?.post ?: ""
                    } ?: emptyList()
                    val postDownVotes = data?.postDownVotes?.map { downVote ->
                        downVote?.post ?: ""
                    } ?: emptyList()
                    val commentUpVotes = data?.commentUpVotes?.map { upVote ->
                        upVote?.comment ?: ""
                    } ?: emptyList()
                    val commentDownVotes = data?.commentDownVotes?.map { downVote ->
                        downVote?.comment ?: ""
                    } ?: emptyList()

                    user.communities = communities
                    user.bookmarkedPosts = bookmarkedPosts
                    user.upVotePosts = postUpVotes
                    user.downVotePosts = postDownVotes
                    user.upVoteComments = commentUpVotes
                    user.downVoteComments = commentDownVotes
                }
                updateAccountCache(user)
                Resource.success(user)
            } ?: run {
                throw ApolloException(
                    "unknown error in AccountApolloService ${
                        userData.errors?.get(0)?.message
                    }"
                )
            }
        } catch (e: ApolloException) {
            throw Exception("AccountApolloService, error happened: ${e.message}")
        }
    }

    fun saveTokenCache(token: Token) {
        Log.d("AccountApolloService", "saveTokenCache token: $token")
        val prefEditor: SharedPreferences.Editor by inject()
        val tokenJson = Gson().toJson(token)
        prefEditor.putString(SHARED_TKN, tokenJson)
        prefEditor.apply()
    }

    fun updateAccountCache(user: User): User {
        val prefEditor: SharedPreferences.Editor by inject()
        val userJson = Gson().toJson(user)
        prefEditor.putString(SHARED_USR, userJson)
        prefEditor.apply()
        return user
    }

    private fun deleteAccountCache() {
        val prefEditor: SharedPreferences.Editor by inject()
        prefEditor.clear()
        prefEditor.apply()
    }

    fun logout(): MutableLiveData<Resource<UserData>> {
        val token: Token by inject()
        val apolloClient: ApolloClient by inject { parametersOf(token) }
        auth.signOut()
        val result = MutableLiveData<Resource<UserData>>()
        result.postValue(Resource.loading(null))
        realmAppClient.currentUser()?.logOutAsync {
            if (it.isSuccess) {
                // Clean user data cache
                deleteAccountCache()
                result.postValue(Resource.success(null))
                val mutation =
                    UpdateUserByAccountIdMutation(
                        Input.fromNullable(token.userId),
                        UserUpdateInput(notificationToken = Input.fromNullable(""))
                    )
                apolloClient.mutate(mutation).toDeferred()
            } else {
                result.postValue(Resource.error(it.error.toString(), null))
            }
        }
        return result
    }
}