package uk.co.mruoc.plugin.environment.properties

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

class GenerateEnvironmentPropertiesTask extends DefaultTask {

    private def log = LoggerFactory.getLogger(GenerateEnvironmentPropertiesTask.class)

    @Input
    String environment

    @Input
    String yamlPath

    @Input
    String propertiesPath

    GenerateEnvironmentPropertiesTask() {
        description = 'Generates properties file from properties.yaml based on specified environment'
    }

    @TaskAction
    def run() {
        def generator = new PropertiesGenerator(yamlFile, propertiesFile, environment)
        generator.generate()
    }

    @Internal
    protected getYamlFile() {
        return project.file(yamlPath)
    }

    @Internal
    protected getPropertiesFile() {
        return project.file(propertiesPath)
    }

}
