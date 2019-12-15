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
	lateinit var presenter: SensorsPresenter

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

	override fun onAttachFragment() = presenter.onAttachViewToReality()

	override fun onDetachFragment() = presenter.onDetachViewFromReality()

	override fun showCO2(value: Int) {
		co2_sensor.updateValue(value)
	}

	override fun showTemp(value: Int) {
		temp_sensor.updateValue(value)
	}

	override fun showHumidity(value: Int) {
		hum_sensor.updateValue(value)
	}

	override fun showPressure(value: Float) {
		pres_sensor.updateValue(value)
	}

	override fun showVoltage(value: Float) {
		battery_voltage.updateValue(value)
	}

	override fun showEnergyUsage(value: Float) {
		usage.updateValue(value)
	}

	override fun showEnergyGen(value: Int) {
		generate.updateValue(value)
	}

	override fun reset() {
		co2_sensor.reset()
		temp_sensor.reset()
		hum_sensor.reset()
		pres_sensor.reset()

		battery_voltage.reset()
		usage.reset()
		generate.reset()
	}
}