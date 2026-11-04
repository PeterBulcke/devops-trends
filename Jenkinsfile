pipeline {
    agent any

    environment {
        TOMCAT_HOST       = '172.31.4.11'
        TOMCAT_WEBAPPS    = '/opt/apache-tomcat-9.0.98/webapps'
        TOMCAT_PUBLIC_URL = 'http://13.39.88.130:8080'
        CONTEXT_PATH      = 'devops-trends'
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
                echo "Deploying WAR via SCP to ${TOMCAT_HOST}..."
                withCredentials([sshUserPrivateKey(credentialsId: 'tomcat-ssh-key', keyFileVariable: 'SSH_KEY')]) {
                    sh '''
                        set -e
                        ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no ubuntu@''' + env.TOMCAT_HOST + ''' "rm -rf ''' + env.TOMCAT_WEBAPPS + '''/devops-trends*"
                        scp -i "$SSH_KEY" -o StrictHostKeyChecking=no target/devops-trends.war ubuntu@''' + env.TOMCAT_HOST + ''':''' + env.TOMCAT_WEBAPPS + '''/
                        echo "WAR copied. Tomcat will auto-deploy."
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
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
