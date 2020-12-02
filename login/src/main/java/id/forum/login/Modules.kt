package id.forum.login

import id.forum.login.data.repository.LoginRepositoryImpl
import id.forum.login.data.service.LoginRealmService
import id.forum.login.domain.repository.LoginRepository
import id.forum.login.domain.usecase.LoginByEmailUseCase
import id.forum.login.domain.usecase.ResetPasswordUseCase
import id.forum.login.presentation.LoginViewModel
import id.forum.login.presentation.reset.PasswordResetViewModel
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
    viewModel { LoginViewModel(loginByEmailUseCase = get()) }
    viewModel { PasswordResetViewModel(resetPasswordUseCase = get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { LoginRepositoryImpl(get()) as LoginRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { LoginByEmailUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { LoginRealmService(get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
