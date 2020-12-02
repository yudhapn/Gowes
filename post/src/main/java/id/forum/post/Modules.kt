package id.forum.post

import id.forum.core.post.domain.model.Post
import id.forum.post.data.repository.CommentsRepositoryImpl
import id.forum.post.data.repository.PostRepositoryImpl
import id.forum.post.data.service.CommentApolloService
import id.forum.post.data.service.PostApolloService
import id.forum.post.domain.repository.CommentsRepository
import id.forum.post.domain.repository.PostRepository
import id.forum.post.domain.usecase.*
import id.forum.post.presentation.PostViewModel
import id.forum.post.presentation.comment.CreateCommentViewModel
import id.forum.post.presentation.create.CreatePostViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

@ExperimentalCoroutinesApi
fun injectFeature() = loadFeature

@ExperimentalCoroutinesApi
private val loadFeature by lazy {
    loadKoinModules(appComponent)
}

@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { (post: Post) ->
        PostViewModel(
            getCommentsPostUseCase = get(),
            deleteCommentUseCase = get(),
            voteCommentUseCase = get(),
            postArg = post,
            currentUser = get()
        )
    }
    viewModel { CreatePostViewModel(get(), get(), get()) }
    viewModel { CreateCommentViewModel(get(), get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { CommentsRepositoryImpl() as CommentsRepository }
    single { PostRepositoryImpl() as PostRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetCommentsPostUseCase(get()) }
    factory { DeleteCommentUseCase(get()) }
    factory { VoteCommentUseCase(get()) }
    factory { CreatePostUseCase(get()) }
    factory { CreateCommentsPostUseCase(get()) }
    factory { CompressMediaUseCase(get()) }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { CommentApolloService(get(), get()) }
    factory { PostApolloService(get(), get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
