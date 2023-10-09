# PMA Connector - Middleware frontend

## Project architecture

The frontend folder has the following folders and files and I will mention on how to run the project after it so don´t
worry if I´m mentioning some code here:
build folder is created when we run any of these script for any environment either the development or the production
ones. npm run build:development or npm run build:production.
node-modules folder created once we run npm run as we are using node to run and build react project.

public folder is the public folder used by default from ReactJS as it has the main index.html there which include the
main div we are using to put in it the root component which we are using to render all the component we built in
src/components.

src folder is actually including everything we are building in this project.

assets folder includes all the css, scss, js and fonts added to this project.

components folder includes all the components that has been built in this project which I will explain it more deeply in
this document.

App.js file is called in index.js. the App.js calls the login component and template component by checking if the user
is loggedin and authorized from the local storage token then it direct the user to template component which includes
everything there.

index.js file calls the App.js component and rendering it to the root div.

package.json file has all the dependencies used in the project as mentioned before, whenever we install new library it
will be added automatically to the dependencies in package.json file so it can be installed by default when we run npm
install and it also will change the data inside package-lock.json every time we run the npm install command as it will
take all the data from packages.json. However, if you want to add new library or you want to change the version of any
dependency, change it or add it from the package.json and run npm install after it so it will automatically install it
for node modules and will update package-lock.json file.

package-lock.json file is updating each time we run npm install.

.env.development file that is used to defined the used variables in the development environment such as the base url for
the development environment which is localhost. whenever we run npm start it will take the variables used in this file
and use it in this project and all of that handled by env-cmd library.

.env.production file that is used to defined the used variables in the production environment such as the base url for
the production environment which is localhost for now but it will need to be changed when it is deployed to production
environment.

.gitignore file is used to ignore the track for some files mentioned in it.

README.md file as any project it included the readme file as a guide for the developers to start with.

## Docker image integration

There is a specific configuration using maven-assembly-plugin in the project pom that builds compresses the frontend
packages. After that, the Dockerfile contains the necessary command to add the frontend to the container.
This behaviour is controlled by the property MIDDLEWARE_FRONTEND_ACTIVE defined in environment.sh or enviroment.bat.
At the first startup of the container, if the frontend should be enabled, it will be installed and started.

## How to run the Project outside the Docker image?

After running all the BE environment here is what we do to run the project:

move to frontend directory cd frontend.

since we are using node to build everything, run npm install to install node and it will install all the dependencies
needed for the project from package.json.

to finally see something in the frontend part run npm start as it will start the project in port 3000.

if you want to add some changes for example new component there will be no need to re run the start command as it
already will watch the new changes. However, if you need to add or install new package you will need to restart the
project. as I mentioned bore if you ever run into for example change the version of a dependency from package.json you
will need to run npm install again.