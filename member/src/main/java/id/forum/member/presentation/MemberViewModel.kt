package id.forum.member.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.data.Status
import id.forum.core.user.domain.model.User
import id.forum.member.domain.usecase.AcceptRequestMemberUseCase
import id.forum.member.domain.usecase.AppointMemberAsAdminUseCase
import id.forum.member.domain.usecase.ExpelMemberUseCase
import id.forum.member.domain.usecase.RejectRequestMemberUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MemberViewModel(
    private val acceptRequestMemberUseCase: AcceptRequestMemberUseCase,
    private val rejectRequestMemberUseCase: RejectRequestMemberUseCase,
    private val appointMemberAsAdminUseCase: AppointMemberAsAdminUseCase,
    private val expelMemberUseCase: ExpelMemberUseCase
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private val _community = MutableLiveData<Resource<Community>>()
    val community: LiveData<Resource<Community>>
        get() = _community

    fun setCommunity(community: Community) {
        _community.postValue(Resource.success(community))
    }

    fun acceptMember(community: Community, requester: User) {
        ioScope.launch {
            community.members.toMutableList().let {
                it.add(requester)
                community.members = it
                community.memberRequest =
                    removeUser(community.memberRequest.toMutableList(), requester)
                ioScope.launch(getJobErrorHandler() + supervisorJob) {
                    Log.d("MemberViewModel", "acceptMember method is called\nargs communityId: ${community.id}\n" +
                            "args requesterId: ${requester.id}")
                    val communityResource = acceptRequestMemberUseCase.execute(
                        community.id, requester.id
                    )
                    _community.postValue(
                        when (communityResource.status) {
                            Status.SUCCESS -> {
                                Resource.success(community)
                            }
                            else -> Resource.error("Something went wrong", community)
                        }

                    )
                }
            }
        }
    }

    fun rejectMember(community: Community, requester: User) {
        ioScope.launch {
            community.memberRequest = removeUser(community.memberRequest.toMutableList(), requester)
            ioScope.launch(getJobErrorHandler() + supervisorJob) {
                Log.d("MemberViewModel", "rejectMember method is called\nargs communityId: ${community.id}\n" +
                        "args requesterId: ${requester.id}")
                val communityResource = rejectRequestMemberUseCase.execute(
                    community.id, requester.id
                )
                _community.postValue(
                    when (communityResource.status) {
                        Status.SUCCESS -> {
                            Resource.success(community)
                        }
                        else -> Resource.error("Something went wrong", community)
                    }
                )
            }
        }
    }

    fun appointAsAdmin(community: Community, member: User) {
        ioScope.launch {
            community.members = removeUser(community.members.toMutableList(), member)
            community.admins = addUser(community.admins.toMutableList(), member)
            ioScope.launch(getJobErrorHandler() + supervisorJob) {
                Log.d("MemberViewModel", "expelMember method is called\nargs communityId: ${community.id}\n" +
                        "args requesterId: ${member.id}")
                val communityResource = appointMemberAsAdminUseCase.execute(
                    community.id, member.id
                )
                _community.postValue(
                    when (communityResource.status) {
                        Status.SUCCESS -> Resource.success(community)
                        else -> Resource.error("Something went wrong", community)
                    }
                )
            }
        }
    }

    fun expelMember(community: Community, member: User) {
        ioScope.launch {
            community.members = removeUser(community.members.toMutableList(), member)
            ioScope.launch(getJobErrorHandler() + supervisorJob) {
                Log.d("MemberViewModel", "expelMember method is called\nargs communityId: ${community.id}\n" +
                        "args requesterId: ${member.id}")
                val communityResource = expelMemberUseCase.execute(
                    community.id, member.id
                )
                _community.postValue(
                    when (communityResource.status) {
                        Status.SUCCESS -> Resource.success(community)
                        else -> Resource.error("Something went wrong", community)
                    }
                )
            }
        }
    }


    private fun removeUser(users: MutableList<User>, requester: User) =
        users.let { list ->
            list.find { it.id == requester.id }?.let { list.remove(it) }
            list
        }

    private fun addUser(users: MutableList<User>, user: User) =
        users.let { list ->
            list.add(user)
            list
        }


    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        _community.postValue(Resource.error("$e", null))
        Log.e(MemberViewModel::class.java.simpleName, "An error happened: $e")
    }
}