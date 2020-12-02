package id.forum.community.presentation.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import id.forum.community.domain.usecase.CreateCommunityUseCase
import id.forum.core.base.BaseViewModel
import id.forum.core.community.domain.model.Community
import id.forum.core.data.Resource
import id.forum.core.vo.Address
import id.forum.core.vo.Profile

class CreateCommunityViewModel(private val createCommunityUseCase: CreateCommunityUseCase) :
    BaseViewModel() {

    private var _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    fun createCommunity(
        name: String, type: String, bio: String, province: String, city: String, imageUri: Uri?
    ): LiveData<Resource<Community>> {
        val successMessage = "Waiting for admin's approval"
        val failedMessage = "Name, province, and city cannot be blank!"
        return if (name.isEmpty() || province.isEmpty() || city.isEmpty()) {
            _message.postValue(failedMessage)
            liveData { Resource.error(failedMessage, null) }
        } else {
            val newCommunity = Community(
                profile = Profile(name = name, biodata = bio),
                address = Address(city = city, province = province),
                type = type
            )
            _message.postValue(successMessage)
            createCommunityUseCase.execute(newCommunity, imageUri)
        }
    }
}
