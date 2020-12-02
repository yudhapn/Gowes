package id.forum.search

import id.forum.search.data.repository.SearchRepositoryImpl
import id.forum.search.data.service.SearchApolloService
import id.forum.search.domain.repository.SearchRepository
import id.forum.search.domain.usecase.SearchCommunitiesUseCase
import id.forum.search.domain.usecase.SearchPostsUseCase
import id.forum.search.presentation.SearchViewModel
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
    viewModel {
        SearchViewModel(
            searchCommunitiesUseCase = get(),
            searchPostsUseCase = get(),
            currentUser = get()
        )
    }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { SearchRepositoryImpl() as SearchRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { SearchPostsUseCase(get()) }
    factory { SearchCommunitiesUseCase(get()) }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { SearchApolloService(get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
