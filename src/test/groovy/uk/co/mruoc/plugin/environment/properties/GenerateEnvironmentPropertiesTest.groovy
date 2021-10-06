package uk.co.mruoc.plugin.environment.properties

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.catchThrowable
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class GenerateEnvironmentPropertiesTest extends Specification {

    @Rule
    private TemporaryFolder testProjectDir = new TemporaryFolder()

    private def buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.github.michaelruocco.environment-properties'
            }
        """

        def src = new File(getClass().getResource('/config/properties.yml').toURI())
        def dest = testProjectDir.newFile('properties.yml')
        dest << src.text
    }

    def "should error if environment not present in yaml and default config is not specified"() {
        given:
        buildFile << """
            generateEnvironmentProperties {
                environment = 'not-found'
                yamlPath = 'properties.yml'
                propertiesPath = 'config/environment.properties'
            }
        """

        when:
        final def error = catchThrowable({ ->
            GradleRunner.create()
                    .withProjectDir(testProjectDir.root)
                    .withArguments('generateEnvironmentProperties', '--info')
                    .withPluginClasspath()
                    .build()
        })

        then:
        assertThat(error).isInstanceOf(UnexpectedBuildFailure.class)
        !new File("${testProjectDir.root}/config/environment.properties").exists()
    }

    def "should use default config if environment not found in yaml but default config is specified"() {
        given:
        buildFile << """
            generateEnvironmentProperties {
                environment = 'not-found'
                yamlPath = 'properties.yml'
                propertiesPath = 'config/environment.properties'
                defaultEnvironment = 'aat1'
            }
        """

        when:
        final def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('generateEnvironmentProperties', '--info')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(':generateEnvironmentProperties').outcome == SUCCESS

        new File("${testProjectDir.root}/config/environment.properties").text ==
                "# generated from properties.yml for not-found environment\n" +
                "app.name=aat-service\n" +
                "abc.url=http://aat1:8081\n"
    }

    def "should generate properties for local environment"() {
        given:
        buildFile << """
            generateEnvironmentProperties {
                environment = 'local'
                yamlPath = 'properties.yml'
                propertiesPath = 'config/environment.properties'
            }
        """

        when:
        final def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('generateEnvironmentProperties', '--info')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(':generateEnvironmentProperties').outcome == SUCCESS

        new File("${testProjectDir.root}/config/environment.properties").text ==
                "# generated from properties.yml for local environment\n" +
                "app.name=test-service\n" +
                "abc.url=http://localhost:8080\n"
    }

    def "should generate properties for sit environment"() {
        given:
        buildFile << """
            generateEnvironmentProperties {
                environment = 'sit'
                yamlPath = 'properties.yml'
                propertiesPath = 'config/environment.properties'
            }
        """

        when:
        final def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('generateEnvironmentProperties', '--info')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(':generateEnvironmentProperties').outcome == SUCCESS

        new File("${testProjectDir.root}/config/environment.properties").text ==
                "# generated from properties.yml for sit environment\n" +
                "app.name=test-service-sit\n" +
                "abc.url=http://sit:8080\n"
    }

    def "should generate properties from cascaded config for aat2 environment"() {
        given:
        buildFile << """
            generateEnvironmentProperties {
                environment = 'aat2'
                yamlPath = 'properties.yml'
                propertiesPath = 'config/environment.properties'
            }
        """

        when:
        final def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('generateEnvironmentProperties', '--info')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(':generateEnvironmentProperties').outcome == SUCCESS

        new File("${testProjectDir.root}/config/environment.properties").text ==
                "# generated from properties.yml for aat2 environment\n" +
                "app.name=aat-service\n" +
                "abc.url=http://aat2:8082\n"
    }

}
