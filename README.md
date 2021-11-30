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
- After the command is successfully executed,open the browser and enter the following url.
