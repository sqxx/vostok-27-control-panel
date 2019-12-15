package open.sqxx.vostok27.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import open.sqxx.vostok27.R
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.presentation.main.chart.OptionsPresenter
import open.sqxx.vostok27.presentation.main.chart.OptionsView
import open.sqxx.vostok27.ui.global.BaseFragment
import toothpick.Scope
import toothpick.config.Module

@ExperimentalUnsignedTypes
class OptionsFragment(val btFront: BluetoothFront) : BaseFragment(), OptionsView {

	@InjectPresenter
	lateinit var switchersPresenter: OptionsPresenter

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

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val layout = super.onCreateView(inflater, container, savedInstanceState)

		setListeners(layout)

		return layout
	}

	private fun setListeners(layout: View) {

	}
}
