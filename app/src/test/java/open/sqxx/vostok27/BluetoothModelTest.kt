package open.sqxx.vostok27

import junit.framework.Assert.assertEquals
import open.sqxx.vostok27.model.repository.BluetoothModel
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class BluetoothModelTest {

	companion object {
		private val DEMO_COMMAND = 10U.toUByte()
		private const val DEMO_VALUE = 120U
	}

	private fun buildDemoPackage(): UByteArray {
		return BluetoothModel.buildPackage(DEMO_COMMAND, DEMO_VALUE)
	}

	@Before
	fun hello() {
		println("Тестовая команда: $DEMO_COMMAND")
		println("Тестовое значение: $DEMO_VALUE")
		println()
	}

	@Test
	fun testCrc() {
		val demoPackage = buildDemoPackage()

		val calculatedCrc = BluetoothModel.calculateCrc(demoPackage)
		val extractedCrc = BluetoothModel.extractCrc(demoPackage)

		assertEquals(
			"Несовпадает рассчётная и извлечённая контрольная сумма",
			calculatedCrc,
			extractedCrc
		)

		println("Рассчётная контрольная сумма: $calculatedCrc")
		println("Извлечённая контрольная сумма: $extractedCrc")
		println()
	}

	@Test
	fun testValue() {
		val demoPackage = buildDemoPackage()

		val extractedValue = BluetoothModel.extractValue(demoPackage)

		assertEquals(
			"Несовпадают демо данные и извлечённый результат",
			extractedValue,
			DEMO_VALUE
		)

		println("Тестовое значение: $DEMO_VALUE")
		println("Извлечённое значение: $extractedValue")
	}
}