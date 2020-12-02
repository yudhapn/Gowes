package id.forum.gallery

import id.forum.core.media.domain.model.MediaList
import id.forum.gallery.data.repository.GalleryRepositoryImpl
import id.forum.gallery.domain.repository.GalleryRepository
import id.forum.gallery.domain.usecase.GetImagesUseCase
import id.forum.gallery.presentation.ImageGalleryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(appComponent)
}


@ExperimentalCoroutinesApi
val viewModelModule = module {
    viewModel { (mediaList: MediaList) ->
        ImageGalleryViewModel(mediaList, get())
    }
}

@ExperimentalCoroutinesApi
val useCaseModule = module {
    factory { GetImagesUseCase(get()) }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { GalleryRepositoryImpl(androidContext()) as GalleryRepository }
}


@ExperimentalCoroutinesApi
val appComponent =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule
    )
