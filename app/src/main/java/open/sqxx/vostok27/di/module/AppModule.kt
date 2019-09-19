package open.sqxx.vostok27.di.module

import android.content.Context
import android.content.res.AssetManager
import open.sqxx.vostok27.BuildConfig
import open.sqxx.vostok27.di.CacheLifetime
import open.sqxx.vostok27.di.DefaultPageSize
import open.sqxx.vostok27.di.PrimitiveWrapper
import open.sqxx.vostok27.entity.app.develop.AppInfo
import open.sqxx.vostok27.model.repository.tools.Base64Tools
import open.sqxx.vostok27.model.system.AppSchedulers
import open.sqxx.vostok27.model.system.ResourceManager
import open.sqxx.vostok27.model.system.SchedulersProvider
import open.sqxx.vostok27.model.system.message.SystemMessageNotifier
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
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