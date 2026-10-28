pipeline {
    agent any

    tools {
        maven 'Maven 3.9.15'
        jdk 'JDK21'
    }

    environment {
        TOMCAT_HOST       = '172.31.6.68'
        TOMCAT_USER       = 'ubuntu'
        TOMCAT_WEBAPPS    = '/var/lib/tomcat10/webapps'
        TOMCAT_SERVICE    = 'tomcat10'
        TOMCAT_PORT       = '8081'
        TOMCAT_PUBLIC_URL = 'http://13.39.88.130:8081'
        CONTEXT_PATH      = 'devops-trends'
        WAR_FILE          = 'target/devops-trends.war'
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
                echo "Deploying WAR to Tomcat at ${TOMCAT_HOST}..."
                sh """
                    set -e
                    scp ${WAR_FILE} ${TOMCAT_USER}@${TOMCAT_HOST}:/tmp/${CONTEXT_PATH}.war
                    ssh ${TOMCAT_USER}@${TOMCAT_HOST} "sudo mv /tmp/${CONTEXT_PATH}.war ${TOMCAT_WEBAPPS}/ && sudo systemctl restart ${TOMCAT_SERVICE}"
                """
                echo 'Waiting for Tomcat to finish deploying...'
                sleep 15
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Verifying the application responds...'
                sh """
                    set -e
                    for i in 1 2 3 4 5 6; do
                        code=\$(curl -s -o /dev/null -w '%{http_code}' http://${TOMCAT_HOST}:${TOMCAT_PORT}/${CONTEXT_PATH}/ || true)
                        if [ "\$code" = "200" ] || [ "\$code" = "302" ] || [ "\$code" = "403" ]; then
                            echo "App is up (HTTP \$code)"
                            exit 0
                        fi
                        echo "Attempt \$i: HTTP \$code — retrying in 5s"
                        sleep 5
                    done
                    echo "App did not respond in time"
                    exit 1
                """
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
            echo "Deployed to ${TOMCAT_HOST}:${TOMCAT_PORT}/${CONTEXT_PATH} (Tomcat private IP)"
            echo "View the app at ${TOMCAT_PUBLIC_URL}/${CONTEXT_PATH}"
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
        always {
            echo 'Pipeline finished. Cleaning workspace...'
            node('') {
                deleteDir()
            }
        }
    }
}
