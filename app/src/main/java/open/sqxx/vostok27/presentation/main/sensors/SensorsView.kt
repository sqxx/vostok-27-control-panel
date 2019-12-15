package open.sqxx.vostok27.presentation.main.sensors

import com.arellomobile.mvp.MvpView

@ExperimentalUnsignedTypes
interface SensorsView : MvpView {
	fun showCO2(value: Int)
	fun showTemp(value: Int)
	fun showHumidity(value: Int)
	fun showPressure(value: Float)

	fun showVoltage(value: Float)
	fun showEnergyUsage(value: Float)
	fun showEnergyGen(value: Int)

	fun reset()
}