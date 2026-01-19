pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 1, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                echo '========== Checking out source code =========='
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '========== Building the application =========='
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    echo 'Build completed successfully'
                }
                failure {
                    echo 'Build failed'
                    error('Maven build failed')
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '========== Running integration tests =========='
                sh 'mvn verify'
            }
            post {
                always {
                    echo 'Publishing test results'
                    junit 'target/surefire-reports/**/*.xml'
                }
                success {
                    echo 'Integration tests passed'
                }
                failure {
                    echo 'Integration tests failed'
                    error('Integration tests failed')
                }
            }
        }
    }

    post {
        always {
            echo '========== Pipeline execution completed =========='
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
