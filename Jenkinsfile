pipeline {
    agent any

    tools {
        maven 'Maven 3.9.15'
        jdk   'JDK21'
    }

    environment {
        TOMCAT_HOST  = '172.31.6.68'
        TOMCAT_USER  = 'ubuntu'
        TOMCAT_HOME  = '/var/lib/tomcat10'
        TOMCAT_PORT  = '8080'
        APP_CONTEXT  = 'devops-trends'
        SSH_CRED_ID  = 'jenkins-tomcat-key'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling the application...'
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging into WAR file...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Preflight: Connectivity') {
            steps {
                echo "Checking TCP connectivity to ${TOMCAT_HOST}:22 ..."
                sh """
                    set -e
                    if ! nc -zv -w 5 ${TOMCAT_HOST} 22; then
                        echo "ERROR: Cannot reach ${TOMCAT_HOST}:22 from this Jenkins host."
                        exit 1
                    fi
                """
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying WAR to Tomcat 10 at ${TOMCAT_HOST} via SCP..."
                sshagent([env.SSH_CRED_ID]) {
                    sh """
                        set -e
                        mkdir -p ~/.ssh
                        ssh-keyscan -H ${TOMCAT_HOST} >> ~/.ssh/known_hosts 2>/dev/null || true

                        scp -o StrictHostKeyChecking=accept-new \
                            target/${APP_CONTEXT}.war \
                            ${TOMCAT_USER}@${TOMCAT_HOST}:/tmp/${APP_CONTEXT}.war

                        ssh -o StrictHostKeyChecking=accept-new \
                            ${TOMCAT_USER}@${TOMCAT_HOST} "
                                set -e
                                sudo mv -f /tmp/${APP_CONTEXT}.war ${TOMCAT_HOME}/webapps/${APP_CONTEXT}.war
                                sudo rm -rf ${TOMCAT_HOME}/webapps/${APP_CONTEXT}
                            "
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                echo "Verifying that ${APP_CONTEXT} is serving HTTP 200..."
                sh """
                    set -e
                    for i in \$(seq 1 30); do
                        if curl -sf -o /dev/null "http://${TOMCAT_HOST}:${TOMCAT_PORT}/${APP_CONTEXT}/"; then
                            echo "App is up at http://${TOMCAT_HOST}:${TOMCAT_PORT}/${APP_CONTEXT}/"
                            exit 0
                        fi
                        echo "Attempt \$i/30: app not yet responding, retrying in 2s..."
                        sleep 2
                    done
                    echo "ERROR: ${APP_CONTEXT} did not respond with HTTP 2xx after 60s."
                    echo "Check ${TOMCAT_HOME}/logs/catalina.out on ${TOMCAT_HOST}."
                    exit 1
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline finished. Deployment successful!"
            echo "App running at: http://${TOMCAT_HOST}:${TOMCAT_PORT}/${APP_CONTEXT}/"
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
        always {
            echo 'Workspace not cleaned (kept for inspection).'
        }
    }
}
