package com.motycka.edu

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import java.time.Duration

/**
 * A custom container for running the Ktor application in integration tests.
 * This container uses a pre-built Docker image and starts the application.
 */
class ApplicationContainer : GenericContainer<ApplicationContainer>(
    DockerImageName.parse("openjdk:21-slim")
) {

    init {
        withExposedPorts(APP_PORT)

        // Copy the application JAR file
        withCopyFileToContainer(
            MountableFile.forHostPath("../coffee-shop-application/build/libs/basic-application-all.jar"),
            "/app/app.jar"
        )

        // Copy the application configuration
        withCopyFileToContainer(
            MountableFile.forHostPath("../coffee-shop-application/src/main/resources/application.yaml"),
            "/app/config/application.yaml"
        )

        // Copy the test configuration
        withCopyFileToContainer(
            MountableFile.forHostPath("../coffee-shop-integration-tests/src/test/resources/application-test.yaml"),
            "/app/config/application-test.yaml"
        )

        // Set the environment variable for the configuration file
        withEnv("KTOR_CONFIG_FILE", "/app/config/application-test.yaml")

        // Run the application
        withCommand("java", "-jar", "/app/app.jar")

        // Add logging
        withLogConsumer(Slf4jLogConsumer(org.slf4j.LoggerFactory.getLogger(ApplicationContainer::class.java)))

        // Wait for the application to be ready
        waitingFor(
            Wait.forListeningPort()
                .withStartupTimeout(Duration.ofSeconds(30))
        )
    }

    /**
     * Get the base URL for the application running in the container.
     */
    fun getBaseUrl(): String {
        return "http://${host}:${getMappedPort(APP_PORT)}/api/v1"
    }

    companion object {
        private val APP_PORT = 8080
    }

}
