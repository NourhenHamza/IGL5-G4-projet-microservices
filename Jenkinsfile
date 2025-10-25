// Declarative Jenkins pipeline for the project
// - Cross-platform: uses 'sh' on Unix agents and 'bat' on Windows agents
// - Runs the Maven wrapper (mvnw / mvnw.cmd) to build and run tests
// - Publishes JUnit test results and archives the build artifact
// Notes for Jenkins admin:
// - Install the 'JaCoCo' plugin if you want to publish coverage in Jenkins UI.
// - Configure a JDK and Maven tool in Jenkins global configuration and update the tool names below if desired.

pipeline {
    agent any

    environment {
        // Use these if you configured tools in Jenkins; otherwise Maven wrapper will be used
        // MAVEN_HOME = "Maven3"
        // JAVA_HOME = "JDK11"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh './mvnw -B -U clean test'
                    } else {
                        bat '.\\mvnw.cmd -B -U clean test'
                    }
                }
            }
            post {
                always {
                    // Publish JUnit test results
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'

                    // Archive JaCoCo exec file so it can be picked up by JaCoCo plugin if installed
                    archiveArtifacts artifacts: 'target/jacoco.exec', allowEmptyArchive: true
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    if (isUnix()) {
                        sh './mvnw -B -U -DskipTests package'
                    } else {
                        bat '.\\mvnw.cmd -B -U -DskipTests package'
                    }
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finished. See JUnit/JaCoCo reports in the job artifacts or use Jenkins plugins to display them."
        }
    }
}
