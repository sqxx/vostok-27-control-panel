package open.sqxx.vostok27.presentation

import open.sqxx.vostok27.Screens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AppLauncher @Inject constructor(
	private val router: Router
) {

	fun onLaunch() {

	}

	fun coldStart() {
		router.newRootScreen(Screens.Main)
	}
}