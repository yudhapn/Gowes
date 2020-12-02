package id.forum.community

import id.forum.community.data.repository.CommunityRepositoryImpl
import id.forum.community.data.service.CommunityApolloService
import id.forum.community.domain.repository.CommunityRepository
import id.forum.community.domain.usecase.*
import id.forum.community.presentation.CommunityViewModel
import id.forum.community.presentation.create.CreateCommunityViewModel
import id.forum.community.presentation.edit.EditCommunityViewModel
import id.forum.core.community.domain.model.Community
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
    viewModel { (community: Community) ->
        CommunityViewModel(
            getCommunityProfileUseCase = get(),
            joinCommunityUseCase = get(),
            requestJoinCommunityUseCase = get(),
            cancelRequestJoinCommunityUseCase = get(),
            leaveCommunityUseCase = get(),
            communityArgs = community,
            currentUser = get(),
            updateAccountCacheUseCase = get()
        )
    }

    viewModel { EditCommunityViewModel(get()) }
    viewModel { CreateCommunityViewModel(get()) }

}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { CommunityRepositoryImpl() as CommunityRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetCommunityProfileUseCase(get()) }
    factory { JoinCommunityUseCase(get()) }
    factory { RequestJoinCommunityUseCase(get()) }
    factory { CancelRequestJoinCommunityUseCase(get()) }
    factory { LeaveCommunityUseCase(get()) }
    factory { UpdateCommunityProfileUseCase(get()) }
    factory { CreateCommunityUseCase(get()) }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { CommunityApolloService(get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
