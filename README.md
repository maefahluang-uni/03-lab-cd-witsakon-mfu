Continuous Deployment
=====================

This lab will explore how to build a docker image and deploy it to the Google Cloud with Cloud Build. The docker container will be executed on Cloud Run.

 # Setting up

 We will create a file named `Dockerfile` at project root folder `lab-web-cd`. Please edit the `Dockerfile` with the following:

 ```dockerfile
 # Build stage
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

# Package stage
FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
 ```

 > This Dockerfile contains 2 stages: Build and Package. At the Build stage, it will build a docker image with maven and java installed. Then, it will build the project and package it into a jar file (using `clean package` goal). At the Package stage it copies the jar file into a docker image with openjdk installed. This docker will run the jar file, which is the spring boot application.


# Build & Run Docker locally
 If you cannot do this step on your local machine, please use cloud shell. You need to clone this repository to your local machine/cloud shell using command `git clone <URL of your repository>`.

With Dockerfile in place, we can build our docker image locally.

```bash
docker build -t lab-cd .
```

You can run the docker image locally with the following command. It maps port 8100 on the container to port 8100 on the host machine.
```bash
docker run --rm -it -p 8100:8100 lab-cd:latest
```

> ***However, in this lab, we will build our docker image in Cloud Build and automatically deploy on Cloud Run.***

# Build & Run Docker on Google Cloud Platform (GCP)
We will use Cloud Build to build our docker image and deploy it to Cloud Run. Everytime we push the code to Github, Cloud Build will build the docker image and deploy it to Cloud Run.

## Setup CD workflow
we will set up continuous deployment in `cloudbuild.yaml`. In `substitutions`, at `_PROJECT_ID` edit `xxxxxx` with your project id, 

```yaml
steps:
 # Build the container image
 - name: 'gcr.io/cloud-builders/docker'
   args: [ 'build', '-t', 'us-central1-docker.pkg.dev/${_PROJECT_ID}/webdev-dockers/lab-cd:$COMMIT_SHA', '.' ]
 # Push the container image to Container Registry
 - name: 'gcr.io/cloud-builders/docker'
   args: ['push', 'us-central1-docker.pkg.dev${_PROJECT_ID}/webdev-dockers/lab-cd:$COMMIT_SHA']
 # Deploy container image to Cloud Run
 - name: 'gcr.io/cloud-builders/gcloud'
   entrypoint: gcloud
   args:
   - 'run'
   - 'deploy'
   - 'lab-cd'
   - '--image'
   - 'us-central1-docker.pkg.dev/${_PROJECT_ID}/webdev-dockers/lab-cd:$COMMIT_SHA'
   - '--region'
   - 'us-central1'
   - '--allow-unauthenticated'
   - '--platform'
   - 'managed'
   - '--port'
   - '8100'
images:
 - 'us-central1-docker.pkg.dev/${_PROJECT_ID}/webdev-dockers/lab-cd'

substitutions:
  _PROJECT_ID: 'xxxxxx'
```

> This `cloudbuild.yaml` contains 3 steps:
> 1. It build the docker image and labels as `us-central1-docker.pkg.dev/${_PROJECT_ID}/webdev-dockers/lab-cd`
> 2. It push it to Artifact repository at `webdev-dockers/lab-cd`, where `webdev-dockers` is the name of the repository and `lab-cd` is the name of the image.
> 3. It deploy the image to Cloud Run as `lab-cd` service.

## Go to Cloud Console
- go to cloud console by https://console.cloud.google.com

## Create Artifact repository
We need to create an artifact repository to store our docker image.
- On Cloud console, go to artifacts repository (You can use search box to find it)
- create a new repository
- name it `webdev-dockers`

## Connect to Repository
With Github repository, you can connect to Cloud Build to trigger build when you push to Github.
- On Cloud console, go to Cloud Build (You can use search box to find it)
- click repositories
- click connect repository
- select GitHub (Cloud Build GitHub App) and click continue. 
- authenticate to github. ***This could take a while, please wait.***
- select GitHub Account as `maefahluang-uni`
- Choose your repo `maefahluang-uni\lab-cd-xxxx`.
- click `Connect` button

## Create a trigger
We need to create a trigger that will trigger a build when we push to Github.
- go to Cloud Build
- click Triggers
- click `Create Trigger`
- name it `lab-cd`
- select Event as `Push to a branch`
- select Source as `1st gen`
- select Repository as `maefahluang-uni\lab-cd-xxxx`, where xxxx is your account on Github.


## Setting service account
To allow Cloud Build to deploy to Cloud Run, you need to grant the Cloud Build service account the Cloud Run Admin role.
- In Cloud build, go to Setting 
- enable Cloud Run Admin

## Try to run our CD workflow
- Update something in the code or README.md, commit and push to github.
- go to cloud build -> history, you should see a new build is triggered.
- go to cloud run, you should see a new service is deployed. Its name is `lab-cd`.
- click on the service, copy the url and open it in browser.
- with the url, put `/concerts` at the end of the url, you should see the list of concerts.
