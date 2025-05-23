import com.android.build.gradle.BaseExtension
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("org.sonarqube") version "5.1.0.4882"
    id("jacoco")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

//Signature app
val useGithubSecrets = System.getenv("CI") == "true" // Vérifie si l'on est dans un environnement CI

val keystoreProperties: Properties? = if (!useGithubSecrets) {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        Properties().apply {
            load(FileInputStream(keystorePropertiesFile))
        }
    } else {
        null
    }
} else {
    null
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

    signingConfigs {
        create("release") {
            if (useGithubSecrets) {
                // Pour GitHub Actions avec secrets

                val keystorePath = rootProject.file("app/rebonnte-release-key.jks") // Fichier keystore à fournir dans GitHub Actions
                if (!keystorePath.exists()) {
                    throw FileNotFoundException("Le fichier de keystore n'a pas été trouvé dans le chemin : ${keystorePath.absolutePath}")
                }

                // Utilisation des secrets de GitHub Actions
                storeFile = keystorePath
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            } else {
                // Pour la configuration locale avec un fichier keystore.properties
                if (keystoreProperties != null) {
                    storeFile = file(keystoreProperties["storeFile"] as String)
                    storePassword = keystoreProperties["storePassword"] as String
                    keyAlias = keystoreProperties["keyAlias"] as String
                    keyPassword = keystoreProperties["keyPassword"] as String
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
            pickFirsts.add("META-INF/LICENSE.md")
            pickFirsts.add("META-INF/LICENSE-notice.md")
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
    testImplementation(libs.testng)
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
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")



    // Tests instrumentalisés
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)


    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.compose.material3:material3")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48") // ou la version la plus récente





    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

    // Dépendance pour BottomSheet
    implementation ("androidx.compose.material:material:1.7.6")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation ("androidx.compose.ui:ui:1.7.8")
    implementation ("androidx.compose.foundation:foundation:1.7.8")
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
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/autogenerated/**",
        "**/*$*.class"

    )

    val kotlinDebugClassesDir = fileTree("${project.buildDir}/tmp/kotlin-classes/debug/") {
        exclude(fileFilter)
    }

    val mainSrc = androidExtension.sourceSets.getByName("main").java.srcDirs

    classDirectories.setFrom(kotlinDebugClassesDir)
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(
        fileTree(project.buildDir).apply {
            include(
                "**/*.exec", "**/*.ec"
            )
            builtBy("testDebugUnitTest")
        }
    )
}