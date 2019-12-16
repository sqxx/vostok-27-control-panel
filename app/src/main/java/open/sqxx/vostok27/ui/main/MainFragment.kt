package open.sqxx.vostok27.ui.main

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.fragment_main.*
import open.sqxx.vostok27.R
import open.sqxx.vostok27.Screens
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.ui.global.BaseFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

@ExperimentalUnsignedTypes
class MainFragment(btFront: BluetoothFront) : BaseFragment() {

	companion object {
		private val sensorsScreen = Screens.Sensors
		private val switchersScreen = Screens.Switchers
		private val optionsScreen = Screens.Options

		private val defaultScreen = sensorsScreen
	}

	override val layoutRes = R.layout.fragment_main

	private var prevScreenFragment: BaseFragment? = null
	private val currentScreenFragment: BaseFragment?
		get() = childFragmentManager.fragments.firstOrNull { !it.isHidden } as? BaseFragment

	private fun getScreen(tab: TabLayout.Tab?) =
		when (tab!!.position) {
			0    -> sensorsScreen
			1    -> switchersScreen
			2    -> optionsScreen
			else -> defaultScreen
		}

	init {
		btFront.let {
			sensorsScreen.bluetoothFront = it
			switchersScreen.bluetoothFront = it
			optionsScreen.bluetoothFront = it
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setupNavigation()
	}

	override fun onBackPressed() {
		currentScreenFragment?.onBackPressed()
	}

	private fun setupNavigation() {
		tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
			override fun onTabReselected(tab: TabLayout.Tab?) {
			}

			override fun onTabUnselected(tab: TabLayout.Tab?) {
			}

			override fun onTabSelected(tab: TabLayout.Tab?) {
				val currentScreen = getScreen(tab)
				selectScreen(currentScreen)
			}
		})

		//tabs.selectScreen(tabs.getTabAt(0))
		selectScreen(defaultScreen)
	}

	private fun selectScreen(screen: SupportAppScreen) {
		val newFragment = childFragmentManager.findFragmentByTag(screen.screenKey)

		if (currentScreenFragment != null &&
			newFragment != null &&
			currentScreenFragment == newFragment
		) return

		childFragmentManager.beginTransaction().apply {
			if (newFragment == null) {
				add(
					R.id.mainScreenContainer,
					screen.fragment,
					screen.screenKey
				)
			}

			currentScreenFragment?.let {
				hide(it)
				prevScreenFragment = it
				it.userVisibleHint = false
			}

			newFragment?.let {
				show(it)
				it.userVisibleHint = true
			}
		}.commitNow()
		childFragmentManager.executePendingTransactions()

		prevScreenFragment?.onDetachFragment()
		currentScreenFragment?.onAttachFragment()
	}
}