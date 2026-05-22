pipeline {
    agent any

    tools {
        maven 'Maven 3.9.15'
        jdk 'JDK21'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'
                git branch: 'main',
                    credentialsId: 'GitHubCreds',
                    url: 'https://github.com/PeterBulcke/devops-trends.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling the application...'
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging into WAR file...'
                bat 'mvn package -DskipTests'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo 'Deploying to local Tomcat...'
                deploy adapters: [
                    tomcat9(
                        credentialsId: 'TomcatCreds',
                        url: 'http://localhost:8080'
                    )
                ],
                contextPath: '/devops-trends',
                war: 'target/devops-trends.war'
            }
        }

    }

    post {
        success {
            echo 'Deployment successful!'
            echo 'App running at: http://localhost:8080/devops-trends'
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
        always {
            echo 'Pipeline finished. Cleaning workspace...'
            deleteDir()
        }
    }
}