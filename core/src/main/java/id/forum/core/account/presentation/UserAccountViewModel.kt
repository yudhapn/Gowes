package id.forum.core.account.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.account.domain.usecase.AuthenticateUseCase
import id.forum.core.account.domain.usecase.UpdateAccountCacheUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.data.Status
import id.forum.core.data.Status.*
import id.forum.core.data.UserData
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserAccountViewModel(
    private val authenticateUseCase: AuthenticateUseCase,
    private val updateAccountCacheUseCase: UpdateAccountCacheUseCase,
    private var currentUser: User
) :
    BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private var email: String = ""
    private var password: String = ""

    private val _userAccount = MutableLiveData<Resource<User>>()
    val userAccount: LiveData<Resource<User>>
        get() = _userAccount

    private val _messageSnackBar = MutableLiveData<String>()
    val messageSnackBar: LiveData<String>
        get() = _messageSnackBar

    private val _keyword = MutableLiveData<String>()
    val keyword: LiveData<String>
        get() = _keyword

    init {
        Log.d("UserAccountViewModel", "UserAccountViewModel created")
        val hasLoggedIn = checkUserHasLoggedIn()
        when {
            hasLoggedIn -> setCurrentUser()
            else -> _userAccount.postValue(Resource.success(null))
        }
    }

    private fun setCurrentUser() {
        _userAccount.postValue(Resource.loading(currentUser))
        if (currentUser.id.isNotBlank()) {
            Log.d("UserAccountViewModel", "currentUser is not blank")
            _userAccount.postValue(Resource.success(currentUser))
        } else {
            ioScope.launch(getJobErrorHandler() + supervisorJob) {
                val userAccount = authenticateUseCase.getUserAccount(email, password)
                currentUser = userAccount.data ?: currentUser
                _userAccount.postValue(userAccount)
                Log.d("UserAccountViewModel", "current user is ${_userAccount.value?.data}")
            }
        }
    }

    fun setUserInformationLogin(email: String, password: String) {
        // has logged in
        this.email = email
        this.password = password
        setCurrentUser()
    }

    private fun checkUserHasLoggedIn() = authenticateUseCase.isLoggedIn()

    fun getCurrentUser() = currentUser

    fun updateCurrentUser(user: User) {
        currentUser = user
        _userAccount.postValue(Resource.success(currentUser))
        updateAccountCacheUseCase.execute(currentUser)
    }

    fun setCurrentState(state: Status) {
        when (state) {
            LOADING -> _userAccount.postValue(Resource.loading(currentUser))
            SUCCESS -> _userAccount.postValue(Resource.success(currentUser))
            ERROR -> _userAccount.postValue(Resource.error("Something Went Wrong", currentUser))
        }
    }

    fun logout(): LiveData<Resource<UserData>> {
        _userAccount.postValue(Resource.loading(currentUser))
        val userData = authenticateUseCase.logout()
        currentUser = User()
        updateAccountCacheUseCase.execute(currentUser)
        _userAccount.postValue(Resource.success(currentUser))
        return userData
    }

    fun showSnackBar(message: String) {
        _messageSnackBar.postValue(message)
    }

    fun setSearchKeyword(keyword: String) {
        _keyword.value = keyword
        Log.d("UserAccountViewModel", "search keyword: $keyword")
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(UserAccountViewModel::class.java.simpleName, "An error happened: $e")
    }
}