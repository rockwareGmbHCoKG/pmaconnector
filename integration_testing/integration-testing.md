# Integration testing

This module contains a standalone jar application that performs IT tests against the middleware and the reststub
containers.
It also defines a set of standard classes to implement additional IT tests.

## How to run

After both the containers (pma_connector and reststub) are started, just run run_integration_tests.sh (on UNIX) or
run_integration_tests.bat (on Windows).
The customization defined in the parent folder (environment.sh or environment.bat) are correctly loaded.

## Warning

Running the integration_testing project will clear the execution status tables in middleware application.

## Add custom IT test

To add custom IT tests, you need to add a new value in
de.rockware.pma.integration.enumerations.TestName and add a new IntegrationTest
implementation.