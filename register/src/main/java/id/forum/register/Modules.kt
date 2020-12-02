package id.forum.register

import id.forum.register.data.repository.RegisterRepositoryImpl
import id.forum.register.data.service.RegisterRealmService
import id.forum.register.domain.repository.RegisterRepository
import id.forum.register.domain.usecase.RegisterByEmailUseCase
import id.forum.register.domain.usecase.SetupUserUseCase
import id.forum.register.domain.usecase.UpdateTokenCacheUseCase
import id.forum.register.presentation.RegisterViewModel
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
    viewModel { RegisterViewModel(get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { RegisterRepositoryImpl() as RegisterRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { RegisterByEmailUseCase(get()) }
    factory { SetupUserUseCase(get()) }
    factory { UpdateTokenCacheUseCase(get()) }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { RegisterRealmService(get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
