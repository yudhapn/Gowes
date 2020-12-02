package id.forum.member

import id.forum.member.data.repository.MemberRepositoryImpl
import id.forum.member.data.service.MemberApolloService
import id.forum.member.domain.repository.MemberRepository
import id.forum.member.domain.usecase.AcceptRequestMemberUseCase
import id.forum.member.domain.usecase.AppointMemberAsAdminUseCase
import id.forum.member.domain.usecase.ExpelMemberUseCase
import id.forum.member.domain.usecase.RejectRequestMemberUseCase
import id.forum.member.presentation.MemberViewModel
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
    viewModel { MemberViewModel(get(), get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { AcceptRequestMemberUseCase(get()) }
    factory { RejectRequestMemberUseCase(get()) }
    factory { AppointMemberAsAdminUseCase(get()) }
    factory { ExpelMemberUseCase(get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { MemberRepositoryImpl() as MemberRepository }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    single { MemberApolloService(get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
