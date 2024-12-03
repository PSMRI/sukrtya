pipeline {
    agent any
    
    environment {
        registry = "050752608385.dkr.ecr.ap-south-1.amazonaws.com/piramal/tech4gov"    
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/nileshmp/springboot-sample.git']])
            }
        }
        stage('Build') {
            steps {
                sh "mvn clean install"
            }
        }
        stage("Dockerize") {
            steps {
                script {
                    dockerImage = docker.build registry
                    dockerImage.tag("$BUILD_NUMBER")
                }
            }
        }
    }
}
            

