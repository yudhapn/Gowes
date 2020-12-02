package id.forum.register.data.service

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import id.forum.core.UpdateSetupUserMutation
import id.forum.core.account.data.mapper.mapToDomain
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.data.UserData
import id.forum.core.network.getFirebaseAccessToken
import id.forum.core.network.getTokenStitchRequest
import id.forum.core.type.UserUpdateInput
import id.forum.core.user.domain.model.User
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class RegisterRealmService(
    private val realmAppClient: App,
    private val token: Token,
    private val storage: FirebaseStorage
) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun registerByEmail(
        email: String,
        password: String
    ): MutableLiveData<Resource<UserData>> {
        val result = MutableLiveData<Resource<UserData>>()
        result.postValue(Resource.loading(null))
        realmAppClient.emailPassword.registerUserAsync(email, password) { response ->
                if (response.isSuccess) {
                    realmAppClient.loginAsync(Credentials.emailPassword(email, password)) {
                        if (it.isSuccess) {
                            var token: Token
                            runBlocking {
                                withContext(Dispatchers.IO) {
                                    val response = getTokenStitchRequest(email, password).execute()
                                    token =
                                        Gson().fromJson(response.body?.string(), Token::class.java)
                                    token.firebaseToken = getFirebaseAccessToken()
                                }
                            }
                            result.postValue(Resource.success(UserData(User(), token)))
                        } else {
                            result.postValue(
                                Resource.error(it.error.toString(), null)
                            )
                        }
                    }
                } else {
                    result.postValue(Resource.error(response.error.toString(), null))
                }
            }
        return result
    }

    fun updateSetupUser(user: User, imageUri: Uri?): LiveData<Resource<User>> {
        val result = MutableLiveData<Resource<User>>()
        result.postValue(Resource.loading(null))
        val storageRef = storage.getReference("gowes_forum")
        val fileReference = storageRef.child(user.accountId + "-avatar")
        if (imageUri != null) {
            val uploadTask = fileReference.putFile(imageUri)
            uploadTask.continueWithTask {
                if (!it.isSuccessful) throw it.exception?.cause!!
                fileReference.downloadUrl
            }.addOnCompleteListener { taskUri ->
                if (taskUri.isSuccessful) {
                    val imageUrl = taskUri.result.toString()
                    user.profile.avatar = imageUrl
//                    Log.d("user avatar", imageUrl)
                    CoroutineScope(Dispatchers.IO).launch {
                        result.postValue(updateUser(user))
                    }

                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                result.postValue(updateUser(user))
            }
        }
        return result
    }

    private suspend fun updateUser(user: User): Resource<User> {
        Log.d("RegisterStitchService", "token: $token")
        val mutation = UpdateSetupUserMutation(
            Input.fromNullable(user.accountId),
            UserUpdateInput(
                accountId = Input.fromNullable(user.accountId),
                avatar = Input.fromNullable(user.profile.avatar),
                name = Input.fromNullable(user.profile.name),
                biodata = Input.fromNullable(user.profile.biodata),
                notificationToken = Input.fromNullable(token.firebaseToken),
                username = Input.fromNullable(user.userName)
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
                return Resource.success(it.mapToDomain())
            } ?: run {
                throw IllegalStateException("unknown error in authRepository")
            }
        } catch (e: ApolloException) {
            throw Exception("authRepository apollo, error happened: ${e.message}")
        }
    }
}