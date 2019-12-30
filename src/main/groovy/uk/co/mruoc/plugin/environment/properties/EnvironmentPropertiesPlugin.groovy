package uk.co.mruoc.plugin.environment.properties

import org.gradle.api.Plugin
import org.gradle.api.Project

class EnvironmentPropertiesPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task('generateEnvironmentProperties', type: GenerateEnvironmentPropertiesTask)
    }

}
