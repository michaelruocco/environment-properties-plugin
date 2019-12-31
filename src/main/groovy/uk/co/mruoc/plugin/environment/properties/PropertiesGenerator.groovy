package uk.co.mruoc.plugin.environment.properties

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

class PropertiesGenerator {

    private def log = LoggerFactory.getLogger(PropertiesGenerator.class)

    private final File yamlFile
    private final File propertiesFile
    private final String environment

    PropertiesGenerator(final File yamlFile, File propertiesFile, final String environment) {
        this.yamlFile = yamlFile
        this.propertiesFile = propertiesFile
        this.environment = environment
    }

    def generate() {
        log.info("generating ${propertiesFile.absolutePath} for ${environment} environment from ${yamlFile.absolutePath}")
        validateInputYaml()
        FileCreator.createFileIfDoesNotExist(propertiesFile)
        generateProperties()
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
