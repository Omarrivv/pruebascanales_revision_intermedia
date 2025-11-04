pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    options {
        timeout(time: 8, unit: 'MINUTES')
        skipDefaultCheckout(false)
    }
    
    environment {
        SONAR_TOKEN = '455e4188da094abfc2ebd67a978455f99f2db738'
        SONAR_PROJECT_KEY = 'Omarrivv_pruebascanales_revision_intermedia'
        SLACK_WEBHOOK = 'https://hooks.slack.com/services/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'
        PROJECT_NAME = 'MS Students Microservice'
    }
    
    stages {
        stage('🚀 Checkout') {
            steps {
                echo "🚀 Iniciando Pipeline - ${PROJECT_NAME}"
                
                // Limpiar workspace y clonar repositorio
                sh 'rm -rf *'
                sh 'git clone https://github.com/Omarrivv/pruebascanales_revision_intermedia.git .'
                
                // Verificar que tenemos los archivos
                sh 'ls -la'
                
                script {
                    sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"🚀 PIPELINE INICIADO - ${PROJECT_NAME} Build #${BUILD_NUMBER} - Código descargado ✅\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
                }
            }
        }
        
        stage('🔨 Build') {
            steps {
                echo "🔨 Compilando proyecto..."
                
                timeout(time: 2, unit: 'MINUTES') {
                    sh 'mvn clean compile -q'
                }
                
                script {
                    sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"✅ BUILD COMPLETADO - ${PROJECT_NAME}\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
                }
            }
        }
        
        stage('🧪 Tests') {
            steps {
                echo "🧪 Ejecutando tests..."
                timeout(time: 1, unit: 'MINUTES') {
                    sh 'mvn test -q'
                }
                
                script {
                    sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"🧪 TESTS COMPLETADOS - ${PROJECT_NAME}\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
                }
            }
        }
        
        stage('� SonarCloud Analysis') {
            steps {
                script {
                    try {
                        echo '🔍 Iniciando análisis de calidad con SonarCloud...'
                        
                        sh """mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} -Dsonar.login=${env.SONAR_TOKEN} -q"""
                        
                        echo '✅ Análisis SonarCloud completado'
                        
                        // Notificar análisis completado
                        sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"🔍 SONAR ENVIADO - Esperando Quality Gate...\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
                    } catch (Exception e) {
                        sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"❌ ERROR EN SONAR - ${PROJECT_NAME}\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
                        throw e
                    }
                }
            }
        }
        
        stage('� Package') {
            steps {
                echo "� Empaquetando..."
                timeout(time: 1, unit: 'MINUTES') {
                    sh 'mvn package -DskipTests -q'
                }
                
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
                
                script {
                    sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"📦 PACKAGE COMPLETADO - ${PROJECT_NAME}\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
                }
            }
        }
    }
    
    post {
        success {
            script {
                sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"🎉 PIPELINE COMPLETADO EXITOSAMENTE - ${PROJECT_NAME} Build #${BUILD_NUMBER} ✅ Duración: ${currentBuild.durationString} 🔗 SonarCloud: https://sonarcloud.io/dashboard?id=${SONAR_PROJECT_KEY}\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
            }
        }
        
        failure {
            script {
                sh """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"� PIPELINE FALLÓ - ${PROJECT_NAME} Build #${BUILD_NUMBER} ❌ Ver logs: ${BUILD_URL}console\\"}" ${SLACK_WEBHOOK} || echo "Slack failed" """
            }
        }
        
        always {
            echo "🧹 Pipeline terminado"
        }
    }
}