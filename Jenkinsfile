pipeline {
    agent any

    parameters {
        booleanParam(name: 'CLEAN_MAVEN_CACHE', defaultValue: false, description: 'Limpiar caché de Maven antes del build')
    }

    tools {
        jdk 'Java17'
        maven 'M3'
    }

    environment {
    GITHUB_REPO = 'https://github.com/Omarrivv/pruebascanales_revision_intermedia.git'
        MAVEN_OPTS = '-Xmx1024m -Dmaven.wagon.http.retryHandler.count=3'
        // MAVEN_CONFIG se puede dejar fuera ya que usas las propiedades en los comandos sh
    }

    stages {
        stage('Checkout') {
            steps {
                echo '� Clonando repositorio...'
                git branch: 'develop-clean', url: "${GITHUB_REPO}"
                
                echo '🧹 Limpiando caché de Maven si es necesario...'
                script {
                    if (params.CLEAN_MAVEN_CACHE == true) {
                        sh 'rm -rf ~/.m2/repository'
                        echo '✅ Caché de Maven limpiada'
                    }
                }
            }
        }

        stage('Build & Compile') {
            steps {
                echo '⚙️ Compilando proyecto...'
                retry(3) {
                    sh '''
                        mvn compile \
                            -s maven-settings.xml \
                            -Dmaven.wagon.http.retryHandler.count=3 \
                            -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
                            -Dmaven.wagon.http.pool=false
                    '''
                }
            }
        }

        stage('Unit Tests & Jacoco') {
            steps {
                echo '🧪 Ejecutando pruebas unitarias con cobertura...'
                script {
                    try {
                        sh '''
                            mvn test jacoco:report \
                                -s maven-settings.xml \
                                -Dsurefire.failIfNoSpecifiedTests=false
                        '''
                        echo '✅ Pruebas unitarias completadas'
                    } catch (Exception e) {
                        echo "⚠️ Algunas pruebas unitarias fallaron: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    script {
                        // Publicar resultados de pruebas de Unit Tests
                        try {
                            // Usamos **/surefire-reports/TEST-*.xml para capturar el último run de tests
                            junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-*.xml' 
                            echo '✅ Resultados de pruebas unitarias publicados'
                        } catch (Exception e) {
                            echo "⚠️ Error publicando resultados de pruebas: ${e.getMessage()}"
                        }

                        // Publicar reporte de cobertura JaCoCo
                        try {
                            if (fileExists('target/site/jacoco/jacoco.xml')) {
                                jacoco execPattern: 'target/jacoco.exec'
                                echo '✅ Reporte de cobertura JaCoCo publicado'
                            } else {
                                echo '⚠️ No se encontró el reporte de JaCoCo'
                            }
                            
                            // También archivar los reportes HTML
                            if (fileExists('target/site/jacoco/index.html')) {
                                archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
                                echo '✅ Reportes HTML de JaCoCo archivados'
                            }
                        } catch (Exception e) {
                            echo "⚠️ Error publicando cobertura: ${e.getMessage()}"
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '🔗 Ejecutando pruebas de integración...'
                script {
                    try {
                        // EJECUCIÓN: No usar 'clean'
                        sh '''
                            mvn failsafe:integration-test \
                                -s maven-settings.xml \
                                -Dtest=*IntegrationTest,*PerformanceTest \
                                -Dsurefire.failIfNoSpecifiedTests=false
                        '''
                        echo '✅ Pruebas de integración completadas'
                    } catch (Exception e) {
                        echo "⚠️ Algunas pruebas de integración fallaron: ${e.getMessage()}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    script {
                        try {
                            // Usar el plugin 'failsafe' para Integration Tests
                            junit allowEmptyResults: true, testResults: 'target/failsafe-reports/TEST-*.xml' 
                            echo '✅ Resultados de integración publicados'
                        } catch (Exception e) {
                            echo "⚠️ Error publicando resultados de integración: ${e.getMessage()}"
                        }
                    }
                }
            }
        }

        stage('Code Analysis') {
            steps {
                echo '🔍 Analizando código con SonarCloud...'
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                    sh '''
                        mvn package sonar:sonar \
                            -s maven-settings.xml \
                            -Dsonar.projectKey=Omarrivv_pruebascanales_revision_intermedia \
                            -Dsonar.organization=omarrivv \
                            -Dsonar.host.url=https://sonarcloud.io \
                            -Dsonar.token=$SONAR_TOKEN \
                            -DskipTests=true
                    '''
                }
            }
        }
    }

    post {
        always {
            echo '🧹 Limpiando workspace...'
            cleanWs()
        }
        success {
            echo '✅ Pipeline ejecutado con éxito!'
            slackSend(
                channel: '#jenkins-ci-cd-bot',
                color: 'good',
                message: """
                ✅ *BUILD EXITOSO*
                Proyecto: *${env.JOB_NAME}*
                Build: *#${env.BUILD_NUMBER}*
                Ver detalles: ${env.BUILD_URL}
                """
            )
        }
        failure {
            echo '❌ Pipeline falló!'
            slackSend(
                channel: '#jenkins-ci-cd-bot',
                color: 'danger',
                message: """
                ❌ *BUILD FALLIDO*
                Proyecto: *${env.JOB_NAME}*
                Build: *#${env.BUILD_NUMBER}*
                *${currentBuild.result}* - Ver detalles: ${env.BUILD_URL}
                """
            )
        }
    }
}