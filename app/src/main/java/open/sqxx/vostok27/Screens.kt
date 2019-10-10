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
		lateinit var bluetoothFront: BluetoothFront
		override fun getFragment() = ChartFragment(bluetoothFront)
	}

	object Switchers : SupportAppScreen() {
		lateinit var bluetoothFront: BluetoothFront
		override fun getFragment() = SwitchersFragment(bluetoothFront)
	}

	object Options : SupportAppScreen() {
		lateinit var bluetoothFront: BluetoothFront
		override fun getFragment() = OptionsFragment(bluetoothFront)
	}
}