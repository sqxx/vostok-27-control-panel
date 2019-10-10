package open.sqxx.vostok27.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import open.sqxx.vostok27.R
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.presentation.main.switchers.SwitchersPresenter
import open.sqxx.vostok27.presentation.main.switchers.SwitchersView
import open.sqxx.vostok27.ui.global.BaseFragment
import toothpick.Scope
import toothpick.config.Module

class SwitchersFragment(val btFront: BluetoothFront) : BaseFragment(), SwitchersView {

	@InjectPresenter
	lateinit var switchersPresenter: SwitchersPresenter

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