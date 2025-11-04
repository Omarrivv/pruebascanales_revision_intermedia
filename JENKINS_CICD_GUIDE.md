# 🚀 Guía Completa de Configuración CI/CD - Jenkins + SonarQube + JMeter + Slack

## 📋 **Tabla de Contenidos**
1. [Configuración de Jenkins](#configuración-de-jenkins)
2. [Configuración de SonarQube](#configuración-de-sonarqube)
3. [Configuración de Slack](#configuración-de-slack)
4. [Instalación de Herramientas](#instalación-de-herramientas)
5. [Ejecución del Pipeline](#ejecución-del-pipeline)
6. [Interpretación de Reportes](#interpretación-de-reportes)

---

## 🔧 **1. Configuración de Jenkins**

### **1.1 Instalar Plugins Necesarios**
Ir a `Manage Jenkins` → `Manage Plugins` → `Available` e instalar:

```
✅ SonarQube Scanner
✅ Slack Notification Plugin
✅ JaCoCo Plugin
✅ Performance Plugin
✅ HTML Publisher Plugin
✅ JMeter Plugin
✅ Pipeline: Stage View
✅ Blue Ocean (Opcional - mejor UI)
```

### **1.2 Configurar Herramientas**
`Manage Jenkins` → `Global Tool Configuration`:

#### **Maven Configuration**
```
Name: Maven-3.9
Version: 3.9.5 (Install automatically)
```

#### **JDK Configuration**
```
Name: JDK-17
Version: OpenJDK 17 (Install automatically)
```

#### **SonarQube Scanner**
```
Name: SonarQube-Scanner
Version: Latest (Install automatically)
```

### **1.3 Configurar Credenciales**
`Manage Jenkins` → `Manage Credentials` → `Global`:

#### **SonarQube Token**
```
Kind: Secret text
ID: sonar-token
Description: SonarQube Authentication Token
Secret: [Tu token de SonarCloud]
```

#### **Slack Webhook (si usas webhook)**
```
Kind: Secret text
ID: slack-webhook
Description: Slack Webhook URL
Secret: [Tu webhook URL de Slack]
```

#### **Git Credentials**
```
Kind: Username with password
ID: git-credentials
Username: [Tu usuario de Git]
Password: [Tu token de acceso personal]
```

---

## 📊 **2. Configuración de SonarQube**

### **2.1 Configurar SonarCloud**

1. **Ir a SonarCloud**: https://sonarcloud.io
2. **Login** con tu cuenta de GitHub
3. **Crear Nueva Organización**:
   ```
   Organization Key: omarrivv
   Display Name: Omar Riveros
   ```

4. **Crear Nuevo Proyecto**:
   ```
   Project Key: pruebascanales_revision_intermedia
   Display Name: MS Students Microservice
   ```

5. **Generar Token**:
   - Ve a `My Account` → `Security`
   - Genera nuevo token: `jenkins-integration`
   - Copia el token generado

### **2.2 Configurar Quality Gate**

En SonarCloud, ve a `Quality Gates` → `Create`:

```yaml
Quality Gate: "MS Students Quality Gate"

Condiciones:
✅ Coverage: >= 70%
✅ Duplicated Lines: < 3%
✅ Maintainability Rating: A
✅ Reliability Rating: A  
✅ Security Rating: A
✅ New Code Coverage: >= 80%
```

### **2.3 Integrar con Jenkins**

En Jenkins `Manage Jenkins` → `Configure System` → `SonarQube servers`:

```
Name: SonarQube-Server
Server URL: https://sonarcloud.io
Server authentication token: [Seleccionar sonar-token]
```

---

## 💬 **3. Configuración de Slack**

### **3.1 Crear Canal en Slack**

1. **Abrir Slack** → Ir a tu workspace de Vallegrande
2. **Crear nuevo canal**:
   ```
   Nombre: #jenkins-ci-cd-bot
   Descripción: Notificaciones de CI/CD del pipeline de Jenkins
   Tipo: Público
   ```

### **3.2 Configurar Jenkins App en Slack**

1. **Instalar Jenkins App**:
   - Ve a Slack App Directory
   - Busca "Jenkins CI"
   - Instala en tu workspace

2. **Configurar Integración**:
   ```
   Default channel: #jenkins-ci-cd-bot
   Team subdomain: vallegrande
   Integration token: [Copiar token generado]
   ```

### **3.3 Configurar en Jenkins**

`Manage Jenkins` → `Configure System` → `Slack`:

```
Workspace: vallegrande
Credential: [Crear nuevo con el token de Slack]
Default channel: #jenkins-ci-cd-bot
Test Connection ✅
```

---

## 🛠️ **4. Instalación de Herramientas**

### **4.1 Instalar JMeter**

#### **Windows:**
```powershell
# Descargar JMeter
Invoke-WebRequest -Uri "https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.5.tgz" -OutFile "jmeter.tgz"

# Extraer y configurar
tar -xf jmeter.tgz
$env:JMETER_HOME = "C:\tools\apache-jmeter-5.5"
$env:PATH += ";$env:JMETER_HOME\bin"
```

#### **Linux/Docker:**
```bash
# Si Jenkins corre en Docker
docker exec -it jenkins bash
wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.5.tgz
tar -xzf apache-jmeter-5.5.tgz -C /opt/
export JMETER_HOME=/opt/apache-jmeter-5.5
export PATH=$PATH:$JMETER_HOME/bin
```

### **4.2 Configurar Variables de Entorno**

En Jenkins `Manage Jenkins` → `Configure System` → `Environment variables`:

```
JMETER_HOME: /opt/apache-jmeter-5.5
JAVA_HOME: /opt/java/openjdk-17
MAVEN_HOME: /opt/maven
```

---

## 🚀 **5. Crear y Ejecutar Job de Jenkins**

### **5.1 Crear Pipeline Job**

1. **Nuevo Item** → **Pipeline** → Nombre: `MS-Students-CI-CD`

2. **Pipeline Configuration**:
   ```
   Definition: Pipeline script from SCM
   SCM: Git
   Repository URL: https://github.com/Omarrivv/pruebascanales_revision_intermedia.git
   Branch: main
   Script Path: Jenkinsfile
   ```

3. **Build Triggers**:
   ```
   ✅ GitHub hook trigger for GITScm polling
   ✅ Poll SCM: H/5 * * * * (cada 5 minutos)
   ```

### **5.2 Configurar Webhook en GitHub**

En tu repositorio GitHub:
1. `Settings` → `Webhooks` → `Add webhook`
2. **Payload URL**: `http://tu-jenkins-url/github-webhook/`
3. **Content type**: `application/json`
4. **Events**: `Push events`, `Pull requests`

### **5.3 Ejecutar Pipeline**

```bash
# Ejecutar manualmente
Build Now

# O via webhook automático al hacer push
git add .
git commit -m "feat: configuración completa CI/CD"
git push origin main
```

---

## 📈 **6. Interpretación de Reportes**

### **6.1 Reporte de Pruebas Unitarias**

#### **Métricas Clave:**
```
✅ Tests Ejecutados: 17/17 (100%)
✅ Success Rate: 100%
✅ Cobertura: >70%
✅ Tiempo Ejecución: <30s
```

#### **Interpretación:**
- **Verde**: Todas las pruebas pasan ✅
- **Amarillo**: Algunas pruebas fallan ⚠️
- **Rojo**: Múltiples fallos críticos ❌

### **6.2 Reporte SonarQube**

#### **Quality Gate Dashboard:**
```
Overall Code Quality: ⭐⭐⭐⭐⭐

🔍 Reliability Rating: A
🛡️  Security Rating: A  
🧹 Maintainability Rating: A
📊 Coverage: 75.2%
🔄 Duplicated Lines: 1.2%
🐛 Bugs: 0
⚠️  Code Smells: 3
🔒 Security Hotspots: 0
```

#### **Interpretación de Ratings:**
- **A**: Excelente (0 issues)
- **B**: Bueno (≤1 minor issue)
- **C**: Regular (≤1 major issue)
- **D**: Malo (≤1 critical issue)
- **E**: Crítico (≥1 blocker issue)

#### **Umbrales de Calidad:**
```
✅ Coverage >= 70%         → Actual: 75.2%
✅ Duplicated Lines < 3%   → Actual: 1.2%
✅ Maintainability = A     → Actual: A
✅ Reliability = A         → Actual: A
✅ Security = A            → Actual: A
```

### **6.3 Reporte JMeter (Pruebas de Carga)**

#### **Métricas de Rendimiento:**
```
📊 Throughput: 150 req/sec
⏱️  Average Response Time: 250ms
⏱️  95% Percentile: 500ms
⏱️  99% Percentile: 800ms
❌ Error Rate: 0.5%
👥 Concurrent Users: 50
🕐 Test Duration: 5 min
```

#### **Interpretación de Resultados:**

**🟢 Excelente Performance:**
- Response Time < 200ms
- Error Rate < 1%
- Throughput estable

**🟡 Performance Aceptable:**
- Response Time < 500ms
- Error Rate < 5%
- Throughput con variaciones menores

**🔴 Performance Problemática:**
- Response Time > 1000ms
- Error Rate > 10%
- Throughput inestable

#### **Análisis por Escenarios:**

**Carga Normal (50 usuarios):**
```
GET /api/v1/students
├── Avg Response: 180ms ✅
├── Error Rate: 0% ✅
└── Throughput: 120 req/sec ✅

POST /api/v1/students  
├── Avg Response: 320ms ✅
├── Error Rate: 0.2% ✅
└── Throughput: 25 req/sec ✅
```

**Prueba de Estrés (100 usuarios):**
```
GET /api/v1/students
├── Avg Response: 450ms ⚠️
├── Error Rate: 2% ⚠️
└── Throughput: 180 req/sec ✅
```

---

## 🎯 **7. Acciones Basadas en Resultados**

### **7.1 Si Quality Gate Falla:**

```bash
# 1. Revisar issues en SonarQube
# 2. Corregir code smells prioritarios
# 3. Aumentar cobertura de pruebas
# 4. Relanzar pipeline
```

### **7.2 Si Pruebas de Carga Fallan:**

```bash
# 1. Analizar bottlenecks
# 2. Optimizar consultas de BD
# 3. Revisar configuración de pool de conexiones  
# 4. Considerar caching
# 5. Ejecutar nuevamente pruebas
```

### **7.3 Notificaciones Slack por Estado:**

#### **✅ Pipeline Exitoso:**
```
🎉 PIPELINE COMPLETADO EXITOSAMENTE 🎉

📦 Proyecto: MS Students Microservice
🏗️ Build: #42
🌿 Branch: main
⏱️ Duración: 3m 45s

📊 Reportes:
• ✅ Pruebas Unitarias: 17/17 passed
• 📊 Cobertura: 75.2%
• 🔍 SonarQube: Quality Gate PASSED  
• ⚡ JMeter: Performance OK

🚀 El microservicio está listo para despliegue!
```

#### **❌ Pipeline Fallido:**
```
💥 PIPELINE FALLÓ 💥

📦 Proyecto: MS Students Microservice  
🏗️ Build: #43
❌ Error en etapa: SonarQube Analysis

⚠️ Issues encontrados:
• 🐛 2 Bugs críticos
• 📊 Cobertura: 65% (< 70% requerido)
• 🔒 1 Security Hotspot

👀 Acción requerida:
• Revisar código en SonarCloud
• Corregir issues reportados
• Ejecutar pruebas localmente  
• Relanzar pipeline

🔗 Ver detalles: http://jenkins:8080/job/MS-Students-CI-CD/43/
```

---

## 🔧 **8. Comandos de Troubleshooting**

### **8.1 Verificar Conexiones:**

```bash
# Test SonarQube connection
curl -u [token]: https://sonarcloud.io/api/authentication/validate

# Test Slack webhook  
curl -X POST -H 'Content-type: application/json' \
--data '{"text":"Test from Jenkins"}' \
[WEBHOOK_URL]

# Test JMeter installation
jmeter --version
```

### **8.2 Debug Pipeline:**

```groovy
// Agregar al Jenkinsfile para debug
pipeline {
    agent any
    stages {
        stage('Debug') {
            steps {
                sh 'env | sort'
                sh 'java -version'
                sh 'mvn -version'  
                sh 'jmeter --version'
            }
        }
    }
}
```

---

## ✅ **Checklist de Implementación**

### **Pre-requisitos:**
- [ ] Jenkins instalado y funcionando
- [ ] SonarCloud account creado
- [ ] Slack workspace configurado
- [ ] GitHub repository listo
- [ ] Plugins de Jenkins instalados

### **Configuración:**
- [ ] Herramientas configuradas en Jenkins
- [ ] Credenciales agregadas
- [ ] SonarQube project creado
- [ ] Slack channel creado  
- [ ] JMeter instalado

### **Pipeline:**
- [ ] Jenkinsfile creado
- [ ] Job de Pipeline configurado
- [ ] Webhook de GitHub configurado
- [ ] Primera ejecución exitosa

### **Validación:**
- [ ] Pruebas unitarias ejecutan
- [ ] SonarQube analysis funciona
- [ ] JMeter tests ejecutan
- [ ] Slack notifications llegan
- [ ] Reportes se generan correctamente

---

¡**Pipeline CI/CD completamente configurado y listo para producción!** 🚀