pipeline {
    agent any

    tools {
        maven 'Maven 3.9.15'
        jdk 'JDK21'
    }

    environment {
        // 'credentials()' fetches the secret by its ID
        WEBHOOK_SECRET = credentials('72dfba94-57a4-4bc5-8bea-8e4a4ef9f576')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'
                git branch: 'main',
                    url: 'https://github.com/PeterBulcke/devops-trends.git'
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

        stage('Deploy to Tomcat') {
            steps {
                echo 'Deploying to Tomcat...'
                deploy adapters: [tomcat9(credentialsId: 'tomcat-credentials', url: 'http://172.31.13.248:8081')], contextPath: 'devops-trends', war: 'target/*.war'
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
            echo 'App running at: http://13.39.88.130:8081/devops-trends'
        }
        failure {
            echo 'Pipeline failed. Check console output for details.'
        }
        always {
            echo 'Pipeline finished.'
        script {
            if (getContext(hudson.FilePath)) {
                cleanWs()
            }
        }   // or: deleteDir()
        }
    }
}
