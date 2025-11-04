# 💬 Guía Rápida de Configuración Slack + Jenkins

## 🚀 **Configuración Express (5 minutos)**

### **Paso 1: Crear Canal en Slack**
```
1. Abrir Slack → workspace "Vallegrande"
2. Crear canal: #jenkins-ci-cd-bot
3. Agregar descripción: "🤖 Notificaciones CI/CD - MS Students Pipeline"
4. Invitar miembros del equipo de desarrollo
```

### **Paso 2: Configurar Jenkins App**
```
1. Ir a: https://vallegrande.slack.com/apps
2. Buscar: "Jenkins CI"
3. Click: "Add to Slack"
4. Configurar:
   - Default channel: #jenkins-ci-cd-bot
   - Notifications: All builds
   - Token: [Copiar el token generado]
```

### **Paso 3: Configurar en Jenkins**
```
Manage Jenkins → Configure System → Slack:
├── Team Subdomain: vallegrande
├── Channel: #jenkins-ci-cd-bot  
├── Token: [Pegar token de Slack]
└── Test Connection ✅
```

---

## 🔧 **Configuración Avanzada con Webhook**

### **Crear Slack App Personalizada:**

```json
{
  "name": "MS Students CI/CD Bot",
  "description": "Bot personalizado para notificaciones de pipeline",
  "features": {
    "incoming_webhooks": true,
    "slash_commands": false,
    "bot_users": true
  },
  "oauth_config": {
    "scopes": {
      "bot": ["incoming-webhook", "chat:write", "chat:write.public"]
    }
  }
}
```

### **Webhook URL Example:**
```
https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX
```

---

## 📱 **Plantillas de Mensajes Slack**

### **Mensaje de Inicio:**
```json
{
  "channel": "#jenkins-ci-cd-bot",
  "username": "Jenkins CI/CD Bot",
  "icon_emoji": ":rocket:",
  "attachments": [
    {
      "color": "good",
      "title": "🚀 Pipeline Iniciado",
      "fields": [
        {
          "title": "Proyecto",
          "value": "MS Students Microservice",
          "short": true
        },
        {
          "title": "Build",
          "value": "#${BUILD_NUMBER}",
          "short": true
        },
        {
          "title": "Branch", 
          "value": "${GIT_BRANCH}",
          "short": true
        },
        {
          "title": "Commit",
          "value": "${GIT_COMMIT[0..7]}",
          "short": true
        }
      ],
      "footer": "Jenkins CI/CD",
      "ts": "${currentBuild.timeInMillis / 1000}"
    }
  ]
}
```

### **Mensaje de Éxito:**
```json
{
  "channel": "#jenkins-ci-cd-bot",
  "attachments": [
    {
      "color": "good",
      "title": "✅ Pipeline Completado Exitosamente",
      "text": "Todos los tests pasaron y el código cumple con los estándares de calidad",
      "fields": [
        {
          "title": "📊 Cobertura",
          "value": "75.2%",
          "short": true
        },
        {
          "title": "🧪 Tests",
          "value": "17/17 ✅",
          "short": true
        },
        {
          "title": "⏱️ Duración",
          "value": "${currentBuild.durationString}",
          "short": true
        },
        {
          "title": "🔍 Quality Gate",
          "value": "PASSED ✅", 
          "short": true
        }
      ],
      "actions": [
        {
          "type": "button",
          "text": "Ver Reporte SonarQube",
          "url": "https://sonarcloud.io/dashboard?id=pruebascanales_revision_intermedia"
        },
        {
          "type": "button", 
          "text": "Ver Build",
          "url": "${BUILD_URL}"
        }
      ]
    }
  ]
}
```

---

## ⚡ **Script de Configuración Rápida**

Crea este archivo y ejecútalo para configuración automática:

```bash
#!/bin/bash
# slack-jenkins-setup.sh

echo "🚀 Configurando integración Slack + Jenkins..."

# Variables (EDITAR ESTAS)
SLACK_WEBHOOK="https://hooks.slack.com/services/YOUR/WEBHOOK/URL" 
JENKINS_URL="http://localhost:8080"
JENKINS_USER="admin"
JENKINS_TOKEN="your-jenkins-api-token"

# Crear configuración XML para Slack
cat > slack-config.xml << EOF
<?xml version='1.0' encoding='UTF-8'?>
<jenkins.plugins.slack.SlackNotifier_-DescriptorImpl>
  <teamDomain>vallegrande</teamDomain>
  <token>${SLACK_WEBHOOK}</token>
  <room>#jenkins-ci-cd-bot</room>
  <sendAs>Jenkins CI/CD Bot</sendAs>
</jenkins.plugins.slack.SlackNotifier_-DescriptorImpl>
EOF

# Aplicar configuración via Jenkins CLI
java -jar jenkins-cli.jar -s $JENKINS_URL -auth $JENKINS_USER:$JENKINS_TOKEN \
  configure-global-security < slack-config.xml

echo "✅ Configuración Slack aplicada"
echo "🎯 Canal configurado: #jenkins-ci-cd-bot"
echo "🔗 Webhook configurado: ${SLACK_WEBHOOK:0:50}..."

# Test de conexión
curl -X POST -H 'Content-type: application/json' \
--data '{"text":"🎉 Jenkins CI/CD Bot configurado correctamente!"}' \
$SLACK_WEBHOOK

echo "📱 Mensaje de prueba enviado a Slack"
```

---

## 🎯 **Casos de Uso de Notificaciones**

### **1. Pipeline Exitoso:**
```
✅ BUILD SUCCESS #42 
📊 Coverage: 75.2% | 🧪 Tests: 17/17 | ⏱️ 3m 45s
🔗 [View Reports] [Deploy to Staging]
```

### **2. Pipeline Fallido:**
```
❌ BUILD FAILED #43
💥 Stage: SonarQube Analysis  
🐛 Issues: 2 bugs, 1 security hotspot
🔗 [View Logs] [Fix Issues]
```

### **3. Quality Gate Warning:**
```
⚠️ QUALITY GATE WARNING #44
📉 Coverage dropped to 68% (< 70% required)
👀 Action needed: Add more unit tests
🔗 [View Coverage Report]
```

### **4. Performance Issues:**
```
🐌 PERFORMANCE ALERT #45
⚡ JMeter: Avg response time 850ms (> 500ms threshold)
🔍 Bottleneck detected in /api/v1/students endpoint  
🔗 [View JMeter Report]
```

---

## 📊 **Dashboard Slack (Opcional)**

### **Crear Slash Command:**
```
Command: /jenkins-status
URL: http://your-jenkins/slack/status
Description: Ver estado actual del pipeline MS Students
Usage: /jenkins-status [build-number]
```

### **Respuesta del Command:**
```
🏗️ MS Students Pipeline Status

📊 Last Build: #45 ✅ SUCCESS
⏱️ Duration: 3m 22s  
🕐 Finished: 2 minutes ago

📈 Metrics:
├── Tests: 17/17 passed ✅
├── Coverage: 76.1% ✅  
├── Quality Gate: PASSED ✅
└── Performance: Good ✅

🔗 [Full Report] | [Trigger New Build]
```

---

¡**Configuración Slack lista en minutos!** 💬🚀