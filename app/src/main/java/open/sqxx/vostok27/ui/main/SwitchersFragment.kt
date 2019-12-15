package open.sqxx.vostok27.ui.main

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_switchers.*
import open.sqxx.vostok27.R
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.presentation.main.switchers.SwitchersPresenter
import open.sqxx.vostok27.presentation.main.switchers.SwitchersView
import open.sqxx.vostok27.ui.global.BaseFragment
import open.sqxx.vostok27.ui.main.view.OptionSwitcherView
import toothpick.Scope
import toothpick.config.Module
import kotlin.math.roundToInt

@ExperimentalUnsignedTypes
class SwitchersFragment(val btFront: BluetoothFront) : BaseFragment(), SwitchersView {

	@InjectPresenter
	lateinit var presenter: SwitchersPresenter

	override val layoutRes = R.layout.fragment_switchers

	@ProvidePresenter
	fun providePresenter(): SwitchersPresenter =
		scope.getInstance(SwitchersPresenter::class.java)

	override fun installModules(scope: Scope) {
		scope.installModules(object : Module() {
			init {
				bind(SwitchersPresenter::class.java)
					.toInstance(SwitchersPresenter(btFront))
			}
		})
	}

	override fun initialize() {
		freezeUi()
		bindListeners()
	}

	private fun freezeUi() {

		// Чтобы не сломать консистентность ui,
		//   замораживаем элементы управления до получения состояния на slave

		pressure_relief_valve_switcher.updateState(false)
		pump_valve_switcher.updateState(false)
		prod_co2_switcher.updateState(false)

		light_level.isEnabled = false
		auto_light_switcher.updateState(false)
	}

	private fun bindListeners() {
		pressure_relief_valve_switcher.onToggleListener = {
			presenter.setPressureReliefValveState(it)
		}

		pump_valve_switcher.onToggleListener = {
			presenter.setPumpValveState(it)
		}

		prod_co2_switcher.onToggleListener = {
			presenter.setProdCO2State(it)
		}

		light_level.setOnChangeListener { _, value ->
			presenter.setLightLevel(value.roundToInt().toUInt())
		}

		auto_light_switcher.onToggleListener = {
			presenter.setAutoLightState(it)
		}
	}

	override fun updatePressureReliefValveState(isEnabled: Boolean) =
		updateSwitcherState(pressure_relief_valve_switcher, isEnabled)

	override fun updatePumpValveState(isEnabled: Boolean) =
		updateSwitcherState(pump_valve_switcher, isEnabled)

	override fun updateProdCO2State(isEnabled: Boolean) =
		updateSwitcherState(prod_co2_switcher, isEnabled)

	override fun updateAutoLightState(isEnabled: Boolean) {
		light_level.isEnabled = !isEnabled
		updateSwitcherState(auto_light_switcher, isEnabled)
	}

	private fun updateSwitcherState(
		optionSwitcherView: OptionSwitcherView,
		isEnabled: Boolean
	) {
		optionSwitcherView.updateState(true)
		optionSwitcherView.updateCheckedState(isEnabled)
	}
}