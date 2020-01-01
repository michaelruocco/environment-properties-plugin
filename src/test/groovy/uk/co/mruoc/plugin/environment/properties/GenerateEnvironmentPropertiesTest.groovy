package uk.co.mruoc.plugin.environment.properties

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

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

    def "generate properties local environment if environment not specified"() {
        given:
        buildFile << """
            generateEnvironmentProperties {
                yamlPath = 'properties.yml'
                propertiesPath = 'config/environment.properties'
            }
        """

        when:
        def result = GradleRunner.create()
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

    def "generate properties for aat environment if specified by environment property"() {
        given:
        buildFile << """
            generateEnvironmentProperties {
                yamlPath = 'properties.yml'
                propertiesPath = 'config/environment.properties'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('generateEnvironmentProperties', '--info', '-Penv=aat')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(':generateEnvironmentProperties').outcome == SUCCESS

        new File("${testProjectDir.root}/config/environment.properties").text ==
                "# generated from properties.yml for aat environment\n" +
                "app.name=test-service\n" +
                "abc.url=http://aat:8080\n"
    }

    def "generate properties for sit environment if specified in build.gradle"() {
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
                .withArguments('generateEnvironmentProperties', '--info', '-Penv=sit')
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

}