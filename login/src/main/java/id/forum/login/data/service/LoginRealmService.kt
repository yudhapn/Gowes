package id.forum.login.data.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import io.realm.mongodb.App
import io.realm.mongodb.Credentials

class LoginRealmService(private val realmAppClient: App) {
    fun loginByEmail(email: String, password: String): MutableLiveData<Resource<UserData>> {
        val result = MutableLiveData<Resource<UserData>>()
        result.postValue(Resource.loading(null))
        realmAppClient.loginAsync(Credentials.emailPassword(email, password)) {
            when {
                it.isSuccess -> result.postValue(Resource.success(UserData()))
                else -> result.postValue(Resource.error(it.error.errorMessage.toString(), null))
            }
        }
        return result
    }

    fun resetPassword(email: String, newPassword: String): LiveData<Resource<UserData>> {
        val result = MutableLiveData<Resource<UserData>>()
        result.postValue(Resource.loading(null))
        realmAppClient.emailPassword.callResetPasswordFunctionAsync(email, newPassword, emptyArray()) {
            when {
                it.isSuccess -> result.postValue(Resource.success(UserData()))
                else -> result.postValue(Resource.error(it.error.toString(), null))
            }
        }
        return result
    }
}