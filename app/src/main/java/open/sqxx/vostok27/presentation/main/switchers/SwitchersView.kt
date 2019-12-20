package open.sqxx.vostok27.presentation.main.switchers

import com.arellomobile.mvp.MvpView

interface SwitchersView : MvpView {
	fun initialize()

	fun updateCamerasState(isEnabled: Boolean)

	fun updateAutoPresState(isEnabled: Boolean)
	fun updatePressureReliefValveState(isEnabled: Boolean)
	fun updatePumpValveState(isEnabled: Boolean)

	fun updateProdCO2State(isEnabled: Boolean)
	fun updateNeutCO2State(isEnabled: Boolean)
	fun updateFanState(isEnabled: Boolean)
	fun updateHeatState(isEnabled: Boolean)

	fun updateLightLevel(value: Int)
	fun updateAutoLightState(isEnabled: Boolean)
}