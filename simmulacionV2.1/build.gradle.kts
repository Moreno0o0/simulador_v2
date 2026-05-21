plugins {
    java
    application
    //id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "com.fes.aragon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.fes.aragon.simmulacionv2")
    mainClass.set("com.fes.aragon.simmulacionv2.HelloApplication")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media")
}

dependencies {
    implementation("com.github.almasb:fxgl:11.17") {
        exclude(group = "org.openjfx")
        exclude(group = "org.jetbrains.kotlin")
    }

    // --- CEREBRO BIÓNICO (DL4J M1.1) ---
    val dl4jVersion = "1.0.0-M1.1"
    implementation("org.deeplearning4j:deeplearning4j-core:$dl4jVersion")
    implementation("org.deeplearning4j:rl4j-core:$dl4jVersion")
    implementation("org.nd4j:nd4j-native-platform:$dl4jVersion")

    // Necesario para ver el progreso en consola
    implementation("org.slf4j:slf4j-simple:1.7.36")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}
tasks.withType<JavaExec> {
    jvmArgs(
        // 1. Memoria Heap (Java)
        "-Xms4G",           // Arranca pidiendo 8GB de golpe
        "-Xmx8G",          // Límite para objetos Java: 16GB

        // 2. Memoria Off-Heap (ND4J - C++ Matemático)
        // ¡Aquí es donde ocurre la magia de las redes neuronales!
        "-Dorg.bytedeco.javacpp.maxbytes=16G",
        "-Dorg.bytedeco.javacpp.maxphysicalbytes=36G",

        // 3. Optimizador de recolección de basura para que no haya tirones
        "-XX:+UseG1GC"
    )
}