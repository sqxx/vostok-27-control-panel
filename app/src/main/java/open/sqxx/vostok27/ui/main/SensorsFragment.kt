package open.sqxx.vostok27.ui.main

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_sensors.*
import open.sqxx.vostok27.R
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.presentation.main.sensors.SensorsPresenter
import open.sqxx.vostok27.presentation.main.sensors.SensorsView
import open.sqxx.vostok27.ui.global.BaseFragment
import toothpick.Scope
import toothpick.config.Module

@ExperimentalUnsignedTypes
class SensorsFragment(val btFront: BluetoothFront) : BaseFragment(), SensorsView {

	@InjectPresenter
	lateinit var sensorsPresenter: SensorsPresenter

	override val layoutRes = R.layout.fragment_sensors

	@ProvidePresenter
	fun providePresenter(): SensorsPresenter =
		scope.getInstance(SensorsPresenter::class.java)

	override fun installModules(scope: Scope) {
		scope.installModules(object : Module() {
			init {
				bind(SensorsPresenter::class.java)
					.toInstance(SensorsPresenter(btFront))
			}
		})
	}

	override fun showCO2(value: Int) {
		co2_sensor.setValue(value.toString())
	}

	override fun showTemp(value: Int) {
		temp_sensor.setValue(value.toString())
	}

	override fun showHumidity(value: Int) {
		hum_sensor.setValue(value.toString())
	}

	override fun showPressure(value: Float) {
		pres_sensor.setValue(String.format("%.2f", value))
	}

	override fun showMessage(msg: String) {
		message.text = msg
	}

	override fun removeMessage() {
		message.text = ""
	}
}