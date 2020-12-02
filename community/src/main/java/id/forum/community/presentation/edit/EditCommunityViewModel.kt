package id.forum.community.presentation.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import id.forum.community.domain.usecase.UpdateCommunityProfileUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.vo.Address
import id.forum.core.vo.Profile

class EditCommunityViewModel(private val updateCommunityProfileUseCase: UpdateCommunityProfileUseCase) :
    BaseViewModel() {

    fun updateUser(
        community: Community,
        newName: String,
        newBio: String,
        newProvince: String,
        newCity: String,
        isPrivate: Boolean,
        imageUri: Uri?
    ): LiveData<Resource<Community>> {
        community.profile =
            Profile(name = newName, biodata = newBio, avatar = community.profile.avatar)
        community.address = Address(city = newCity, province = newProvince)
        community.isPrivate = isPrivate
        return updateCommunityProfileUseCase.execute(community, imageUri)
    }
}
