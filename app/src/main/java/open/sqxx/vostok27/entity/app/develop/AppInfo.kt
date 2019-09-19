package open.sqxx.vostok27.entity.app.develop

data class AppInfo(
	val versionName: String,
	val versionCode: Int,
	val description: String,
	val buildId: String,
	val url: String,
	val feedbackUrl: String
)
