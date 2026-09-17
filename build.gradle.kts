plugins {
    id("java")
}

group = "com.minecraft.minigame"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(files("E:/api/nartema.jar"))

}

tasks.test {
    useJUnitPlatform()
}