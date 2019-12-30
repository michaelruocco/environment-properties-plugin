package uk.co.mruoc.plugin.environment.properties

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class EnvironmentPropertiesPluginTest extends Specification {

    def "generate properties local environment if environment not specified"() {
        given:

        when:
        def result = GradleRunner.create()
                .withProjectDir(new File('src/test/resources/test-build'))
                .withArguments('generateEnvironmentProperties', '--info')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(":generateEnvironmentProperties").outcome == SUCCESS
        String contents = new File('build/environment.properties').text
        contents == "# generated from properties.yml for local environment\n" +
                "app.name=test-service\n" +
                "abc.url=http://localhost:8080\n"
    }

    def "generate properties for aat environment if specified by environment property"() {
        given:

        when:
        def result = GradleRunner.create()
                .withProjectDir(new File('src/test/resources/test-build'))
                .withArguments('generateEnvironmentProperties', '--info', '-Penv=aat')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(":generateEnvironmentProperties").outcome == SUCCESS
        String contents = new File('build/environment.properties').text
        contents == "# generated from properties.yml for aat environment\n" +
                "app.name=test-service\n" +
                "abc.url=http://aat:8080\n"
    }

    def "generate properties for sit environment if specified by environment property"() {
        given:

        when:
        def result = GradleRunner.create()
                .withProjectDir(new File('src/test/resources/test-build'))
                .withArguments('generateEnvironmentProperties', '--info', '-Penv=sit')
                .withPluginClasspath()
                .build()

        then:
        println result.output
        result.task(":generateEnvironmentProperties").outcome == SUCCESS
        String contents = new File('build/environment.properties').text
        contents == "# generated from properties.yml for sit environment\n" +
                "app.name=test-service-sit\n" +
                "abc.url=http://sit:8080\n"
    }

}