import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(libs.material3.window.size.class1)
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.components.resources)
}

compose.desktop {
    application {
        mainClass = "ink.terraria.bill.MainKt"

        nativeDistributions {
            modules("java.base", "java.desktop", "java.net.http")
            targetFormats(TargetFormat.Msi)
            packageName = "Oh My Bill"
            packageVersion = "1.0.1"

            windows {
                iconFile.set(project.file("src/main/resources/icon.ico"))
                shortcut = true
                menuGroup = "Oh My Bill"
                dirChooser = true
                upgradeUuid = "ecd6deb6-5095-421a-9c16-7961a3c7d50e"
            }
        }
    }
}
