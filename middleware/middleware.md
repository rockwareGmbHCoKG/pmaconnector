# PMA Connector - Middleware

## Parametrization

There are some utility scripts (tested on macOS and Windows) that can automatically remove previous images, build new
ones and start containers (with customizable parameters, such as ports, directories, and so on).

The application has been pre-configured to mount the following volumes:

<li><b>volumes/data_dir</b>: where pma_connector stores its DB (H2)</li>
<li><b>volumes/log_dir</b>: where pma_connector writes its logs</li>
<li><b>volumes/shared</b>: where pma_connector receives configuration, mappings and CSV files with data</li>

The application also uses the following ports:

<b>pma_connector</b> → HTTP: 8080, Debug (Java): 5005, SSH: 3020, FTP: 2020, FTP PASV: ports 61000-61050

<b>reststub</b> → HTTP: 8081, Debug (Java): 5006

And finally, it is configured to set the Europe/Rome timezone.

For further details and for changing these properties, it is possible to edit “environment.sh”  (for UNIX machines) or
“environment.bat” (for Windows machines) file.

## Run the container

### UNIX

If you need to rebuild the container (for instance, after a Git project update) → Run “build_and_run.sh” script. This
will build the project, create the container and start it accordingly with the customization defined in
“environment.sh”. This procedure will remove all the data stored in internal database (because the DB structure could be
changed).

If you only need to restart a previously stopped container (without updating the sources) → Run “run.sh” script. In that
way, all data will be maintained.

### Windows

f you need to rebuild the container (for instance, after a Git project update) → Run “build_and_run.bat” script. This
will build the projects, create the container and start it accordingly with the customization defined in
“environment.bat”. This procedure will remove all the data stored in internal database (because the DB structure could
be changed).

If you only need to restart a previously stopped container (without updating the sources) → Run “run.bat” script. In
that way, all data will be maintained.

## Start an execution

### UNIX

Run “copy_files.sh” script. This will copy the template CSV files from execution-resources directory to volumes/shared;
pma_connector checks every 60 seconds if there are csv files in that folder, and when it founds them, it processes them
automatically.

### Windows

Run “copy_files.bat” script. This will copy the template CSV files from execution-resources directory to volumes/shared;
pma_connector checks every 60 seconds if there are csv files in that folder, and when it founds them, it processes them
automatically.

### Manual execution

Copy the files you want to push to
SELECTED_SHARED_DIR_PATH.

## Reporting endpoints

All application endpoints are secured with basic auth. The default credentials are:

<li>USER: pmaconnector</li>
<li>PASSWORD: pmaconnectorpwd</li>

## Build and run only the middleware

Use the scripts build_and_run.sh (on UNIX) or build_and_run (on Windows) to build and start just the middleware
application. The customization defined in the parent folder (environment.sh or environment.bat) are correctly loaded.

## Authentication

First call to get the cookie header and authenticate to backend

http://localhost:8080/security/authenticate → POST method, no parameters

It returns:

200 OK if the credentials are valid and message “Authentication granted”

401 UNAUTHORIZED if credentials are not valid

## Execution status

Root information on previous execution grouped by campaign ID and delivery ID

http://localhost:8080/service/execution/status/get?campaignId=CAMPAIGN1&deliveryId=DELIVERY1 → GET method, get single
record with parameters:

campaignId: Campaign ID

deliveryId: Delivery ID

http://localhost:8080/service/execution/status/getPage?campaignId=CAMPAIGN1&deliveryId=DELIVERY1&startTime=2021-10-06T00:00:00.000&endTime=2021-10-07T00:00:00.000&page=0&size=100 →
GET method, paged data with parameters:

campaignId (optional): Campaign ID

deliveryId (optional): Delivery ID

startTime (optional): execution time greater or equal than (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

endTime: execution time less or equal than (optional) (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

page: page number (from 0 to max page number)

size: number of records of the page

## Execution status details

Details of each execution.

http://localhost:8080/service/execution/status/details/getPage?campaignId=CAMPAIGN1&deliveryId=DELIVERY1&startTime=2021-10-06T00:00:00.000&endTime=2021-10-07T00:00:00.000&page=0&size=100 →
GET method, paged data with parameters:

campaignId (optional): Campaign ID

deliveryId (optional): Delivery ID

startTime (optional): execution time greater or equal than (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

endTime: execution time less or equal than (optional) (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

page: page number (from 0 to max page number)

size: number of records of the page

## Execution errors

Errors related to specific execution, child of execution status table

http://localhost:8080/service/execution/status/error/getPage?campaignId=CAMPAIGN3&deliveryId=DELIVERY3&startTime=2021-10-06T00:00:00.000&endTime=2021-10-07T00:00:00.000&page=0&size=100 →
GET method, paged data with parameters:

detailsOid (optional): OID of related execution detail record

campaignId (optional): Campaign ID

deliveryId (optional): Delivery ID

startTime (optional): execution time greater or equal than (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

endTime: execution time less or equal than (optional) (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

page: page number (from 0 to max page number)

size: number of records of the page

## Transferred recipients

Data related to recipients transferred to PMA endpoints, child of execution status table

http://localhost:8080/service/execution/status/transferred-recipients/getPage?campaignId=CAMPAIGN1&deliveryId=DELIVERY1&startTime=2021-10-06T00:00:00.000&endTime=2021-10-07T00:00:00.000&page=0&size=100 →
GET method, paged data with parameters:

detailsOid (optional): OID of related execution detail record

campaignId (optional): Campaign ID

deliveryId (optional): Delivery ID

startTime (optional): execution time greater or equal than (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

endTime: execution time less or equal than (optional) (using format yyyy-MM-dd'T'HH:mm:ss.SSS)

page: page number (from 0 to max page number)

size: number of records of the page

## Fill DB with random data

Loads the database with random data. This endpoint is not active in production environments.

http://localhost:8080/service/execution/db/fill → GET method without parameters