package open.sqxx.vostok27.ui.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_option_time.view.*
import open.sqxx.vostok27.R

class OptionTimeView(
	context: Context,
	private val attrs: AttributeSet
) : LinearLayout(context, attrs) {

	private var layout: View = LayoutInflater
		.from(context)
		.inflate(R.layout.view_option_time, this, true)

	var onTimeChangedListener: ((hour: Int, minute: Int) -> Unit)? = null

	init {
		setupLayout()
		collectAttributes()
	}

	private fun setupLayout() {
		container.setOnClickListener {
			time_container.toggle()
		}

		layout.time.setIs24HourView(true)
		layout.time.setOnTimeChangedListener { _, hour, min ->
			updateTimePreview(hour, min)
			onTimeChangedListener?.invoke(hour, min)
		}
	}

	private fun collectAttributes() {
		val attributes = context.obtainStyledAttributes(attrs, R.styleable.OptionTimeView)

		try {
			updateTitle(attributes.getString(R.styleable.OptionTimeView_title)!!)

			val hour = attributes.getInt(R.styleable.OptionTimeView_hour, 0)
			val minute = attributes.getInt(R.styleable.OptionTimeView_minute, 0)

			if (hour !in (0..23)) {
				throw Exception("Hour must be in range from 0 to 23")
			}

			if (minute !in (0..59)) {
				throw Exception("Minute must be in range from 0 to 59")
			}

			updateTime(hour, minute)
		} finally {
			attributes.recycle()
		}
	}

	fun updateTitle(title: String) {
		layout.title.text = title
	}

	fun updateTime(hour: Int, minute: Int) {
		updateTimePreview(hour, minute)

		layout.time.currentHour = hour
		layout.time.currentMinute = minute
	}

	private fun updateTimePreview(hour: Int, minute: Int) {
		time_preview.text = ("%02d:%02d".format(hour, minute))
	}

	fun updateState(isEnabled: Boolean) {
		layout.container.isEnabled = isEnabled
		layout.time.isEnabled = isEnabled
	}
}