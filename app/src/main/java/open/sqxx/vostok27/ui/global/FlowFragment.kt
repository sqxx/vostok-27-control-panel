package open.sqxx.vostok27.ui.global

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import open.sqxx.vostok27.R
import open.sqxx.vostok27.di.module.FlowNavigationModule
import open.sqxx.vostok27.extension.setLaunchScreen
import ru.terrakok.cicerone.*
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.Command
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

abstract class FlowFragment : BaseFragment() {
	override val layoutRes: Int = R.layout.layout_container

	private val currentFragment
		get() = childFragmentManager.findFragmentById(R.id.container) as? BaseFragment

	@Inject
	lateinit var navigatorHolder: NavigatorHolder

	@Inject
	lateinit var router: Router

	override fun installModules(scope: Scope) {
		scope.installModules(
			FlowNavigationModule(scope.getInstance(Router::class.java))
		)
	}

	private val navigator: Navigator by lazy {
		object : SupportAppNavigator(this.activity, childFragmentManager, R.id.container) {
			override fun activityBack() {
				router.exit()
			}

			override fun setupFragmentTransaction(
				command: Command?,
				currentFragment: Fragment?,
				nextFragment: Fragment?,
				fragmentTransaction: FragmentTransaction
			) {
				// Fix incorrect order lifecycle callback of MainFragment
				fragmentTransaction.setReorderingAllowed(true)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Toothpick.inject(this, scope)
		if (childFragmentManager.fragments.isEmpty()) {
			navigator.setLaunchScreen(getLaunchScreen())
		}
	}

	abstract fun getLaunchScreen(): SupportAppScreen

	override fun onBackPressed() {
		currentFragment?.onBackPressed() ?: super.onBackPressed()
	}

	override fun onResume() {
		super.onResume()
		navigatorHolder.setNavigator(navigator)
	}

	override fun onPause() {
		navigatorHolder.removeNavigator()
		super.onPause()
	}
}