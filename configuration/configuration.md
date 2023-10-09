# Configuration

This module contains a standalone jar application that creates the defined configurations and mappings in JSON format,
ready to be used with the application.

## How to run

<li>Build the application: run <i>mvn clean install</i>
<li>Call the entry point of the JAR: <i>java -jar target/configuration-0.0.1-jar-with-dependencies.jar \
MIDDLEWARE_SHARED_DIR_PATH CONFIGURATION_TYPE MAPPINGS_TYPE</i>

Its execution it is also invoked by the build and run scripts (build_and_run.sh/build_and_run.bat and run.sh/run.bat) in
the parent folder.

## Add custom configurations

To add custom configurations, you need to add a new value in
de.rockware.pma.connector.configuration.enumerations.ConfigurationType and add a new ConfigurationProvider
implementation.
To add custom mapping, you need to add a new value in de.rockware.pma.connector.mapping.enumerations,MappingsType and
add a new MappingsProvider implementation.
