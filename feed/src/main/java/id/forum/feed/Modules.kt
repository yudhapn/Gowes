package id.forum.feed

import id.forum.feed.domain.usecase.GetFeedPostsUseCase
import id.forum.feed.presentation.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(appComponent)
}


@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { HomeViewModel(getFeedPostsUseCase = get(), currentUser = get()) }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetFeedPostsUseCase(get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule
    )
