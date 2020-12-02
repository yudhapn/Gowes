package id.forum.gowes

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@ExperimentalCoroutinesApi
open class GowesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        App(AppConfiguration.Builder("gowesforumandroid-klwks").build())
        startKoin {
            androidContext(this@GowesApp)
            modules(appComponent)
        }
        val nightMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}