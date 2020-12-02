package id.forum.login.domain.repository

import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.data.UserData

interface LoginRepository {
    fun loginByEmail(email: String, password: String): LiveData<Resource<UserData>>
    fun resetPassword(email: String, newPassword: String): LiveData<Resource<UserData>>
}