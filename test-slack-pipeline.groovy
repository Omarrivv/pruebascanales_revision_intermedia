pipeline {
    agent any
    
    stages {
        stage('Test Slack') {
            steps {
                script {
                    // Método 1: Con plugin Slack
                    try {
                        slackSend(
                            channel: '#jenkins-ci-cd-bot',
                            color: 'good',
                            message: '🧪 **TEST JENKINS-SLACK** ✅\nConfiguración funcionando correctamente!'
                        )
                    } catch (Exception e) {
                        echo "Slack plugin failed: ${e.message}"
                        
                        // Método 2: Con webhook directo
                        bat '''
                            curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"🧪 TEST desde Jenkins Pipeline - Webhook directo funcionando!\\"}" https://hooks.slack.com/services/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
                        '''
                    }
                }
            }
        }
    }
}