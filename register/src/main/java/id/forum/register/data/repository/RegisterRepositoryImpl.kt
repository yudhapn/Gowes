package id.forum.register.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.data.Resource
import id.forum.core.data.UserData
import id.forum.core.user.domain.model.User
import id.forum.register.data.service.RegisterRealmService
import id.forum.register.domain.repository.RegisterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject


@ExperimentalCoroutinesApi
class RegisterRepositoryImpl : RegisterRepository, KoinComponent {

    override fun registerByEmail(
        email: String,
        password: String
    ): MutableLiveData<Resource<UserData>> {
        val registerRealmService: RegisterRealmService by inject()
        return registerRealmService.registerByEmail(email, password)
    }

    override fun setupUser(user: User, imageUri: Uri?): LiveData<Resource<User>> {
        val registerRealmService: RegisterRealmService by inject()
        return registerRealmService.updateSetupUser(user, imageUri)
    }
}
