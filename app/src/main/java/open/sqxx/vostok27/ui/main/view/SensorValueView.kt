package open.sqxx.vostok27.ui.main.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.view_sensor_value.view.*
import open.sqxx.vostok27.R
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@ExperimentalUnsignedTypes
class SensorValueView(
	context: Context,
	private val attrs: AttributeSet
) : LinearLayout(context, attrs) {

	companion object {
		private const val TIME_MARKER_FORMAT = "HH:mm:ss"

		private const val AUTO_CLEAR_MEMORY = true

		private const val OVERFLOW_BOUNDARY = 200
		private const val DROP_COUNT_ON_OVERFLOW = 15
	}

	private var layout: View = LayoutInflater
		.from(context)
		.inflate(R.layout.view_sensor_value, this, true)

	val label: String
		get() = layout.label.text.toString()

	val value: String
		get() = layout.label.value.toString()

	var unit: String = ""
		private set

	private var set: LineDataSet? = null

	private val dates = mutableListOf<String>()

	init {
		bindListeners()
		collectAttributes()
	}

	private fun bindListeners() {
		layout.container.setOnClickListener {
			layout.chart_container.toggle()
		}
	}

	private fun collectAttributes() {
		val attributes = context.obtainStyledAttributes(attrs, R.styleable.SensorValueView)

		try {
			unit = attributes.getString(R.styleable.SensorValueView_unit)!!
			updateLabel(attributes.getString(R.styleable.SensorValueView_label)!!)
			updateValue(attributes.getInteger(R.styleable.SensorValueView_value, 0).toFloat())
		} finally {
			attributes.recycle()
		}
	}

	fun updateLabel(label: String) {
		layout.label.text = label
	}

	fun <T> updateValue(value: T)
		where T : Number {

		val isFloatingPointNumber = ((value.toFloat() * 10f) % 10 != 0.0f)

		if (isFloatingPointNumber) {
			layout.value.text = ("${"%.2f".format(value.toFloat())} $unit")
		} else {
			layout.value.text = ("${"%d".format(value.toInt())} $unit")
		}

		updateChart(value.toFloat())
	}

	private fun updateChart(value: Float) {
		// Первый запуск графика...
		if (set == null) {
			setupChart()

			set!!.values = mutableListOf<Entry>()
		}

		// Копируем текущие значения
		// Изменение массива напрямую из set не работает
		val values = set!!.values

		// Формируем маркер для x
		val today = LocalDateTime.now()
		val timeMarker = today.format(DateTimeFormatter.ofPattern(TIME_MARKER_FORMAT))

		// Добавляем новое значение.
		// y - индекс значения, x - значение
		values.add(Entry(values.size.toFloat(), value))

		// Добавляем маркер
		dates.add(timeMarker)

		// Удаляем неиспользуемые значения, после достижения порога
		// Использовано для экономии памяти
		if (values.size >= OVERFLOW_BOUNDARY && AUTO_CLEAR_MEMORY) {
			values.drop(DROP_COUNT_ON_OVERFLOW)
			dates.drop(DROP_COUNT_ON_OVERFLOW)
		}

		// Обновляем данные и график
		set!!.values = values
		refreshChart()
	}

	fun reset() {
		updateValue(0f)

		set!!.values = mutableListOf<Entry>()
		refreshChart()
	}

	private fun setupChart() {
		chart.setPinchZoom(true)
		chart.setTouchEnabled(false)

		chart.setNoDataText("Нет данных")
		chart.setNoDataTextColor(Color.WHITE)

		// Делаем шаг по оси x равным 1,
		//   чтобы корректно отображались даты
		chart.xAxis.granularity = 1f

		// Отключаем описание и легенду
		chart.description.isEnabled = false
		chart.legend.isEnabled = false

		// Отключаем отображение правой шкалы
		chart.axisRight.isEnabled = false

		// Перемещаем линию координаты X вниз
		chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

		chart.xAxis.textColor = Color.WHITE
		chart.axisLeft.textColor = Color.WHITE

		// Отображение дат, вместо индексов значений
		chart.xAxis.valueFormatter = object : ValueFormatter() {
			override fun getAxisLabel(value: Float, axis: AxisBase?): String {
				return dates.getOrNull(value.toInt()) ?: value.toString()
			}
		}

		// Настройка отображения графика
		set = LineDataSet(null, null)

		set!!.color = Color.WHITE
		set!!.lineWidth = 1f

		set!!.setDrawCircleHole(false)
		set!!.setDrawFilled(true)
		set!!.setCircleColor(Color.RED)
		set!!.circleRadius = 2f

		set!!.valueTextSize = 9f
		set!!.setValueTextColors(listOf(Color.WHITE))

		set!!.formLineWidth = 1f
		set!!.formSize = 15f

		set!!.fillColor = Color.WHITE
	}

	private fun refreshChart() {
		val zeroEntry = Entry(0f, 0f)
		val isZeroData = set!!.values[0].equalTo(zeroEntry) && set!!.values.size == 1

		chart.data =
			if (set!!.values.isEmpty() || isZeroData)
				null
			else
				LineData(listOf(set))

		chart.invalidate()
	}
}