package id.forum.community.domain.usecase

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.community.domain.repository.CommunityRepository
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource

class UpdateCommunityProfileUseCase(private val communityRepository: CommunityRepository) {
    fun execute(community: Community, imageUri: Uri?): LiveData<Resource<Community>> =
        communityRepository.updateCommunityProfile(community, imageUri)
}