plugins {
    id("java")

    // adding shadow
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.sleepyfish"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Adding com.google.gson
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.jar {
    archiveFileName.set("CustomTP-Tools-v"+{version}+"-SleepyFish.jar")

    manifest {
        attributes["Main-Class"] = "me.sleepyfish.CTPT.Main"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "4g"
}

tasks.withType<JavaCompile> {
    options.isFork = true
    options.forkOptions.memoryMaximumSize = "4g"
}