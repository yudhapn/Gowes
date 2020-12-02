package id.forum.member.data.service

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import id.forum.core.UpdateMemberMutation
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.data.Token
import id.forum.core.type.MemberCommunityRelationInput
import id.forum.core.type.MemberInsertInput
import id.forum.core.type.MemberUserRelationInput
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class MemberApolloService(private val token: Token) : KoinComponent {
    private val apolloClient: ApolloClient by inject { parametersOf(token) }

    suspend fun updateMember(
        communityId: String,
        userId: String,
        isAdmin: Boolean = false,
        isJoin: Boolean,
        isRequest: Boolean
    ): Resource<Community> {
        try {
            Log.d(
                "MemberViewModel",
                "rejectMember method is called\nargs communityId: $communityId\n" +
                        "args requesterId: $userId"
            )
            val mutation =
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
                        isAdmin = Input.fromNullable(isAdmin),
                        isJoin = Input.fromNullable(isJoin),
                        isRequest = Input.fromNullable(isRequest),
                        joinedOn = Input.fromNullable(Calendar.getInstance().time)
                    )
                )

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
            throw Exception("PostApolloService apollo, error happened: ${e.message}")
        }
    }

}