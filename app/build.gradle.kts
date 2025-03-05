import com.android.build.gradle.BaseExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("org.sonarqube") version "5.1.0.4882"
    id("jacoco")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.openclassrooms.rebonnte"
    compileSdk = 34




    testCoverage {
        version = "0.8.8"
    }

    testOptions {
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
    }






    defaultConfig {
        applicationId = "com.openclassrooms.rebonnte"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    //Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    //UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    //Material3
    implementation(libs.androidx.material3)
    //Hilt (DI)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.fragment)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    //Coroutines
    implementation(libs.kotlinx.coroutines.android)
    //SplashScreen
    implementation(libs.androidx.core.splashscreen)
    //Firebase Auth
    implementation(libs.firebase.auth.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation (libs.play.services.auth)
    //Firebase Firestore
    implementation (libs.firebase.firestore.ktx)
    implementation (libs.firebase.firestore)
    //Firebase Storage
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.appcheck.debug)
    implementation (libs.firebase.storage)
    //accompanist
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.32.0")
    //Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

}

//JACOCO STUFF --------------------
tasks.withType<Test> {
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}
val androidExtension = extensions.getByType<BaseExtension>()

val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn("testDebugUnitTest","connectedDebugAndroidTest")


    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/di/**",
        "**/hilt/**",
        "**/Hilt_*.*",
        )

    val kotlinDebugClassesDir = fileTree("${project.buildDir}/tmp/kotlin-classes/debug/") {
        exclude(fileFilter)
    }

    val mainSrc = androidExtension.sourceSets.getByName("main").java.srcDirs

    classDirectories.setFrom(kotlinDebugClassesDir)
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(
        fileTree(project.buildDir) {
            include(
                "**/*.exec", "**/*.ec"
            )
        }
    )
}