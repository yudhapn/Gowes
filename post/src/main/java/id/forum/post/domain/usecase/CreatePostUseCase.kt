package id.forum.post.domain.usecase

import androidx.lifecycle.MutableLiveData
import id.forum.core.data.Resource
import id.forum.core.media.domain.model.Image
import id.forum.core.post.domain.model.Post
import id.forum.post.domain.repository.PostRepository

class CreatePostUseCase(private val postRepository: PostRepository) {
    fun execute(post: Post, mediaList: List<Image>): MutableLiveData<Resource<Post>> =
        postRepository.createPost(post, mediaList)
}