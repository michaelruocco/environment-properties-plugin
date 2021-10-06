package uk.co.mruoc.plugin.environment.properties

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class GenerateEnvironmentPropertiesTask extends DefaultTask {

    @Input
    String environment

    @Input
    String yamlPath

    @Input
    String propertiesPath

    @Input
    @Optional
    String defaultEnvironment

    GenerateEnvironmentPropertiesTask() {
        description = 'Generates properties file from properties.yaml based on specified environment'
    }

    @TaskAction
    def run() {
        def yamlFile = project.file(yamlPath)
        def propertiesFile = project.file(propertiesPath)
        def generator = new PropertiesGenerator(yamlFile, propertiesFile, environment, defaultEnvironment)
        generator.generate()
    }

}
