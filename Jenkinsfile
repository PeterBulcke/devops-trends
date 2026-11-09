pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        // Tomcat private IP (deploys)
        TOMCAT_URL = 'http://172.31.37.215:8080'
        // Tomcat Elastic IP (browsing)
        TOMCAT_PUBLIC_URL = 'http://13.39.88.130:8080'
        // Tomcat application path
        CONTEXT_PATH = '/devops-trends'
        // Folder that holds pom.xml ('.' = top level of the repository)
        APP_DIR = '.'
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
                dir("${APP_DIR}") {
                    sh 'mvn -B clean compile'
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
                dir("${APP_DIR}") {
                    sh 'mvn -B test'
                }
            }
        }
        stage('Package') {
            steps {
                echo 'Packaging into WAR file...'
                dir("${APP_DIR}") {
                    sh 'mvn -B package -DskipTests'
                }
            }
        }
        stage('Deploy to Tomcat') {
            steps {
                echo "Deploying to Tomcat 10 at ${TOMCAT_URL}..."
                withCredentials([usernamePassword(credentialsId: 'TomcatCreds', usernameVariable: 'TC_USER', passwordVariable: 'TC_PASS')]) {
                    sh '''
                        RESPONSE=$(curl -sS -u "$TC_USER:$TC_PASS" -T "$APP_DIR/target/devops-trends.war" "$TOMCAT_URL/manager/text/deploy?path=$CONTEXT_PATH&update=true")
                        echo "$RESPONSE"
                        echo "$RESPONSE" | grep -q '^OK'
                    '''
                }
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
            deleteDir()
        }
    }
}