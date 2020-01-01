# Environment Properties Plugin

[![Build Status](https://travis-ci.org/michaelruocco/environment-properties-plugin.svg?branch=master)](https://travis-ci.org/michaelruocco/environment-properties-plugin)

This plugin intends to make the management of environment specific properties easier, often it is a pain if you have
and environment specific properties that is the same in a number of environments but needs to vary in one or a couple
of environments. This can lead to duplication for those environments where the value remains the same, which is the
main problem this plugin attempts to address.

## How does it work

It does this by using a single yaml file for all your environment properties definitions, but the structure of this file
means that it is possible to specify default values for any property you like, but also provide environment specific
values where they are required. A very basic example is shown below:

```yaml
properties:
  default: &config-default
    app.name: test-service
    abc.url: http://localhost:8080

environments:

  local:
    <<: *config-default

  aat: &config-aat
    <<: *config-default

    app.name: aat-service

  aat1:
    <<: *config-aat

    abc.url: http://aat1:8081

  aat2:
    <<: *config-aat

    abc.url: http://aat2:8082

  sit:
    <<: *config-default

    app.name: test-service-sit
    abc.url: http://sit:8080
```

In the above example, the properties files produced for each environment would be as
follows.

For the local environment, there are no environment specific values, so all default
values are used

```properties
# generated from properties.yml for local environment
app.name=test-service
abc.url=http://localhost:8080
```

For the aat environment, abc.url is not specified so the default value is used and
there is an environment specific value for app.name so that is used instead

```properties
# generated from properties.yml for aat environment
app.name=aat-service
abc.url=http://localhost:8080
```

For the sit environment, environment specific values are provided for both app.name and
abc.url so both of those values are used and no default values are picked up

```properties
# generated from properties.yml for local environment
app.name=test-service-sit
abc.url=http://sit:8080
```

### Cascading

You don't just have to extend from the default properties, it is also possible for one
environment to use another as its default values, in the above examples, the aat, aat1
and aat2 configurations demonstrate this. The snippet below gives the aat config the name
config-aat (this is done by adding the &config-aat after the initial definition) this is
done so that it can be referenced as a default for the aat1 and aat2 configs. The next
line (<<: *config-default) states that the default config should be inherited for the 
aat config:

```yaml
  aat: &config-aat
    <<: *config-default
```

The aat1 and aa2 configs use the same config to inherit from the aat config, this is
shown again in the snippets below:

```yaml
  aat1:
    <<: *config-aat
```

```yaml
  aat2:
    <<: *config-aat
```

As mentioned above, for the aat environment, abc.url is not specified so the default value is used and
there is an environment specific value for app.name so that is used instead

```properties
# generated from properties.yml for aat environment
app.name=aat-service
abc.url=http://localhost:8080
```

For the aat1 environment, app.name is not specified so the aat value is used and
there is an environment specific value for abc.url so that is used instead

```properties
# generated from properties.yml for aat1 environment
app.name=aat-service
abc.url=http://aat1:8081
```

For the aat2 environment, again app.name is not specified so the aat value is used and
there is an environment specific value for abc.url so that is used instead

```properties
# generated from properties.yml for aat2 environment
app.name=aat-service
abc.url=http://aat2:8082
```

## Usage

If you are using gradle version < 2.1 (you should upgrade!)

```gradle
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.github.michaelruocco:environment-properties-plugin:0.1.0'
    }
}

apply plugin: 'com.github.michaelruocco:environment-properties'
```

or alternatively:

```gradle
plugins {
    id 'com.github.michaelruocco.environment-properties-plugin' version '0.1.0'
}
```

The plugin will add two new tasks to your gradle build:

* generateEnvironmentProperties
* generateMultipleEnvironmentProperties

As you would expect, the first task can be used to create a single environment
properties file, the second can generate one or more environment properties files.
Which you choose to use will depend on how your project and CI process is set up.

### Generating a single environment properties file

If you want to create a single environment properties file then you can set up the
task by telling it the path to the yaml file to use as input and the path to where
you would like your properties file to be generated, finally you need to provide an
argument which specifies which environment you want to build the properties for.

```gradle
generateEnvironmentProperties {
    yamlPath = 'properties.yml'
    environment = 'aat'
    propertiesPath = 'config/environment.properties'
}
```

When this task is run it will generate the properties file at config/environment.properties
which will contain the values for the aat environment. Note - you will most likely want to
specific the environment value from some kind of dynamic value, e.g. passed by a gradle property
or maybe read from an environment variable rather than hardcoding, the hardcoded example above
is just for simplicity and to demonstrate the way the plugin works.

### Generating multiple environment properties file

It is also possible to generate multiple environment properties files if required, the task
configuration is similar to the above example with a few tweaks:

```gradle
generateMultipleEnvironmentProperties {
    yamlPath = 'properties.yml'
    environments = [ 'aat1', 'sit' ]
    propertiesPath = 'config/{{env}}/environment.properties'
}
```

The yaml path is obviously the same as it was for the previous task, it specifies the
location of the yaml file to read the property values above. For this task it is possible
to specify multiple environments, to do this it is also wise to add an environment placeholder
in the properties path, otherwise the plugin will simply overwrite each property file, meaning
that only one would be produced! In the example above, the task would generate two properties
files at the following locations:

* config/aat1/environment.properties
* config/sit/environment.properties

## Running the Tests

You can run the unit and tests for this project by running the following command:

```
./gradlew clean build
```

## Checking dependencies

You can check the current dependencies used by the project to see whether
or not they are currently up to date by running the following command:

```
./gradlew dependencyUpdates
```