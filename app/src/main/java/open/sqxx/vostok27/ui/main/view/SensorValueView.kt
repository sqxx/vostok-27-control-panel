package open.sqxx.vostok27.ui.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_sensor_value.view.*
import open.sqxx.vostok27.R

class SensorValueView(
	context: Context,
	private val attrs: AttributeSet
) : LinearLayout(context, attrs) {

	private var layout: View = LayoutInflater
		.from(context)
		.inflate(R.layout.view_sensor_value, this, true)

	val label: String
		get() = layout.label.text.toString()

	val value: String
		get() = layout.label.value.toString()

	var unit: String = ""
		private set

	init {
		collectAttributes()
	}

	private fun collectAttributes() {
		val attributes = context.obtainStyledAttributes(attrs, R.styleable.SensorValueView)

		try {
			setLabel(attributes.getString(R.styleable.SensorValueView_label)!!)
			setValue(attributes.getString(R.styleable.SensorValueView_value)!!)
			unit = attributes.getString(R.styleable.SensorValueView_unit)!!
		} finally {
			attributes.recycle()
		}
	}

	private fun setLabel(label: String) {
		layout.label.text = label
	}

	fun setValue(value: String) {
		layout.value.text = ("$value $unit")
	}
}