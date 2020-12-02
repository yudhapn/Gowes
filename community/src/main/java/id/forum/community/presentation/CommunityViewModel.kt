package id.forum.community.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import id.forum.community.domain.usecase.*
import id.forum.core.account.domain.usecase.UpdateAccountCacheUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.data.Status.SUCCESS
import id.forum.core.post.domain.model.Post
import id.forum.core.user.domain.model.User
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CommunityViewModel(
    private val getCommunityProfileUseCase: GetCommunityProfileUseCase,
    private val joinCommunityUseCase: JoinCommunityUseCase,
    private val requestJoinCommunityUseCase: RequestJoinCommunityUseCase,
    private val cancelRequestJoinCommunityUseCase: CancelRequestJoinCommunityUseCase,
    private val leaveCommunityUseCase: LeaveCommunityUseCase,
    private val updateAccountCacheUseCase: UpdateAccountCacheUseCase,
    private var communityArgs: Community,
    val currentUser: User
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()

    private var _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private val _community = MutableLiveData<Resource<Community>>()
    val community: LiveData<Resource<Community>>
        get() = _community

    val isAdmin = Transformations.map(community) {
        Log.d("communityViewModel", "there is an admin")
        // if admin list contain currentuid means currentuser is an admin
        it.data?.admins?.find { admin -> admin.id == currentUser.id } != null
    }
    val isMember = Transformations.map(community) {
        // if member list contain currentuid means currentuser is an member
        it.data?.members?.find { member -> member.id == currentUser.id } != null
    }
    val isRequest = Transformations.map(community) {
        // if request list contain currentuid means currentuser is requesting to join the community
        it.data?.memberRequest?.find { memberRequest -> memberRequest.id == currentUser.id } != null
    }

    init {
        _community.value = Resource.success(communityArgs)
        getCommunityProfile()
    }

    private fun getCommunityProfile(isRefresh: Boolean = false) {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            getCommunityProfileUseCase.execute(communityArgs.id, isRefresh)
                .onStart {
                    _community.postValue(Resource.loading(_community.value?.data))
                }
                .catch { cause ->
                    _community.postValue(
                        Resource.error(cause.message.toString(), _community.value?.data)
                    )
                }
                .collect {
                    _community.postValue(Resource.success(it))
                }
        }
    }

    fun deletePost(post: Post) {
        val community = _community.value?.data
        val list = community?.posts?.toMutableList() ?: mutableListOf()
        list.remove(post)
        community?.posts = list
        _community.value = Resource.success(community)
    }

    fun joinCommunity(answer: String = "") {
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            communityArgs = _community.value?.data ?: communityArgs
            val joinMessage = "Waiting for admin's approval"
            val requestMessage = "You have joined into the community"

            _community.postValue(Resource.loading(_community.value?.data))
            val communityResource =
                if (communityArgs.isPrivate) {
                    _message.postValue(requestMessage)
                    communityArgs.memberRequest.toMutableList().let {
                        it.add(currentUser)
                        communityArgs.memberRequest = it
                    }
                    requestJoinCommunityUseCase.execute(communityArgs.id, currentUser.id, answer)
                } else {
                    _message.postValue(joinMessage)
                    communityArgs.members.toMutableList().let { members ->
                        members.add(currentUser)
                        communityArgs.members = members
                    }
                    currentUser.communities.toMutableList().let { communities ->
                        val (id, profile) = communityArgs
                        communities.add(Community(id = id, profile = profile))
                        currentUser.communities = communities
                    }
                    joinCommunityUseCase.execute(communityArgs.id, currentUser.id)
                }
            _community.postValue(
                when (communityResource.status) {
                    SUCCESS -> {
                        updateCurrentAccountCache()
                        Resource.success(communityArgs)
                    }
                    else -> Resource.error("Something went wrong", communityArgs)
                }
            )
        }
    }

    fun cancelRequestJoinCommunity() {
        _community.postValue(Resource.loading(_community.value?.data))
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            communityArgs = _community.value?.data ?: communityArgs
            communityArgs.memberRequest = removeUser(communityArgs.memberRequest.toMutableList())
            val communityResource = cancelRequestJoinCommunityUseCase.execute(
                communityArgs.id,
                currentUser.id
            )
            _community.postValue(
                when (communityResource.status) {
                    SUCCESS -> {
                        updateCurrentAccountCache()
                        Resource.success(communityArgs)
                    }
                    else -> Resource.error("Something went wrong", communityArgs)
                }
            )
        }
    }

    fun leaveCommunity(): Boolean {
        val isAdmin = isAdmin.value ?: false
        val adminSize = _community.value?.data?.admins?.size ?: 1
        if (isAdmin && (adminSize <= 1)) {
            return false
        }
        _community.postValue(Resource.loading(_community.value?.data))
        ioScope.launch(getJobErrorHandler() + supervisorJob) {
            communityArgs = _community.value?.data ?: communityArgs
            communityArgs.admins = removeUser(communityArgs.admins.toMutableList())
            communityArgs.members = removeUser(communityArgs.members.toMutableList())
            currentUser.communities = removeCommunity(currentUser.communities.toMutableList())
            val communityResource = leaveCommunityUseCase.execute(communityArgs.id, currentUser.id)
            _community.postValue(
                when (communityResource.status) {
                    SUCCESS -> {
                        updateCurrentAccountCache()
                        Resource.success(communityArgs)
                    }
                    else -> Resource.error("Something went wrong", communityArgs)
                }
            )
        }
        return true
    }

    private fun updateCurrentAccountCache() {
        updateAccountCacheUseCase.execute(currentUser)
    }

    private fun removeUser(users: MutableList<User>) =
        users.let { list ->
            list.find { it.id == currentUser.id }?.let { list.remove(it) }
            list
        }


    private fun removeCommunity(communities: MutableList<Community>) =
        communities.let { list ->
            list.find { it.id == communityArgs.id }?.let { list.remove(it) }
            list
        }

    fun refreshData() {
        getCommunityProfile(true)
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        _community.postValue(Resource.error("$e", null))
        Log.e(CommunityViewModel::class.java.simpleName, "An error happened: $e")
    }
}
