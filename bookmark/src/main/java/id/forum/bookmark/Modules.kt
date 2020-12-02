package id.forum.bookmark

import id.forum.bookmark.domain.usecase.GetBookmarkPostsUseCase
import id.forum.bookmark.presentation.BookmarkViewModel
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
    viewModel { BookmarkViewModel(getBookmarkPostsUseCase = get()) }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetBookmarkPostsUseCase(get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule
    )
