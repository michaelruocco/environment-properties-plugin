package uk.co.mruoc.plugin.environment.properties

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

class PropertiesGenerator {

    private def log = LoggerFactory.getLogger(PropertiesGenerator.class)

    private final File yamlFile
    private final File propertiesFile
    private final String environment
    private final String defaultEnvironment

    PropertiesGenerator(final File yamlFile,
                        final File propertiesFile,
                        final String environment,
                        final String defaultEnvironment) {
        this.yamlFile = yamlFile
        this.propertiesFile = propertiesFile
        this.environment = environment
        this.defaultEnvironment = defaultEnvironment
    }

    def generate() {
        log.info(buildConfigMessage())
        validateInputYaml()
        generateProperties()
    }

    private buildConfigMessage() {
        def message = "generating ${propertiesFile.absolutePath} for ${environment} environment from ${yamlFile.absolutePath}, "
        if (defaultEnvironment) {
            return message + "default environment ${defaultEnvironment} will be used if ${environment} is not found in yaml"
        }
        return message + "no default environment provided, plugin will error if ${environment} is not found in yaml"
    }

    private validateInputYaml() {
        log.info("checking input yaml file ${yamlFile.absolutePath} exists")
        if (!yamlFile.exists()) {
            throw new IllegalArgumentException("input yaml file ${yamlFile.absolutePath} does not exist")
        }
    }

    private generateProperties() {
        def yamlProperties = new Yaml().load(yamlFile.text)
        def environmentFinder = new EnvironmentFinder(yamlProperties, environment, defaultEnvironment)
        def environment = environmentFinder.environmentOrDefault
        if (!environment.isPresent()) {
            throw new EnvironmentNotFoundInYamlException("${this.environment} environment and default environment ${defaultEnvironment} not found in yaml ${yamlFile.absolutePath}")
        }
        buildEnvironmentProperties(yamlProperties, environment.get())
    }

    private buildEnvironmentProperties(def yamlProperties, def environment) {
        def collector = []
        yamlProperties['environments'][environment].each { k, v -> traverse(k, v, collector) }
        FileCreator.createFileIfDoesNotExist(propertiesFile)
        propertiesFile.withWriter { out ->
            out.writeLine(buildCommentMessage())
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

    private buildCommentMessage() {
        return "# generated from ${yamlFile.name} for ${this.environment} environment"
    }

}
