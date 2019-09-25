package open.sqxx.vostok27.presentation

import open.sqxx.vostok27.Screens
import open.sqxx.vostok27.model.repository.BluetoothFront
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class AppLauncher @Inject constructor(
	private val router: Router
) {

	fun onLaunch() {

	}

	fun coldStart(btFront: BluetoothFront) {
		Screens.Main.bluetoothFront = btFront

		router.newRootScreen(Screens.Main)
	}
}