package id.forum.register.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.data.UserData
import id.forum.core.user.domain.model.User
import id.forum.register.domain.usecase.RegisterByEmailUseCase
import id.forum.register.domain.usecase.SetupUserUseCase
import id.forum.register.domain.usecase.UpdateTokenCacheUseCase

class RegisterViewModel(
    private val registerByEmailUseCase: RegisterByEmailUseCase,
    private val updateTokenCacheUseCase: UpdateTokenCacheUseCase,
    private val setupUserUseCase: SetupUserUseCase
) :
    BaseViewModel() {

    fun registerByEmail(email: String, password: String): LiveData<Resource<UserData>> {
        return registerByEmailUseCase.invoke(email, password)
    }

    fun updateUser(user: User, imageUri: Uri?): LiveData<Resource<User>> =
        setupUserUseCase.invoke(user, imageUri)

    fun updateToken(token: Token) {
        updateTokenCacheUseCase.invoke(token)
    }
}
