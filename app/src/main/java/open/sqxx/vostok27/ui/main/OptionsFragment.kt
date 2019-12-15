package open.sqxx.vostok27.ui.main

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_options.*
import open.sqxx.vostok27.R
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.presentation.main.options.OptionsPresenter
import open.sqxx.vostok27.presentation.main.options.OptionsView
import open.sqxx.vostok27.ui.global.BaseFragment
import open.sqxx.vostok27.ui.main.view.OptionTimeView
import toothpick.Scope
import toothpick.config.Module

@ExperimentalUnsignedTypes
class OptionsFragment(val btFront: BluetoothFront) : BaseFragment(), OptionsView {

	@InjectPresenter
	lateinit var presenter: OptionsPresenter

	override val layoutRes = R.layout.fragment_options

	@ProvidePresenter
	fun providePresenter(): OptionsPresenter =
		scope.getInstance(OptionsPresenter::class.java)

	override fun installModules(scope: Scope) {
		scope.installModules(object : Module() {
			init {
				bind(OptionsPresenter::class.java)
					.toInstance(OptionsPresenter(btFront))
			}
		})
	}

	override fun onAttachFragment() = presenter.onAttachViewToReality()

	override fun onDetachFragment() = presenter.onDetachViewFromReality()

	override fun initialize() {
		freezeUi()
		bindListeners()
	}

	private fun freezeUi() {

		// Чтобы не сломать консистентность ui,
		//   замораживаем элементы управления до получения состояния на slave

		current_time.updateState(false)
		day_time.updateState(false)
		night_time.updateState(false)
	}

	private fun bindListeners() {
		current_time.onTimeChangedListener = { hour, min ->
			presenter.setCurrentTime(hour.toUInt(), min.toUInt())
		}

		day_time.onTimeChangedListener = { hour, min ->
			presenter.setDayTime(hour.toUInt(), min.toUInt())
		}

		night_time.onTimeChangedListener = { hour, min ->
			presenter.setNightTime(hour.toUInt(), min.toUInt())
		}
	}

	override fun updateCurrentTime(hour: Int, minute: Int) =
		updateTimeState(current_time, hour, minute)

	override fun updateDayTime(hour: Int, minute: Int) =
		updateTimeState(day_time, hour, minute)

	override fun updateNightTime(hour: Int, minute: Int) =
		updateTimeState(night_time, hour, minute)

	private fun updateTimeState(
		optionTimeView: OptionTimeView,
		hour: Int,
		minute: Int
	) {
		optionTimeView.updateState(true)
		optionTimeView.updateTime(hour, minute)
	}
}
