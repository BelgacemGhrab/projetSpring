pipeline{
  agent any
  environment {
    POM = readMavenPom(file:'pom.xml')
    ARTIFACTID=POM.getArtifactId()
    ARTIFACT_VERSION = POM.getVersion()
    DOCKER_IMAGE_VERSION = "${env.BUILD_NUMBER}"
    DOCKER_SERVICE_NAME = "${ARTIFACTID}"
  }

   tools {
     maven 'maven'
     jdk 'java'
   }
  stages{
    stage("Build"){
      steps{
        bat "mvn clean package -DskipTests"
      }
    }
    stage("build docker image"){
      steps{
        bat "docker build -t ${ARTIFACTID} ."
        bat "docker tag ${ARTIFACTID}:latest ${ARTIFACTID}:${DOCKER_IMAGE_VERSION}"
      }
    }
    stage("deploy"){
      steps{
      bat "docker service update --force --image ${ARTIFACTID}:${DOCKER_IMAGE_VERSION} ${ARTIFACTID}"
      }
    }
  }
}
