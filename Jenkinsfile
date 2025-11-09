pipeline {
    agent any
    
    environment {
        SONAR_HOST_URL = 'http://host.docker.internal:9000'
        SONAR_PROJECT_KEY = 'mon-projet-spring'
        JAVA_HOME = '/opt/java/openjdk'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        MAVEN_OPTS = '-Dmaven.compiler.source=17 -Dmaven.compiler.target=17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                git branch: 'feat/Salle', url: 'https://github.com/nermineH123/IGL5-G4-projet.git'
            }
        }
        
        stage('Setup') {
            steps {
                echo '⚙️ Configuration de l\'environnement...'
                sh 'chmod +x mvnw'
                echo '🔍 Diagnostic Java:'
                sh '''
                    echo "=== Java Version ==="
                    java -version
                    echo "=== Javac Version ==="
                    javac -version
                    echo "=== JAVA_HOME ==="
                    echo $JAVA_HOME
                    echo "=== PATH ==="
                    echo $PATH
                '''
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Compilation du projet...'
                sh '''
                    export JAVA_HOME=/opt/java/openjdk
                    export PATH=$JAVA_HOME/bin:$PATH
                    ./mvnw -U clean compile -X 2>&1 | head -100
                '''
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 Exécution des tests...'
                sh './mvnw test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Création du package...'
                sh './mvnw package -DskipTests'
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyse SonarQube en cours...'
                withSonarQubeEnv('SonarQube-Local') {
                    sh """
                        ./mvnw sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=${SONAR_HOST_URL}
                    """
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                echo '🚦 Vérification du Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            echo "⚠️ Quality Gate échoué: ${qg.status}"
                        } else {
                            echo "✅ Quality Gate réussi!"
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ =========================================='
            echo '✅ Pipeline terminé avec succès!'
            echo '✅ =========================================='
            echo "📊 Consultez les résultats sur:"
            echo "   ${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
        }
        failure {
            echo '❌ =========================================='
            echo '❌ Pipeline échoué!'
            echo '❌ =========================================='
            echo 'Consultez les logs ci-dessus pour plus de détails'
        }
        always {
            echo '🧹 Nettoyage terminé'
        }
    }
}
