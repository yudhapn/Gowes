package id.forum.community.data.service

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.google.firebase.storage.FirebaseStorage
import id.forum.core.*
import id.forum.core.community.data.mapper.bindMembers
import id.forum.core.community.data.mapper.bindPosts
import id.forum.core.community.data.mapper.mapToDomain
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.type.*
import id.forum.core.user.domain.model.User
import id.forum.core.util.apolloResponseFetchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.*

@ExperimentalCoroutinesApi
class CommunityApolloService(
    private val currentUser: User,
    private val token: Token,
    private val storage: FirebaseStorage
) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    fun getCommunity(communityId: String, isRefresh: Boolean): Flow<Community> = apolloClient
        .query(CommunityQuery(Input.fromNullable(communityId)))
        .responseFetcher(isRefresh.apolloResponseFetchers())
        .watcher()
        .toFlow()
        .map { response ->
            response.data?.community?.let {
                val community = it.fragments.communityDetails.mapToDomain()
                    .bindMembers(response.data?.members)
                    .bindPosts(response.data?.posts, currentUser)
                community
            } ?: Community()
        }

    suspend fun updateMember(
        communityId: String,
        userId: String,
        isJoin: Boolean,
        isRequest: Boolean,
        isCancel: Boolean = false,
        answer: String = ""
    ): Resource<Community> {
        try {
            val mutation = if (isCancel) {
                CancelRequestMemberMutation(
                    communityId = Input.fromNullable(communityId),
                    userId = Input.fromNullable(userId)
                )
            } else {
                UpdateMemberMutation(
                    communityId = Input.fromNullable(communityId),
                    userId = Input.fromNullable(userId),
                    input = MemberInsertInput(
                        community = Input.fromNullable(
                            MemberCommunityRelationInput(
                                link = Input.fromNullable(communityId)
                            )
                        ),
                        user = Input.fromNullable(
                            MemberUserRelationInput(
                                link = Input.fromNullable(userId)
                            )
                        ),
                        isAdmin = Input.fromNullable(false),
                        isJoin = Input.fromNullable(isJoin),
                        isRequest = Input.fromNullable(isRequest),
                        joinedOn = Input.fromNullable(Calendar.getInstance().time),
                        surveyAnswer = Input.fromNullable(answer)
                    )
                )

            }
            val response = apolloClient
                .mutate(mutation)
                .toDeferred().await()

            if (response.hasErrors()) {
                return Resource.error(
                    response.errors?.get(0)?.message ?: "Something wrong, please try again later",
                    null
                )
            }
            return Resource.success(null)
        } catch (e: ApolloException) {
            throw Exception("CommunityApolloService, error happened: ${e.message}")
        }
    }

    fun isCommunityAvatarExist(
        community: Community,
        imageUri: Uri?,
        isCreate: Boolean
    ): LiveData<Resource<Community>> {
        val result = MutableLiveData<Resource<Community>>()
        result.postValue(Resource.loading(null))
        val storageRef = storage.getReference("gowes_forum")
        val fileReference = storageRef.child(community.id + "-avatar")
        if (imageUri != null) {
            val uploadTask = fileReference.putFile(imageUri)
            uploadTask.continueWithTask {
                if (!it.isSuccessful) throw it.exception?.cause!!
                fileReference.downloadUrl
            }.addOnCompleteListener { taskUri ->
                if (taskUri.isSuccessful) {
                    val imageUrl = taskUri.result.toString()
                    community.profile.avatar = imageUrl
                    CoroutineScope(Dispatchers.IO).launch {
                        val communityResource = if (isCreate) {
                            createCommunity(community)
                        } else {
                            updateProfile(community)
                        }
                        result.postValue(communityResource)
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val communityResource = if (isCreate) {
                    createCommunity(community)
                } else {
                    updateProfile(community)
                }
                result.postValue(communityResource)
            }
        }
        return result
    }

    private suspend fun updateProfile(community: Community): Resource<Community> {
        val mutation = UpdateCommunityMutation(
            Input.fromNullable(community.id),
            CommunityUpdateInput(
                avatar = Input.fromNullable(community.profile.avatar),
                name = Input.fromNullable(community.profile.name),
                biodata = Input.fromNullable(community.profile.biodata),
                province = Input.fromNullable(community.address.province),
                city = Input.fromNullable(community.address.city),
                isPrivate = Input.fromNullable(community.isPrivate)
            )
        )
        try {
            val response = apolloClient
                .mutate(mutation)
                .refetchQueries(CommunityQuery(Input.fromNullable(community.id)))
                .toDeferred().await()
            if (response.hasErrors()) {
                return Resource.error("${response.errors}", community)
            }
            response.data?.updateOneCommunity?.fragments?.communityDetails?.let {
                return Resource.success(community)
            } ?: run {
                throw IllegalStateException("unknown error in CommunityApolloService")
            }
        } catch (e: ApolloException) {
            throw Exception("CommunityApolloService, error happened: ${e.message}")
        }
    }

    private suspend fun createCommunity(community: Community): Resource<Community> {
        val mutation = CreateCommunityMutation(
            CommunityInsertInput(
                avatar = Input.fromNullable(community.profile.avatar),
                name = Input.fromNullable(community.profile.name),
                biodata = Input.fromNullable(community.profile.biodata),
                type = Input.fromNullable(community.type),
                province = Input.fromNullable(community.address.province),
                city = Input.fromNullable(community.address.city),
                isPrivate = Input.fromNullable(false),
                memberCount = Input.fromNullable(0)
            )
        )
        try {
            val response = apolloClient
                .mutate(mutation)
                .refetchQueries(CommunityQuery(Input.fromNullable(community.id)))
                .toDeferred().await()
            if (response.hasErrors()) {
                return Resource.error("${response.errors}", community)
            }
            response.data?.insertOneCommunity?.fragments?.communityDetails?.let {
                return Resource.success(community)
            } ?: run {
                throw IllegalStateException("unknown error in CommunityApolloService")
            }
        } catch (e: ApolloException) {
            throw Exception("CommunityApolloService, error happened: ${e.message}")
        }
    }
}