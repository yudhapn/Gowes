package id.forum.gowes

import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import id.forum.core.account.data.repository.AccountRepositoryImpl
import id.forum.core.account.data.service.AccountApolloService
import id.forum.core.account.domain.repository.AccountRepository
import id.forum.core.account.domain.usecase.AuthenticateUseCase
import id.forum.core.account.domain.usecase.UpdateAccountCacheUseCase
import id.forum.core.account.presentation.UserAccountViewModel
import id.forum.core.community.data.repository.CommunityRepositoryImpl
import id.forum.core.community.data.service.CommunityApolloService
import id.forum.core.community.domain.repository.CommunityRepository
import id.forum.core.data.Token
import id.forum.core.media.data.repository.GalleryRepositoryImpl
import id.forum.core.media.domain.repository.GalleryRepository
import id.forum.core.network.*
import id.forum.core.post.data.repository.PostRepositoryImpl
import id.forum.core.post.data.service.PostApolloService
import id.forum.core.post.domain.repository.PostRepository
import id.forum.core.post.domain.usecase.BookmarkPostUseCase
import id.forum.core.post.domain.usecase.DeletePostUseCase
import id.forum.core.post.domain.usecase.GetExplorePostsUseCase
import id.forum.core.post.domain.usecase.VotePostUseCase
import id.forum.core.post.presentation.PostViewModel
import id.forum.core.user.domain.model.User
import id.forum.core.util.SHARED_TKN
import id.forum.core.util.SHARED_USR
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountDataModule = module {
    factory {
        val sharedPref = androidApplication().getSharedPreferences(SHARED_TKN, MODE_PRIVATE)
        val tknJson = sharedPref.getString(SHARED_TKN, "")
        tknJson.let {
            Gson().fromJson(tknJson, Token::class.java)
        } ?: Token()
    }
    factory {
        val sharedPref = androidApplication().getSharedPreferences(SHARED_TKN, MODE_PRIVATE)
        val userJson = sharedPref.getString(SHARED_USR, "")
        userJson.let {
            Gson().fromJson(userJson, User::class.java)
        } ?: User()
    }

    factory {
        val prefEditor = androidApplication().getSharedPreferences(SHARED_TKN, MODE_PRIVATE).edit()
        prefEditor
    }
}

// No need injecting twice if it has injected when the app run for the first time

val networkModule = module {
    single { getRealmClient() }
    single { getFileApolloCache(androidContext()) }
    single { getApolloClientBuilder(androidContext()) }
    factory { getApolloClient(getHttpClient(token = get(), context = androidContext()), get()) }
}

val firebaseModule = module {
    factory { FirebaseStorage.getInstance() }
    factory { FirebaseAuth.getInstance() }
}

@ExperimentalCoroutinesApi
val serviceModule = module {
    factory { PostApolloService(get(), get()) }
    factory { CommunityApolloService(get(), get()) }
    factory { AccountApolloService(get(), get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { PostRepositoryImpl() as PostRepository }
    single { AccountRepositoryImpl(get(), get()) as AccountRepository }
    single { CommunityRepositoryImpl() as CommunityRepository }
    single { GalleryRepositoryImpl(androidContext()) as GalleryRepository }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { AuthenticateUseCase(get()) }
    factory { UpdateAccountCacheUseCase(get()) }
    factory { VotePostUseCase(get()) }
    factory { DeletePostUseCase(get()) }
    factory { BookmarkPostUseCase(get()) }
    factory { GetExplorePostsUseCase(get()) }
}

@ExperimentalCoroutinesApi
val baseViewModelModule = module {
    viewModel {
        PostViewModel(
            currentUser = get(),
            votePostUseCase = get(),
            deletePostUseCase = get(),
            bookmarkPostUseCase = get()
        )
    }
    viewModel { UserAccountViewModel(get(), get(), get()) }
}

@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        accountDataModule,
        networkModule,
        serviceModule,
        repositoryModule,
        useCaseModule,
        firebaseModule,
        baseViewModelModule
    )