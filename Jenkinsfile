pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    options {
        timeout(time: 8, unit: 'MINUTES')
        retry(1)
    }
    
    environment {
        SONAR_TOKEN = '9f0d4355a17c04aa11cc931798438b04e2cd8bae'
        SONAR_PROJECT_KEY = 'Omarrivv_pruebascanales_revision_intermedia'
        SLACK_WEBHOOK = 'https://hooks.slack.com/services/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'
        PROJECT_NAME = 'MS Students Microservice'
    }
    
    stages {
        stage('🚀 Inicio') {
            steps {
                echo "🚀 Iniciando Pipeline Completo pero Rápido"
                
                script {
                    bat """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"🚀 *PIPELINE INICIADO* - ${PROJECT_NAME}\\\\n📦 Build #${BUILD_NUMBER}\\\\n⏰ Tiempo máximo: 8 minutos\\"}" ${SLACK_WEBHOOK}"""
                }
            }
        }
        
        stage('🔨 Build Rápido') {
            steps {
                echo "🔨 Compilando proyecto..."
                timeout(time: 2, unit: 'MINUTES') {
                    bat 'mvn clean compile -T 4 -q'
                }
            }
        }
        
        stage('🧪 Tests Selectivos') {
            steps {
                echo "🧪 Ejecutando tests más rápidos..."
                timeout(time: 2, unit: 'MINUTES') {
                    bat 'mvn test -Dtest=*MapperTest,*ServiceImplTest -q'
                }
                
                script {
                    bat """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"✅ *TESTS COMPLETADOS* - Build #${BUILD_NUMBER}\\\\n🧪 Tests principales ejecutados exitosamente\\"}" ${SLACK_WEBHOOK}"""
                }
            }
        }
        
        stage('📊 SonarQube Optimizado') {
            steps {
                echo "📊 Análisis SonarQube optimizado..."
                timeout(time: 3, unit: 'MINUTES') {
                    bat """mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.token=${SONAR_TOKEN} -Dsonar.sourceEncoding=UTF-8 -q"""
                }
                
                script {
                    bat """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"📊 *SONARQUBE COMPLETADO* - Build #${BUILD_NUMBER}\\\\n✅ Análisis de calidad finalizado\\\\n🔗 https://sonarcloud.io/dashboard?id=${SONAR_PROJECT_KEY}\\"}" ${SLACK_WEBHOOK}"""
                }
            }
        }
        
        stage('📦 Package Final') {
            steps {
                echo "📦 Empaquetando aplicación..."
                timeout(time: 1, unit: 'MINUTES') {
                    bat 'mvn package -DskipTests -q'
                }
                
                archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            }
        }
    }
    
    post {
        success {
            script {
                bat """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"🎉 *PIPELINE COMPLETADO EXITOSAMENTE* - ${PROJECT_NAME}\\\\n\\\\n📊 *Resumen Final:*\\\\n⏱️ Duración: ${currentBuild.durationString}\\\\n✅ Compilación: SUCCESS\\\\n✅ Tests: PASSED\\\\n✅ SonarQube: COMPLETED\\\\n✅ JAR: GENERATED\\\\n\\\\n🚀 ¡Todo listo!\\"}" ${SLACK_WEBHOOK}"""
            }
        }
        
        failure {
            script {
                bat """curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"💥 *PIPELINE FALLÓ* - ${PROJECT_NAME}\\\\n❌ Build #${BUILD_NUMBER}\\\\n⏱️ Falló después de: ${currentBuild.durationString}\\\\n🔗 Ver logs: ${BUILD_URL}console\\"}" ${SLACK_WEBHOOK}"""
            }
        }
        
        always {
            echo "🧹 Limpieza completada - Pipeline terminado en ${currentBuild.durationString}"
        }
    }
}