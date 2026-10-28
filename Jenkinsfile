pipeline {
    agent any

    tools {
        maven 'Maven 3.9.15'
        jdk 'JDK21'
    }

    environment {
        TOMCAT_URL        = 'http://172.31.6.68:8081'      // private IP used by Jenkins to deploy
        TOMCAT_PUBLIC_URL = 'http://13.39.88.130:8081'     // public IP used to browse the app
        CONTEXT_PATH      = 'devops-trends'                // no leading slash
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'
                git branch: 'main',
                    credentialsId: 'github-pat',
                    url: 'https://github.com/PeterBulcke/devops-trends.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling the application...'
                sh 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn -B test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging into WAR file...'
                sh 'mvn -B package -DskipTests'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying to Tomcat EC2 at ${TOMCAT_URL}..."
                deploy adapters: [
                    tomcat9(
                        credentialsId: 'tomcat-credentials',
                        url: "${TOMCAT_URL}"
                    )
                ],
                contextPath: "${CONTEXT_PATH}",
                war: 'target/devops-trends.war'
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
            echo "Deployed to ${TOMCAT_URL}/${CONTEXT_PATH} (Tomcat private IP)"
            echo "View the app at ${TOMCAT_PUBLIC_URL}/${CONTEXT_PATH}"
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
        always {
            echo 'Pipeline finished. Cleaning workspace...'
            node {
                deleteDir()
            }
        }
    }
}
