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
    String defaultEnvironment

    @Input
    String yamlPath

    @Input
    String propertiesPath

    private String environment

    GenerateEnvironmentPropertiesTask() {
        description = 'Generates environment.properties file from properties.yaml based on specified environment'
    }

    @TaskAction
    def run() {
        def generator = new PropertiesGenerator(yamlFile, propertiesFile, loadEnvironment())
        generator.generate()
    }

    @Internal
    protected getEnvironment() {
        environment
    }

    @Internal
    protected getYamlFile() {
        return new File(yamlPath)
    }

    @Internal
    protected getPropertiesFile() {
        return new File(propertiesPath)
    }

    private String loadEnvironment() {
        if (project.hasProperty('env')) {
            def environmentProperty = project.getProperties().get('env')
            log.info("using environment property value ${environmentProperty}")
            return environmentProperty
        }
        if (defaultEnvironment) {
            log.info("using default environment value ${defaultEnvironment}")
            return defaultEnvironment
        }
        log.info("no environment specified returning local")
        return "local"
    }

}
