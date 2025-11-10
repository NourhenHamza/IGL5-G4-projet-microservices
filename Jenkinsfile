pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONAR_PROJECT_KEY = 'mon-projet-spring'
        SONAR_PROJECT_NAME = 'Mon Projet Spring Boot'
    }

    stages {
        stage('📥 Checkout Code') {
            steps {
                echo '🔄 Récupération du code source...'
                checkout scm
            }
        }

        stage('🧹 Clean') {
            steps {
                echo '🧹 Nettoyage du projet...'
                sh 'mvn clean'
            }
        }

        stage('🔨 Build') {
            steps {
                echo '🔨 Compilation du projet...'
                sh 'mvn compile'
            }
        }

        stage('🧪 Test') {
            steps {
                echo '🧪 Exécution des tests...'
                sh 'mvn test'
            }
        }

        stage('📦 Package') {
            steps {
                echo '📦 Création du JAR...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('🔍 SonarQube Analysis') {
            steps {
                echo '🔍 Analyse SonarQube...'
                withSonarQubeEnv('SonarQube-Local') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.projectName='${SONAR_PROJECT_NAME}' \
                        -Dsonar.host.url=${SONAR_HOST_URL}
                    """
                }
            }
        }

        stage('✅ Quality Gate') {
            steps {
                echo '✅ Vérification du Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline exécuté avec succès ! 🎉'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifiez les logs.'
        }
        always {
            echo '🧹 Nettoyage...'
            cleanWs()
        }
    }
}