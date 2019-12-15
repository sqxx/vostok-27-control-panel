package open.sqxx.vostok27.presentation.main.switchers

import com.arellomobile.mvp.MvpView

interface SwitchersView : MvpView {
	fun initialize()

	fun updatePressureReliefValveState(isEnabled: Boolean)
	fun updatePumpValveState(isEnabled: Boolean)
	fun updateProdCO2State(isEnabled: Boolean)
	fun updateAutoLightState(isEnabled: Boolean)
	fun updateLightLevel(value: Int)
}