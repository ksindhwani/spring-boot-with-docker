# spring-boot-with-docker
Personio Interview Coding assingment with Spring Boot and Docker

This Project is an imlementation of Spring Boot containarized using Docker. Following are the steps to setup the projects on Local.

## Prerequisite

Before Setting up the project, please ensure Docker Engine is installed on the machine

## Steps

- Clone the Project / Download the zip
- Unzip the Project 
- Open the Terminal / Command Line and go to directory of the Project by following command 
  - **cd spring-boot-with-docker**
- Run the following command in the project directory
  - **docker compose up**

  > You can also add **-d** option in the above command i.e **docker compose up -d** to run the setup task as dettached process

- It will start downloading the docker image of mysql and start building the application. This may take few minutes. (On Mac with Silicon M1, it took 6 minutes to setup the project at very first time)

  > We are using **port 9090** for the application, hence our url address would be **localhost:9090**

- After the command is successfully executed,open the browser and enter the following url.
  - http://localhost:9090/welcome
  - We are also using Http Basic authentication for our application hence if the application ask to enter username and password the please refer to [this](src/main/resources/application.properties) (Crdentials Property )
  - After sucessfull authentication , It should display **"welcome"** on the browser
  - This would mean that application is has been properly setup .

## Run Unit Tests
Use the following steps to run the unit tests in the application
- Go to Project Directory
- Run command **mvn test**

API Endpoints

Refer to the image folder in the repo for API endpoints , Request and Response contract





 
