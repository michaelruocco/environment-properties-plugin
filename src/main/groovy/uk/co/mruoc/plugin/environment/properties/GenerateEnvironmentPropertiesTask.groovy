package uk.co.mruoc.plugin.environment.properties

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

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
        environment = loadEnvironment()
        log.info("generating ${propertiesFile.absolutePath} for ${environment} environment from ${yamlFile.absolutePath}")
        validateInputYaml()
        FileCreator.createFileIfDoesNotExist(propertiesFile)
        generateProperties()
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

    private validateInputYaml() {
        log.info("checking input yaml file ${yamlFile.absolutePath} exists")
        if (!yamlFile.exists()) {
            throw new IllegalArgumentException("input yaml file ${yamlFile.absolutePath} does not exist")
        }
    }

    private generateProperties() {
        def yamlProperties = new Yaml().load(yamlFile.text)
        if (!yamlProperties['environments'].containsKey(environment)) {
            log.warn("no properties found in yaml ${yamlFile.absolutePath} for environment ${environment}")
        }

        def collector = []
        yamlProperties['environments'][environment].each { k, v -> traverse(k, v, collector) }
        propertiesFile.withWriter { out ->
            out.writeLine("# generated from ${yamlFile.name} for ${environment} environment")
        }
        collector.each { propertiesFile << it + "\n" }
    }

    private static traverse(key, value, collector) {
        if (value instanceof Map) {
            value.each { k, v -> traverse("$key.$k", v, collector) }
        } else {
            collector << "$key=" + replaceNullWithEmptyString(value)
        }
    }

    private static def replaceNullWithEmptyString(Object value) {
        if (value == null)
            return ""
        return value
    }

}
