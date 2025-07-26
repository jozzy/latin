plugins {
    alias(libs.plugins.ktor)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "org.latin.server.LatinAppKt"
}

dependencies {
    val arcVersion = "0.143.0"

    // Arc
    implementation("org.eclipse.lmos:arc-azure-client:$arcVersion")
    implementation("org.eclipse.lmos:arc-api:$arcVersion")
    implementation("org.eclipse.lmos:arc-agents:$arcVersion")
    implementation("org.eclipse.lmos:arc-result:$arcVersion")
    implementation("org.eclipse.lmos:arc-server:$arcVersion")
    implementation("org.eclipse.lmos:arc-graphql-spring-boot-starter:$arcVersion")
    implementation("org.eclipse.lmos:arc-assistants:$arcVersion")

    implementation(libs.ktor.server.cio)
    implementation(libs.kotlinx.coroutines.slf4j)
    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.sse)

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("io.mockk:mockk:1.14.2")
}
