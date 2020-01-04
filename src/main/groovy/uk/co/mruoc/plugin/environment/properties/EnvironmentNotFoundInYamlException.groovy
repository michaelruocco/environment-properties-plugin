package uk.co.mruoc.plugin.environment.properties

import org.gradle.api.GradleException

class EnvironmentNotFoundInYamlException extends GradleException {

    EnvironmentNotFoundInYamlException(def message) {
        super(message)
    }

}
