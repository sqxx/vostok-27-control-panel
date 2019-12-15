plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android.extensions")
	kotlin("android")
	kotlin("kapt")
}

apply(from = "${project.rootDir}/codequality/ktlint.gradle.kts")

val buildUid = System.getenv("BUILD_COMMIT_SHA") ?: "local"

android {
	compileSdkVersion(29)

	defaultConfig {
		applicationId = "open.sqxx.vostok27"

		minSdkVersion(21)
		targetSdkVersion(29)

		versionName = "1.0"
		versionCode = 1

		buildToolsVersion = "29.0.0"

		lintOptions {
			isWarningsAsErrors = true
			isIgnoreTestSources = true
			setLintConfig(file("${project.rootDir}/codequality/lint_rules.xml"))
		}

		defaultConfig {
			buildConfigField("String", "VERSION_UID", "\"$buildUid\"")
			buildConfigField("String", "APP_DESCRIPTION", "\"Control station Vostok-27.\"")
			buildConfigField(
				"String",
				"FEEDBACK_URL",
				"\"https://github.com/sqxx/vostok-27-control-panel/issues\""
			)
			buildConfigField(
				"String",
				"APP_HOME_PAGE",
				"\"https://github.com/sqxx/vostok-27-control-panel\""
			)

			multiDexEnabled = true
		}

		//todo put signingConfigs for release

		buildTypes {
			create("debugPG") {
				initWith(getByName("debug"))
				isMinifyEnabled = true
				versionNameSuffix = " debugPG"

				proguardFiles(
					getDefaultProguardFile("proguard-android-optimize.txt"),
					file("proguard-rules.pro")
				)
			}
			/*todo
			getByName("release") {
				isMinifyEnabled = true
				signingConfig = signingConfigs.getByName("prod")

				proguardFiles(
					getDefaultProguardFile("proguard-android-optimize.txt"),
					file("proguard-rules.pro")
				)
			}
			*/
		}
	}
}

androidExtensions {
	isExperimental = true
}

dependencies {
	val moxyVersion = "1.7.0"
	val toothpickVersion = "3.0.2"

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-stdlib:${extra["kotlinVersion"] as String}")
	implementation(kotlin("reflect"))

	// Bluetooth
	implementation("com.akexorcist:bluetoothspp:1.0.0")

	// Support
	implementation("androidx.appcompat:appcompat:1.1.0")
	implementation("com.google.android.material:material:1.2.0-alpha02")
	implementation("androidx.cardview:cardview:1.0.0")
	implementation("androidx.constraintlayout:constraintlayout:1.1.3")

	// Ui
	implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
	implementation("net.cachapa.expandablelayout:expandablelayout:2.9.2")

	// Log
	implementation("com.jakewharton.timber:timber:4.7.0")

	// MVP Moxy
	kapt("tech.schoolhelper:moxy-x-compiler:$moxyVersion")
	implementation("tech.schoolhelper:moxy-x:$moxyVersion")
	implementation("tech.schoolhelper:moxy-x-androidx:$moxyVersion")

	// Cicerone Navigation
	implementation("ru.terrakok.cicerone:cicerone:5.0.0")

	// DI
	implementation("com.github.stephanenicolas.toothpick:toothpick-runtime:$toothpickVersion")
	kapt("com.github.stephanenicolas.toothpick:toothpick-compiler:$toothpickVersion")

	// RxJava2
	implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
	implementation("io.reactivex.rxjava2:rxjava:2.2.6")
	implementation("com.jakewharton.rxrelay2:rxrelay:2.1.0")

	// Date
	implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

	// Test
	testImplementation("junit:junit:4.12")
}

configurations.all {
	resolutionStrategy {
		force("org.jetbrains.kotlin:kotlin-stdlib:${extra["kotlinVersion"] as String}")
	}
}

gradle.buildFinished {
	println("VersionName: ${android.defaultConfig.versionName}")
	println("BuildUid: $buildUid")
}
println("VersionCode: ${android.defaultConfig.versionCode}")