pipeline {
    agent any
    
    options {
        timeout(time: 3, unit: 'MINUTES')
        skipDefaultCheckout()
    }
    
    environment {
        SLACK_WEBHOOK_URL = 'https://hooks.slack.com/services/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'
        PROJECT_NAME = 'MS Students Fast'
    }
    
    stages {
        stage('⚡ Quick Setup') {
            steps {
                echo "⚡ Pipeline Rápido Iniciado"
                checkout scm
                
                script {
                    bat """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"⚡ PIPELINE RÁPIDO #${BUILD_NUMBER} - INICIADO\\"}" ${SLACK_WEBHOOK_URL}"""
                }
            }
        }
        
        stage('🏃 Fast Test') {
            steps {
                echo "🏃 Ejecutando solo 1 test rápido..."
                
                script {
                    if (isUnix()) {
                        sh 'mvn test -Dtest=StudentMapperTest -q'
                    } else {
                        bat 'mvn test -Dtest=StudentMapperTest -q'
                    }
                }
            }
        }
        
        stage('📦 Quick Package') {
            steps {
                echo "📦 Empaquetado rápido..."
                
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests -q'
                    } else {
                        bat 'mvn package -DskipTests -q'
                    }
                }
            }
        }
        
        stage('🎯 Mock Analysis') {
            steps {
                echo "🎯 Simulando análisis SonarCloud..."
                sleep 3
                echo "✅ Análisis completado (mock)"
            }
        }
    }
    
    post {
        always {
            script {
                def status = currentBuild.result ?: 'SUCCESS'
                def emoji = status == 'SUCCESS' ? '✅' : '❌'
                def duration = currentBuild.durationString.replace(' and counting', '')
                
                bat """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"${emoji} PIPELINE RÁPIDO TERMINADO\\\\n⏱️ Duración: ${duration}\\\\n📊 Estado: ${status}\\"}" ${SLACK_WEBHOOK_URL}"""
            }
        }
    }
}