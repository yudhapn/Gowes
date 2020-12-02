package id.forum.chat

import id.forum.chat.data.repository.ChatRepositoryImpl
import id.forum.chat.data.service.ChatApolloService
import id.forum.chat.domain.repository.ChatRepository
import id.forum.chat.domain.usecase.*
import id.forum.chat.presentation.ChatViewModel
import id.forum.chat.presentation.create.CreateChatViewModel
import id.forum.chat.presentation.room.ChatRoomViewModel
import id.forum.core.user.domain.model.User
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
val serviceModule = module {
    factory { ChatApolloService(get(), get()) }
}

@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { ChatViewModel(get()) }
    viewModel { (chatId: String, receiver: User) ->
        ChatRoomViewModel(
            getMessagesUseCase = get(),
            createMessageUseCase = get(),
            createChatUseCase = get(),
            chatId = chatId,
            receiver = receiver
        )
    }
    viewModel { CreateChatViewModel(get(), get()) }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetMembersCommunitiesUseCase(get()) }
    factory { GetChatsUseCase(get()) }
    factory { GetMessagesUseCase(get()) }
    factory { CreateMessageUseCase(get()) }
    factory { CreateChatUseCase(get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { ChatRepositoryImpl() as ChatRepository }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        serviceModule
    )
