package id.forum.chat.presentation.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import id.forum.chat.domain.usecase.GetMembersCommunitiesUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.data.Resource
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CreateChatViewModel(
    val currentUser: User,
    private val getMembersCommunitiesUseCase: GetMembersCommunitiesUseCase
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _query = MutableLiveData<String>()

    val members = Transformations.map(_query) {
        if (it.isEmpty()) {
            _users.value?.data
        } else {
            _users.value?.data?.filter { list ->
                list.profile.name.contains(it, true) || list.userName.contains(it, true)
            }
        }
    }

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users: LiveData<Resource<List<User>>>
        get() = _users

    init {
        getMembersCommunities()
    }

    fun searchMember(query: String) {
        _query.value = query
    }

    private fun getMembersCommunities(isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getMembersCommunitiesUseCase.execute(isRefresh)
                .onStart {
                    _users.postValue(Resource.loading(_users.value?.data))
                }
                .catch { cause ->
                    _users.postValue(Resource.error(cause.message.toString(), _users.value?.data))
                }
                .collect {
                    _users.postValue(Resource.success(it))
                    _query.postValue("")
                }
        }
    }

    fun refreshMembers() = getMembersCommunities(isRefresh = true)

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Log.e(CreateChatViewModel::class.java.simpleName, "An error happened: $e")
    }
}
