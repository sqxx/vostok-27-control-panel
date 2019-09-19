package open.sqxx.vostok27

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import open.sqxx.vostok27.di.DI
import open.sqxx.vostok27.di.module.AppModule
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration

class App : Application() {

	override fun onCreate() {
		super.onCreate()

		initLogger()
		initToothpick()
		initAppScope()
		initThreetenABP()
	}

	private fun initLogger() {
		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		}
	}

	private fun initToothpick() {
		if (BuildConfig.DEBUG) {
			Toothpick.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
		} else {
			Toothpick.setConfiguration(Configuration.forProduction())
		}
	}

	private fun initAppScope() {
		Toothpick
			.openScope(DI.APP_SCOPE)
			.installModules(AppModule(this))
	}

	private fun initThreetenABP() {
		AndroidThreeTen.init(this)
	}
}