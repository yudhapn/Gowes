package id.forum.profile

import id.forum.core.user.domain.model.User
import id.forum.profile.data.repository.ProfileRepositoryImpl
import id.forum.profile.data.service.ProfileApolloService
import id.forum.profile.domain.repository.ProfileRepository
import id.forum.profile.domain.usecase.GetUserProfileUseCase
import id.forum.profile.domain.usecase.UpdateUserProfileUseCase
import id.forum.profile.presentation.ProfileViewModel
import id.forum.profile.presentation.edit.EditProfileViewModel
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
    viewModel { (profileUser: User) ->
        ProfileViewModel(
            getUserProfileUseCase = get(),
            profileUser = profileUser,
            currentUser = get()
        )
    }
    viewModel {
        EditProfileViewModel(get(), get())
    }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { ProfileRepositoryImpl() as ProfileRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetUserProfileUseCase(get()) }
    factory { UpdateUserProfileUseCase(get()) }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { ProfileApolloService(get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
