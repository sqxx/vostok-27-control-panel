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
		private val sensorsTab = Screens.Sensors
		private val chartTab = Screens.Chart
		private val switchersTab = Screens.Switchers
		private val optionsTab = Screens.Options

		private val defaultTab = sensorsTab
	}

	override val layoutRes = R.layout.fragment_main

	private val currentTabFragment: BaseFragment?
		get() = childFragmentManager.fragments.firstOrNull { !it.isHidden } as? BaseFragment

	init {
		btFront.let {
			sensorsTab.bluetoothFront = it
			chartTab.bluetoothFront = it
			switchersTab.bluetoothFront = it
			optionsTab.bluetoothFront = it
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setupNavigation()
	}

	override fun onBackPressed() {
		currentTabFragment?.onBackPressed()
	}

	private fun setupNavigation() {
		tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
			override fun onTabReselected(tab: TabLayout.Tab?) {}

			override fun onTabUnselected(tab: TabLayout.Tab?) {
				//todo stop bluetooth presenter on selected tab
			}

			override fun onTabSelected(tab: TabLayout.Tab?) {
				selectTab(
					when (tab!!.position) {
						0 -> sensorsTab
						1 -> switchersTab
						2 -> optionsTab
						else -> defaultTab
					}
				)
			}

		})

		selectTab(
			when (currentTabFragment?.tag) {
				sensorsTab.screenKey -> sensorsTab
				chartTab.screenKey -> chartTab
				switchersTab.screenKey -> switchersTab
				optionsTab.screenKey -> optionsTab
				else -> defaultTab
			}
		)
	}

	private fun selectTab(tab: SupportAppScreen) {
		val currentFragment = currentTabFragment
		val newFragment = childFragmentManager.findFragmentByTag(tab.screenKey)

		if (currentFragment != null &&
			newFragment != null &&
			currentFragment == newFragment
		) return

		childFragmentManager.beginTransaction().apply {
			if (newFragment == null)
				add(
					R.id.mainScreenContainer,
					createTabFragment(tab),
					tab.screenKey
				)

			currentFragment?.let {
				hide(it)
				it.userVisibleHint = false
			}

			newFragment?.let {
				show(it)
				it.userVisibleHint = true
			}
		}.commitNow()
	}

	private fun createTabFragment(tab: SupportAppScreen) = tab.fragment
}