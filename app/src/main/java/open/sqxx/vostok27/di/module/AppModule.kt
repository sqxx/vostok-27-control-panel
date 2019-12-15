package open.sqxx.vostok27.di.module

import android.content.Context
import android.content.res.AssetManager
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import open.sqxx.vostok27.BuildConfig
import open.sqxx.vostok27.di.*
import open.sqxx.vostok27.entity.app.develop.AppInfo
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.tools.Base64Tools
import open.sqxx.vostok27.model.system.*
import open.sqxx.vostok27.model.system.message.SystemMessageNotifier
import ru.terrakok.cicerone.*
import toothpick.config.Module

class AppModule(context: Context) : Module() {
	init {
		// Global
		bind(Context::class.java).toInstance(context)

		bind(PrimitiveWrapper::class.java)
			.withName(DefaultPageSize::class.java)
			.toInstance(PrimitiveWrapper(20))

		bind(PrimitiveWrapper::class.java)
			.withName(CacheLifetime::class.java)
			.toInstance(PrimitiveWrapper(300_000L))

		bind(SchedulersProvider::class.java).toInstance(AppSchedulers())
		bind(ResourceManager::class.java).singleton()
		bind(Base64Tools::class.java).toInstance(Base64Tools())
		bind(AssetManager::class.java).toInstance(context.assets)
		bind(SystemMessageNotifier::class.java).toInstance(SystemMessageNotifier())

		bind(BluetoothSPP::class.java).toInstance(BluetoothSPP(context))
		bind(BluetoothFront::class.java).toInstance(BluetoothFront())

		// Navigation
		Cicerone.create().apply {
			bind(Router::class.java).toInstance(router)
			bind(NavigatorHolder::class.java).toInstance(navigatorHolder)
		}

		// AppInfo
		bind(AppInfo::class.java).toInstance(
			AppInfo(
				BuildConfig.VERSION_NAME,
				BuildConfig.VERSION_CODE,
				BuildConfig.APP_DESCRIPTION,
				BuildConfig.VERSION_UID.take(8),
				BuildConfig.APP_HOME_PAGE,
				BuildConfig.FEEDBACK_URL
			)
		)
	}
}