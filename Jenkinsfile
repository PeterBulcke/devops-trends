pipeline {
    agent any

    environment {
        // Tomcat private IP (deploys)
        TOMCAT_URL = 'http://172.31.4.11:8080'

        // Tomcat Elastic IP (browsing)
        TOMCAT_PUBLIC_URL = 'http://13.39.88.130:8080'

        // Tomcat application path
        CONTEXT_PATH = 'devops-trends'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'

                git branch: 'main',
                    credentialsId: 'github-pat-jenkins',
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

                withCredentials([usernamePassword(
                    credentialsId: 'jenkins-deployer',
                    usernameVariable: 'TOMCAT_USER',
                    passwordVariable: 'TOMCAT_PASS'
                )]) {
                    sh '''
                        set -e
                        echo "Uploading WAR to ${TOMCAT_URL}/manager/text/deploy ..."
                        curl -sS --fail-with-body -m 60 \
                            --upload-file target/devops-trends.war \
                            -u "${TOMCAT_USER}:${TOMCAT_PASS}" \
                            "${TOMCAT_URL}/manager/text/deploy?path=/${CONTEXT_PATH}&update=true"
                        echo ""
                        echo "Deployment request completed."
                    '''
                }
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
            cleanWs()
        }
    }
}
