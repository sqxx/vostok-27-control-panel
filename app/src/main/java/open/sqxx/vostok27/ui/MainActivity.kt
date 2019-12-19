package open.sqxx.vostok27.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import app.akexorcist.bluetotohspp.library.*
import com.arellomobile.mvp.MvpAppCompatActivity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import open.sqxx.vostok27.R
import open.sqxx.vostok27.di.DI
import open.sqxx.vostok27.model.repository.*
import open.sqxx.vostok27.model.system.message.*
import open.sqxx.vostok27.presentation.AppLauncher
import open.sqxx.vostok27.ui.global.BaseFragment
import open.sqxx.vostok27.ui.global.MessageDialogFragment
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import toothpick.Toothpick
import javax.inject.Inject

@ExperimentalUnsignedTypes
class MainActivity : MvpAppCompatActivity() {

	@Inject
	lateinit var appLauncher: AppLauncher

	@Inject
	lateinit var navigatorHolder: NavigatorHolder

	@Inject
	lateinit var systemMessageNotifier: SystemMessageNotifier

	@Inject
	lateinit var bluetoothFront: BluetoothFront

	@Inject
	lateinit var bt: BluetoothSPP

	private var savedInstanceState: Bundle? = null

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

	//region Жизненный цикл

	override fun onStart() {
		super.onStart()

		if (!bt.isBluetoothEnabled) {
			enableBluetooth()
			selectBluetoothDevice()
			return
		}

		if (!bt.isServiceAvailable) {
			bt.setupService()
			bt.startService(BluetoothState.DEVICE_ANDROID)

			selectBluetoothDevice()
			setup()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		Toothpick.inject(this, Toothpick.openScope(DI.APP_SCOPE))

		appLauncher.onLaunch()
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_main)

		this.savedInstanceState = savedInstanceState

		if (!bt.isBluetoothAvailable) {
			systemMessageNotifier.send(
				SystemMessage(
					"Bluetooth недоступен",
					SystemMessageType.ALERT
				)
			)
			finish()
		}

		handleBluetooth()
	}

	override fun onPause() {
		navigatorHolder.removeNavigator()
		unsubscribeOnSystemMessages()
		super.onPause()
	}

	override fun onDestroy() {
		super.onDestroy()
		bt.stopService()
	}

	override fun onResumeFragments() {
		super.onResumeFragments()
		subscribeOnSystemMessages()
		navigatorHolder.setNavigator(navigator)
	}

	//endregion

	//region Система событий

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

	private fun showAlertMessage(message: String) {
		MessageDialogFragment.create(
			message = message
		).show(supportFragmentManager, null)
	}

	private fun showToastMessage(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}

	//endregion

	//region Внешние события

	override fun onBackPressed() {
		currentFragment?.onBackPressed() ?: super.onBackPressed()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
			if (resultCode == Activity.RESULT_OK) {
				bt.connect(data)
			}
		} else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				bt.setupService()
				bt.startService(BluetoothState.DEVICE_ANDROID)
				setup()
			} else {
				systemMessageNotifier.send(
					SystemMessage(
						"Bluetooth требуется для работы приложения",
						SystemMessageType.ALERT
					)
				)

				finish()
			}
		}

		super.onActivityResult(requestCode, resultCode, data)
	}

	//endregion

	private fun setup() {
		if (savedInstanceState == null) {
			appLauncher.coldStart(bluetoothFront)
		}
	}

	private fun enableBluetooth() {
		val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
		startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT)
	}

	private fun selectBluetoothDevice() {
		if (bt.serviceState == BluetoothState.STATE_CONNECTED) {
			bt.disconnect()
		}

		bt.setDeviceTarget(BluetoothState.DEVICE_OTHER)

		val intent = Intent(applicationContext, DeviceList::class.java)
		startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
	}

	private fun handleBluetooth() {
		bt.setOnDataReceivedListener { data, _ ->
			if (data.isEmpty()) return@setOnDataReceivedListener

			val d = data.toUByteArray()
			val command = BluetoothModel.extractCommand(d)

			if (command == BluetoothModel._P_STARTUP ||
				command == BluetoothModel._P_INIT_COMPLETE ||
				command == BluetoothModel._P_NOT_READY
			) {
				bluetoothFront.status.value = BluetoothStatus.WAIT_FOR_READY
			} else if (bluetoothFront.status.value != BluetoothStatus.READY) {
				bluetoothFront.status.value = BluetoothStatus.READY
			}

			bluetoothFront.receiver.value = d
		}

		bt.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
			override fun onDeviceDisconnected() {
				bluetoothFront.status.value = BluetoothStatus.NONE

				systemMessageNotifier.send(
					SystemMessage(
						"Bluetooth устройство отключилось",
						SystemMessageType.TOAST
					)
				)

				selectBluetoothDevice()
			}

			override fun onDeviceConnectionFailed() {
				bluetoothFront.status.value = BluetoothStatus.NONE

				systemMessageNotifier.send(
					SystemMessage(
						"Не удалось подключится к Bluetooth усройству",
						SystemMessageType.TOAST
					)
				)

				selectBluetoothDevice()
			}

			override fun onDeviceConnected(name: String, address: String) {
				bluetoothFront.status.value = BluetoothStatus.CONNECTED
			}
		})

		bluetoothFront.sender.observable.subscribe(
			object : Observer<UByteArray> {
				override fun onSubscribe(d: Disposable) {
					//unused
				}

				override fun onNext(s: UByteArray) {
					if (s.size != BluetoothModel.PACKAGE_SIZE) return
					bt.send(s.toByteArray(), true)
				}

				override fun onError(e: Throwable) {
					//unused
				}

				override fun onComplete() {
					//unused
				}
			}
		)
	}
}