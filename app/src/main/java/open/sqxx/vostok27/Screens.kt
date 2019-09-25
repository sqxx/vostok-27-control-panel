package open.sqxx.vostok27

import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.ui.main.*
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

	object Main : SupportAppScreen() {
		lateinit var bluetoothFront: BluetoothFront

		override fun getFragment() = MainFragment(bluetoothFront)
	}

	object Sensors : SupportAppScreen() {
		lateinit var bluetoothFront: BluetoothFront

		override fun getFragment() = SensorsFragment(bluetoothFront)
	}

	object Chart : SupportAppScreen() {
		override fun getFragment() = ChartFragment()
	}

	object Switchers : SupportAppScreen() {
		override fun getFragment() = SwitchersFragment()
	}

	object Options : SupportAppScreen() {
		override fun getFragment() = OptionsFragment()
	}
}