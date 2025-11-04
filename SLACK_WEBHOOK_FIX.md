# 🔧 Solución Rápida - Slack Webhook Directo en Jenkinsfile

## Reemplaza las llamadas slackSend() con esto:

### En lugar de:
```groovy
slackSend(
    channel: '#jenkins-ci-cd-bot',
    color: 'good',
    message: 'Mensaje'
)
```

### Usa esto:
```groovy
script {
    def webhookUrl = 'https://hooks.slack.com/services/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'
    def message = '🚀 **JENKINS CI/CD** - Pipeline iniciado correctamente!'
    
    bat """
        curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"${message}\\"}" ${webhookUrl}
    """
}
```

## ✅ Beneficios:
- ✅ No depende del plugin Slack
- ✅ Funciona siempre con curl
- ✅ Control total sobre el mensaje
- ✅ Más confiable en producción

## 🚀 Implementación Rápida:

1. Crea función helper en Jenkinsfile:
```groovy
def sendSlackMessage(message, color = 'good') {
    def webhookUrl = env.SLACK_WEBHOOK
    def emoji = color == 'good' ? '✅' : (color == 'warning' ? '⚠️' : '❌')
    
    bat """
        curl -X POST -H "Content-type: application/json" --data "{\\"text\\":\\"${emoji} ${message}\\"}" ${webhookUrl}
    """
}
```

2. Úsala así:
```groovy
sendSlackMessage('Pipeline iniciado correctamente!', 'good')
sendSlackMessage('Tests fallaron!', 'danger')
```