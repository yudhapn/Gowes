package id.forum.post.domain.repository

import androidx.lifecycle.MutableLiveData
import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image
import id.forum.core.post.domain.model.Post

interface PostRepository {
    fun createPost(post: Post, mediaList: List<Image>): MutableLiveData<Resource<Post>>
}