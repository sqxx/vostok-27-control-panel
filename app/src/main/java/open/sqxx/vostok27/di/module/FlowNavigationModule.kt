package open.sqxx.vostok27.di.module

import open.sqxx.vostok27.model.system.flow.FlowRouter
import ru.terrakok.cicerone.*
import toothpick.config.Module

class FlowNavigationModule(globalRouter: Router) : Module() {
	init {
		val cicerone = Cicerone.create(FlowRouter(globalRouter))
		bind(FlowRouter::class.java).toInstance(cicerone.router)
		bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)
	}
}