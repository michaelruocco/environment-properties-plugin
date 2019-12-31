package uk.co.mruoc.plugin.environment.properties

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

class GenerateEnvironmentPropertiesTask extends DefaultTask {

    private def log = LoggerFactory.getLogger(GenerateEnvironmentPropertiesTask.class)

    @Input
    @Optional
    String environment

    @Input
    File yamlFile

    @Input
    String propertiesPath

    GenerateEnvironmentPropertiesTask() {
        description = 'Generates properties file from properties.yaml based on specified environment'
    }

    @TaskAction
    def run() {
        def generator = new PropertiesGenerator(yamlFile, propertiesFile, loadEnvironment())
        generator.generate()
    }

    @Internal
    protected getPropertiesFile() {
        return project.file(propertiesPath)
    }

    private String loadEnvironment() {
        if (project.hasProperty('env')) {
            def environmentProperty = project.getProperties().get('env')
            log.info("using environment property value ${environmentProperty}")
            return environmentProperty
        }
        if (environment) {
            log.info("using environment value ${environment}")
            return environment
        }
        log.info("no environment specified returning local")
        return "local"
    }

}
