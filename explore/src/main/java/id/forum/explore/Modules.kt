package id.forum.explore

import id.forum.explore.presentation.ExploreViewModel
import id.forum.explore.usecase.GetExploreCommunitiesUseCase
import id.forum.explore.usecase.GetExplorePostsUseCase
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
    viewModel { (requestDataType: Int) ->
        ExploreViewModel(
            requestDataType,
            getExploreCommunitiesUseCase = get(),
            getExplorePostsUseCase = get(),
            currentUser = get()
        )
    }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetExplorePostsUseCase(get()) }
    factory { GetExploreCommunitiesUseCase(get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule
    )
