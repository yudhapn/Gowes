package id.forum.gallery.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.forum.core.base.BaseViewModel
import id.forum.core.media.domain.model.Image
import id.forum.core.media.domain.model.Media
import id.forum.core.media.domain.model.MediaList
import id.forum.gallery.domain.usecase.GetImagesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob

class ImageGalleryViewModel(
    private val mediaList: MediaList,
    private val getImagesUseCase: GetImagesUseCase
) : BaseViewModel() {
    private var supervisorJob = SupervisorJob()
    private var _mediaListSelected = mutableListOf<Image>()
    val mediaListSelected: List<Image>
        get() = _mediaListSelected

    val images: LiveData<List<Media>>
        get() = _images
    private val _images = MutableLiveData<List<Media>>()

    private val _pictures = MutableLiveData<List<Image>>()
    val pictures: LiveData<List<Image>>
        get() = _pictures

    init {
        getImages()
    }

    fun getVideos() {
//        val data = repository.loadVideos()
    }

    fun getImages() {
        Log.d("viewmodel", "size image list: ${mediaList.mediaList.size}")
        val data = getImagesUseCase.execute(mediaList.mediaList)
        _pictures.postValue(data)
    }

    fun updateImages(media: Image) {
        val imageList = _pictures.value?.toMutableList() ?: mutableListOf()
        imageList.forEachIndexed { index, it ->
            if (it.path == media.path) {
                imageList[index] = it.copy(isSelected = !media.isSelected)
                return@forEachIndexed
            }
        }
        _pictures.value = imageList
        updateMediaList(media)
    }

    fun setMediaList(media: MediaList) {
        _mediaListSelected = media.mediaList.toMutableList()
    }

    fun updateMediaList(media: Image) {
        if (_mediaListSelected.isEmpty()) {
            _mediaListSelected.add(media)
        } else {
            val removedMedia = _mediaListSelected.find { it.path == media.path }
            if (removedMedia == null) {
                _mediaListSelected.add(media)
            } else {
                Log.d("testing", "result: ${_mediaListSelected.remove(removedMedia)}")
            }
        }
    }
}