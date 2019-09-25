package open.sqxx.vostok27.presentation.main.sensors

import com.arellomobile.mvp.MvpView

interface SensorsView : MvpView {
	fun showCO2(value: Int)
	fun showTemp(value: Int)
	fun showHumidity(value: Int)
	fun showPressure(value: Float)

	fun showMessage(msg: String)
	fun removeMessage()
}