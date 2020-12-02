package id.forum.faq

import id.forum.faq.data.repository.FaqRepositoryImpl
import id.forum.faq.data.service.FaqApolloService
import id.forum.faq.domain.repository.FaqRepository
import id.forum.faq.domain.usecase.GetFaqsUseCase
import id.forum.faq.presentation.TopicViewModel
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
    viewModel { TopicViewModel(getFaqsUseCase = get()) }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetFaqsUseCase(get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { FaqRepositoryImpl() as FaqRepository }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    single { FaqApolloService(get()) }
}


@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
