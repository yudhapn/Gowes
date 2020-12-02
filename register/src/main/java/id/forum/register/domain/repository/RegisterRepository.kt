package id.forum.register.domain.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import id.forum.core.user.domain.model.User

interface RegisterRepository {
    fun registerByEmail(email: String, password: String): LiveData<Resource<UserData>>
    fun setupUser(user: User, imageUri: Uri?): LiveData<Resource<User>>
}