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
                        echo "Check the Tomcat instance's Security Group inbound rule for TCP/22"
                        echo "from this Jenkins host's IP, and the subnet Network ACL."
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

                        # 1) Seed known_hosts so scp does not fail on first contact.
                        mkdir -p ~/.ssh
                        ssh-keyscan -H ${TOMCAT_HOST} >> ~/.ssh/known_hosts 2>/dev/null || true

                        # 2) Copy WAR to the target host's /tmp.
                        scp -o StrictHostKeyChecking=accept-new \
                            target/${APP_CONTEXT}.war \
                            ${TOMCAT_USER}@${TOMCAT_HOST}:/tmp/${APP_CONTEXT}.war

                        # 3) Atomically replace the WAR in Tomcat 10's webapps dir,
                        #    then remove the exploded directory so Tomcat unpacks
                        #    the fresh WAR on next detection.
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
    }

    post {
        success {
            echo 'Pipeline finished. Deployment successful!'
            echo "App should be running at: http://${TOMCAT_HOST}:${TOMCAT_PORT}/${APP_CONTEXT}/"
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
        always {
            echo 'Workspace not cleaned (kept for inspection).'
        }
    }
}
