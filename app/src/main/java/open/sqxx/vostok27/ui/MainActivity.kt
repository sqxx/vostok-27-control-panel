package open.sqxx.vostok27.ui

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import com.arellomobile.mvp.MvpAppCompatActivity
import io.reactivex.disposables.Disposable
import open.sqxx.vostok27.R
import open.sqxx.vostok27.di.DI
import open.sqxx.vostok27.model.system.message.SystemMessageNotifier
import open.sqxx.vostok27.model.system.message.SystemMessageType
import open.sqxx.vostok27.presentation.AppLauncher
import open.sqxx.vostok27.ui.global.BaseFragment
import open.sqxx.vostok27.ui.global.MessageDialogFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import toothpick.Toothpick
import javax.inject.Inject

class MainActivity : MvpAppCompatActivity() {

	@Inject
	lateinit var appLauncher: AppLauncher

	@Inject
	lateinit var navigatorHolder: NavigatorHolder

	@Inject
	lateinit var systemMessageNotifier: SystemMessageNotifier

	@Inject
	lateinit var bt: BluetoothSPP

	private var notifierDisposable: Disposable? = null

	private val currentFragment: BaseFragment?
		get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment

	private val navigator: Navigator =
		object : SupportAppNavigator(this, supportFragmentManager, R.id.container) {
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

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		Toothpick.inject(this, Toothpick.openScope(DI.APP_SCOPE))

		appLauncher.onLaunch()
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_main)

		if (savedInstanceState == null) {
			appLauncher.coldStart()
		}
	}

	override fun onResumeFragments() {
		super.onResumeFragments()
		subscribeOnSystemMessages()
		navigatorHolder.setNavigator(navigator)
	}

	override fun onPause() {
		navigatorHolder.removeNavigator()
		unsubscribeOnSystemMessages()
		super.onPause()
	}

	override fun onBackPressed() {
		currentFragment?.onBackPressed() ?: super.onBackPressed()
	}

	private fun showAlertMessage(message: String) {
		MessageDialogFragment.create(
			message = message
		).show(supportFragmentManager, null)
	}

	private fun showToastMessage(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}

	private fun subscribeOnSystemMessages() {
		notifierDisposable = systemMessageNotifier.notifier
			.subscribe { msg ->
				when (msg.type) {
					SystemMessageType.ALERT -> showAlertMessage(msg.text)
					SystemMessageType.TOAST -> showToastMessage(msg.text)
				}
			}
	}

	private fun unsubscribeOnSystemMessages() {
		notifierDisposable?.dispose()
	}
}
