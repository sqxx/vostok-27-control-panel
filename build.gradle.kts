buildscript {
	repositories {
		google()
		jcenter()
		maven { url = uri("https://jitpack.io") }
		maven { url = uri("https://maven.fabric.io/public") }
	}
	dependencies {
		classpath("com.android.tools.build:gradle:3.5.3")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
		classpath("io.fabric.tools:gradle:1.29.0")
	}
}

allprojects {
	extra["kotlinVersion"] = "1.3.41"
	repositories {
		google()
		jcenter()
		maven { url = uri("https://jitpack.io") }
		maven { url = uri("https://maven.fabric.io/public") }
	}
}

val clean by tasks.creating(Delete::class) {
	delete = setOf(rootProject.buildDir)
}