pipeline {
    agent any

    environment {
        // Tomcat private IP (deploys)
        TOMCAT_URL = 'http://172.31.7.244:8080'

        // Tomcat Elastic IP (browsing)
        TOMCAT_PUBLIC_URL = 'http://13.39.88.130:8080'

        // Tomcat application path
        CONTEXT_PATH = '/devops-trends'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'

                // Use the GitHub credential ID
                git branch: 'main',
                    credentialsId: 'github-pat-jenkins',
                    url: 'https://github.com/PeterBulcke/devops-trends.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling the application...'

                // Compile with Maven
                sh 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'

                // Run Maven tests
                sh 'mvn -B test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging into WAR file...'

                // Package WAR without rerunning tests
                sh 'mvn -B package -DskipTests'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying to Tomcat EC2 at ${TOMCAT_URL}..."

                // Use the Deploy to container plugin
                // Use the Tomcat credential ID
                deploy adapters: [
                    tomcat9(
                        credentialsId: 'jenkins-deployer',
                        url: "${TOMCAT_URL}"
                    )
                ],

                // Set the deployment context path
                contextPath: "${CONTEXT_PATH}",
                war: 'target/devops-trends.war'
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
            echo "Deployed to ${TOMCAT_URL}${CONTEXT_PATH} (Tomcat private IP)"
            echo "View the app at ${TOMCAT_PUBLIC_URL}${CONTEXT_PATH}"
        }

        failure {
            echo 'Pipeline failed. Check console output for details.'
        }

        always {
            echo 'Pipeline finished. Cleaning workspace...'
            cleanWs()
        }
    }
}
