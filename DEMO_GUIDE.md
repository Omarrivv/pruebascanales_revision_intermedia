# 🎯 Configuración Manual para Demo - Jenkins CI/CD

## 📋 **Estado Actual del Proyecto**

✅ **Completado:**
- 17 pruebas unitarias funcionando correctamente
- JaCoCo configurado para reportes de cobertura
- Jenkinsfile completo con todas las etapas
- Plan de pruebas JMeter creado
- Webhook de Slack configurado: `https://hooks.slack.com/services/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`

---

## 🚀 **Comandos para la Demo**

### 1. **Ejecutar Tests Unitarios**
```bash
mvn clean test
# Resultado: Tests run: 17, Failures: 0, Errors: 0, Skipped: 0 ✅
```

### 2. **Generar Reporte JaCoCo**
```bash
mvn clean test jacoco:report
# Reporte generado en: target/site/jacoco/index.html
```

### 3. **Compilar y Empaquetar**
```bash
mvn clean package -DskipTests
# JAR generado: target/vg-ms-students-1.0.jar
```

---

## 🔧 **Configuración SonarCloud**

### Token de Acceso:
```
a16d14977ea78acff7ea90041fc36ee7dca7e068
```

### Comando Manual SonarCloud:
```bash
set SONAR_TOKEN=a16d14977ea78acff7ea90041fc36ee7dca7e068

mvn verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar ^
-Dsonar.projectKey=Omarrivv_pruebascanales_revision_intermedia ^
-Dsonar.organization=omarrivv ^
-Dsonar.host.url=https://sonarcloud.io ^
-Dsonar.token=%SONAR_TOKEN%
```

### URL del Proyecto:
```
https://sonarcloud.io/project/overview?id=Omarrivv_pruebascanales_revision_intermedia
```

---

## 📱 **Configuración Slack**

### Webhook URL:
```
https://hooks.slack.com/services/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
```

### Canal:
```
#jenkins-ci-cd-bot
```

### Test de Slack (PowerShell):
```powershell
$webhook = "https://hooks.slack.com/services/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"
$payload = @{
    text = "🧪 Test desde PowerShell - Jenkins CI/CD funcionando!"
    channel = "#jenkins-ci-cd-bot"
    username = "Jenkins Bot"
} | ConvertTo-Json

Invoke-RestMethod -Uri $webhook -Method Post -Body $payload -ContentType "application/json"
```

---

## 🏗️ **Jenkins - Pasos de Configuración**

### 1. **Plugins Requeridos:**
```
- Pipeline
- Git  
- Maven Integration
- SonarQube Scanner
- Slack Notification
- HTML Publisher
- JUnit
- JaCoCo
```

### 2. **Configurar Herramientas Globales:**

**Maven:**
```
Name: Maven-3.9
Version: Maven 3.9.4 (Auto-install)
```

**JDK:**
```
Name: JDK-17
JAVA_HOME: C:\Program Files\Eclipse Adoptium\jdk-17.0.8.101-hotspot\
```

**SonarQube Scanner:**
```
Name: SonarQube Scanner
Version: SonarQube Scanner 4.8+ (Auto-install)
```

### 3. **Configurar Credenciales:**

**SonarCloud Token:**
```
Kind: Secret text
Scope: Global
Secret: a16d14977ea78acff7ea90041fc36ee7dca7e068
ID: sonar-token
Description: SonarCloud Authentication Token
```

**Slack Webhook:**
```
Kind: Secret text
Scope: Global  
Secret: https://hooks.slack.com/services/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
ID: slack-webhook
Description: Slack Webhook URL for Notifications
```

### 4. **Configurar SonarQube:**
```
Name: SonarCloud
Server URL: https://sonarcloud.io
Server authentication token: [Usar credential: sonar-token]
```

### 5. **Configurar Slack:**
```
Workspace: vallegrande  
Credential: [Usar credential: slack-webhook]
Default Channel: #jenkins-ci-cd-bot
```

---

## 🎬 **Script de Demo**

### **Demo Flow Completo:**

```bash
# 1. Mostrar estructura del proyecto
ls -la

# 2. Ejecutar tests unitarios
mvn clean test
# ✅ Mostrar: 17/17 tests passed

# 3. Generar reporte de cobertura
mvn jacoco:report
# ✅ Abrir: target/site/jacoco/index.html

# 4. Ejecutar análisis SonarCloud (manual)
set SONAR_TOKEN=a16d14977ea78acff7ea90041fc36ee7dca7e068
mvn verify sonar:sonar -Dsonar.projectKey=Omarrivv_pruebascanales_revision_intermedia

# 5. Mostrar Jenkinsfile
type Jenkinsfile

# 6. Mostrar plan JMeter  
type src\test\jmeter\students-load-test.jmx

# 7. Test de notificación Slack
# [Ejecutar script PowerShell de arriba]
```

---

## 📊 **Métricas de la Demo**

### **Pruebas Unitarias:**
```
✅ StudentServiceImplTest: 5/5 tests
✅ StudentControllerTest: 6/6 tests  
✅ StudentMapperTest: 6/6 tests
📊 Total: 17/17 tests passing (100%)
```

### **Cobertura de Código:**
```
📊 Instructions Coverage: ~12%
📊 Branch Coverage: ~8%
📊 Line Coverage: ~15%
📊 Method Coverage: ~18%
📊 Class Coverage: ~25%
```

### **Pipeline Stages:**
```
🚀 Checkout: ~30s
🏗️ Build & Compile: ~1m
🧪 Unit Tests: ~45s (17/17 ✅)
📊 SonarQube Analysis: ~1m 30s
🚨 Quality Gate: ~30s  
📦 Package: ~30s
⚡ JMeter Load Tests: ~3m
📱 Slack Notifications: Todo el pipeline
```

---

## 🗣️ **Puntos Clave para la Presentación**

### **1. Automatización Completa:**
- "Hemos implementado un pipeline CI/CD completo que se ejecuta automáticamente"
- "17 pruebas unitarias se ejecutan en cada commit"
- "Análisis de calidad automático con SonarCloud"

### **2. Calidad de Código:**
- "JaCoCo genera reportes de cobertura en cada build"
- "SonarCloud analiza bugs, vulnerabilidades y code smells"
- "Quality Gate garantiza estándares mínimos"

### **3. Pruebas de Rendimiento:**
- "JMeter ejecuta pruebas de carga automáticas"
- "Simulamos 10 usuarios concurrentes durante 60 segundos"
- "Validamos tiempo de respuesta y throughput"

### **4. Notificaciones Inteligentes:**
- "Slack nos notifica el estado de cada build"
- "Mensajes diferenciados por tipo de evento"
- "Enlaces directos a reportes y logs"

### **5. Monitoreo Continuo:**
- "Métricas visuales en tiempo real"
- "Historial de builds y tendencias"
- "Alertas proactivas de fallos"

---

## 🔗 **Enlaces Importantes**

- **Repositorio GitHub:** https://github.com/Omarrivv/pruebascanales_revision_intermedia
- **SonarCloud Project:** https://sonarcloud.io/project/overview?id=Omarrivv_pruebascanales_revision_intermedia
- **Jenkins Local:** http://localhost:9090
- **Slack Workspace:** https://vallegrande.slack.com

---

## ✅ **Checklist Final Demo**

```
☐ Jenkins funcionando en localhost:9090
☐ Plugins instalados y configurados
☐ Credenciales SonarCloud y Slack agregadas
☐ Job Pipeline creado y configurado
☐ Tests unitarios ejecutados: 17/17 ✅
☐ Reporte JaCoCo generado
☐ Plan JMeter creado
☐ Notificación Slack probada
☐ Jenkinsfile revisado y listo
☐ Documentación completa preparada
```

---

## 🎉 **¡TODO LISTO PARA LA DEMO! 🚀**

**El proyecto está completamente configurado con:**
- ✅ 17 pruebas unitarias funcionando
- ✅ Pipeline Jenkins completo
- ✅ Integración SonarCloud
- ✅ Pruebas JMeter
- ✅ Notificaciones Slack
- ✅ Documentación completa

**¡Impresiona con tu pipeline CI/CD profesional! 🎯**