package uk.co.mruoc.plugin.environment.properties

import groovy.util.logging.Slf4j

@Slf4j
class EnvironmentFinder {

    private final def yamlProperties
    private final String environment
    private final String defaultEnvironment

    EnvironmentFinder(final def yamlProperties, final def environment, final def defaultEnvironment) {
        this.yamlProperties = yamlProperties
        this.environment = environment
        this.defaultEnvironment = defaultEnvironment
    }

    Optional<String> getEnvironmentOrDefault() {
        def result = getEnvironmentIfFound()
        if (result.isPresent()) {
            return result
        }
        return getDefaultIfFound()
    }

    private Optional<String> getEnvironmentIfFound() {
        if (!yamlContainsEnvironment(environment)) {
            log.warn("environment ${environment} not found in yaml")
            return Optional.empty()
        }
        return Optional.of(environment)
    }

    private Optional<String> getDefaultIfFound() {
        if (!defaultEnvironment) {
            log.warn("no default environment specified")
            return Optional.empty()
        }
        if (!yamlContainsEnvironment(defaultEnvironment)) {
            log.warn("default environment ${defaultEnvironment} not found in yaml")
            return Optional.empty()
        }
        log.info("using default environment ${defaultEnvironment}")
        return Optional.of(defaultEnvironment)
    }

    private yamlContainsEnvironment(final String environment) {
        return yamlProperties['environments'].containsKey(environment)
    }

}
