package uk.co.mruoc.plugin.environment.properties

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

class GenerateMultipleEnvironmentPropertiesTask extends DefaultTask {

    private def log = LoggerFactory.getLogger(GenerateMultipleEnvironmentPropertiesTask.class)

    @Input
    Collection<String> environments

    @Input
    String yamlPath

    @Input
    String propertiesPath

    @Input
    @Optional
    String defaultEnvironment

    GenerateMultipleEnvironmentPropertiesTask() {
        description = 'Generates multiple properties files from properties.yaml based on specified environments'
    }

    @TaskAction
    def run() {
        for (String environment : environments) {
            def propertiesFile = buildPropertiesFile(environment)
            def yamlFile = project.file(yamlPath)
            def generator = new PropertiesGenerator(yamlFile, propertiesFile, environment, defaultEnvironment)
            generator.generate()
        }
    }

    private buildPropertiesFile(String environment) {
        final def path = propertiesPath.replaceAll("\\{\\{env}}", environment)
        log.info("building properties file with path ${path}")
        def file = project.file(path)
        log.info("created properties file at ${file.absolutePath}")
        file
    }

}
