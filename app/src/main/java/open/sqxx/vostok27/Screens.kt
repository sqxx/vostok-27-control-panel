package open.sqxx.vostok27

import open.sqxx.vostok27.ui.main.*
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

	object Main : SupportAppScreen() {
		override fun getFragment() = MainFragment()
	}

	object Sensors : SupportAppScreen() {
		override fun getFragment() = SensorsFragment()
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