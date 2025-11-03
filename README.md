# 🎓 vg-ms-students - Microservicio de Gestión Estudiantil

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring WebFlux](https://img.shields.io/badge/Spring%20WebFlux-Reactive-blue.svg)](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
[![MongoDB](https://img.shields.io/badge/MongoDB-Reactive-green.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

---

## 📋 RESUMEN EJECUTIVO

**vg-ms-students** es un microservicio reactivo de alta performance que implementa las **Especificaciones PRS (Sistema de Estandarización)** para la gestión integral de estudiantes y sus matrículas académicas. Desarrollado con **arquitectura hexagonal** y **programación reactiva**, ofrece APIs REST completamente estandarizadas para el ciclo de vida completo de la gestión estudiantil.

### 🎯 **OBJETIVO PRINCIPAL**
Proporcionar un sistema robusto y escalable para gestionar:
- ✅ **Información completa de estudiantes** (personal, académica, apoderados)
- ✅ **Matrículas por períodos académicos** con validaciones de negocio
- ✅ **Historial académico integral** con trazabilidad completa
- ✅ **Transferencias y promociones** entre períodos
- ✅ **Reportes y consultas avanzadas** con filtros especializados

---

## 🏛️ ENTIDADES DEL SISTEMA

### 👤 **ENTIDAD MAESTRO: Student**
**Propósito:** Gestión integral de la información estudiantil personal, académica y de contacto.

```java
📋 CAMPOS PRINCIPALES:
• id (UUID) - Identificador único del estudiante
• firstName, lastName - Información personal
• documentType, documentNumber - Identificación oficial (único nacional)
• birthDate, gender - Datos demográficos
• address, district, province, department - Ubicación geográfica
• phone, email - Datos de contacto

👨‍👩‍👧‍👦 INFORMACIÓN DEL APODERADO:
• guardianName, guardianLastName - Datos del responsable
• guardianDocumentType, guardianDocumentNumber - ID del apoderado
• guardianPhone, guardianEmail - Contacto del apoderado
• guardianRelationship - Relación familiar (FATHER, MOTHER, GUARDIAN, etc.)

📊 METADATOS:
• status - Estado del estudiante (ACTIVE, INACTIVE, TRANSFERRED, etc.)
• createdAt, updatedAt - Auditoría temporal automática
```

### 📚 **ENTIDAD TRANSACCIONAL: StudentEnrollment**
**Propósito:** Gestión de matrículas estudiantiles por período académico con validaciones de negocio.

```java
📋 CAMPOS PRINCIPALES:
• id (UUID) - Identificador único de la matrícula
• studentId - Referencia al estudiante (FK)
• classroomId - Referencia al aula/período académico
• enrollmentNumber - Código único de matrícula por período
• enrollmentDate - Fecha de matrícula
• status - Estado de la matrícula (ACTIVE, COMPLETED, TRANSFERRED, etc.)

📊 METADATOS:
• createdAt, updatedAt - Auditoría temporal automática
```

---

## 🗺️ MAPA CONCEPTUAL DETALLADO

```
🏗️ vg-ms-students MICROSERVICE ARCHITECTURE
│
├── 🌐 PRESENTATION LAYER (Infrastructure/REST)
│   ├── 📡 StudentController (/api/v1/students)
│   │   ├── ✅ CRUD Básico: GET, POST, PUT, DELETE
│   │   ├── 🔍 Búsquedas: /document/{num}, /status/{status}, /gender/{gender}
│   │   ├── 🔎 Filtros: /search/firstname/{name}, /search/lastname/{name}
│   │   └── 📊 Respuestas: Estructura ApiResponse estandarizada PRS
│   │
│   └── 📡 StudentEnrollmentController (/api/v1/enrollments)
│       ├── ✅ CRUD Básico: GET, POST, PUT, DELETE
│       ├── 🔍 Búsquedas: /student/{id}, /classroom/{id}, /enrollment-number/{num}
│       ├── 🔎 Filtros: /status/{status}
│       └── 📊 Respuestas: Estructura ApiResponse estandarizada PRS
│
├── 🎯 APPLICATION LAYER (Business Logic)
│   ├── 🧠 StudentService + StudentServiceImpl
│   │   ├── 🔄 CRUD Operations con validaciones
│   │   ├── 🔍 Búsquedas especializadas con ordenamiento
│   │   ├── ✅ Validaciones de negocio (unicidad, formatos)
│   │   └── 🔄 Transformación entre DTOs y Entities
│   │
│   └── 🧠 StudentEnrollmentService + StudentEnrollmentServiceImpl
│       ├── 🔄 CRUD Operations con validaciones
│       ├── 🔍 Filtros avanzados por estudiante, aula, período
│       ├── ✅ Validación de matrícula única activa por estudiante
│       └── 🔄 Gestión de estados de matrícula
│
├── 🏛️ DOMAIN LAYER (Core Business)
│   ├── 📋 Models
│   │   ├── 👤 Student (Entidad agregada raíz)
│   │   └── 📚 StudentEnrollment (Entidad transaccional)
│   │
│   └── 🏷️ Enums
│       ├── 📄 DocumentType (DNI, CE, PASSPORT)
│       ├── ⚧️ Gender (MALE, FEMALE)
│       ├── 📊 Status (ACTIVE, INACTIVE, TRANSFERRED, GRADUATED, DECEASED)
│       ├── 👨‍👩‍👧‍👦 GuardianRelationship (FATHER, MOTHER, GUARDIAN, GRANDPARENT, OTHER)
│       └── 📝 EnrollmentStatus (ACTIVE, COMPLETED, TRANSFERRED, WITHDRAWN, SUSPENDED)
│
└── 🗄️ INFRASTRUCTURE LAYER (Technical Details)
    ├── 💾 Repositories (MongoDB Reactive)
    │   ├── 👤 StudentRepository + StudentRepositoryImpl
    │   └── 📚 StudentEnrollmentRepository + StudentEnrollmentRepositoryImpl
    │
    ├── 📦 DTOs
    │   ├── 📥 Requests (CreateStudentRequest, UpdateStudentRequest, CreateStudentEnrollmentRequest)
    │   └── 📤 Responses (StudentResponse, StudentEnrollmentResponse, ApiResponse<T>)
    │
    ├── ⚙️ Configuration
    │   ├── 🍃 MongoConfig (Configuración reactiva MongoDB)
    │   └── 🌐 WebConfig (CORS y configuraciones web)
    │
    ├── 🚨 Exception Handling
    │   ├── 🌍 GlobalExceptionHandler (Manejo centralizado de errores)
    │   └── 🔍 ResourceNotFoundException (Excepciones de negocio)
    │
    └── 🛠️ Utils
        ├── 🔄 StudentMapper (Conversión Entity ↔ DTO)
        ├── 🔄 StudentEnrollmentMapper (Conversión Entity ↔ DTO)
        └── 📊 CsvUtils (Exportación de datos)
```

---

## 🏗️ ESTRUCTURA DEL PROYECTO

```
src/main/java/pe/edu/vallegrande/msvstudents/
│
├── 🚀 MsvStudentsApplication.java (Punto de entrada Spring Boot)
│
├── 🏛️ domain/
│   ├── 📋 model/
│   │   ├── 👤 Student.java ✅ (Entidad principal)
│   │   └── 📚 StudentEnrollment.java ✅ (Entidad transaccional)
│   │
│   └── 🏷️ enums/
│       ├── 📄 DocumentType.java ✅ (DNI, CE, PASSPORT)
│       ├── ⚧️ Gender.java ✅ (MALE, FEMALE)
│       ├── 📊 Status.java ✅ (Estados del estudiante)
│       ├── 👨‍👩‍👧‍👦 GuardianRelationship.java ✅ (Relaciones familiares)
│       └── 📝 EnrollmentStatus.java ✅ (Estados de matrícula)
│
├── 🎯 application/service/
│   ├── 👤 StudentService.java ✅ (Interfaz de servicio)
│   ├── 📚 StudentEnrollmentService.java ✅ (Interfaz de servicio)
│   └── impl/
│       ├── 👤 StudentServiceImpl.java ✅ (Lógica de negocio)
│       └── 📚 StudentEnrollmentServiceImpl.java ✅ (Lógica de negocio)
│
└── 🌐 infrastructure/
    ├── 📦 dto/
    │   ├── 📥 request/
    │   │   ├── CreateStudentRequest.java ✅
    │   │   ├── UpdateStudentRequest.java ✅
    │   │   ├── StudentRequest.java ✅
    │   │   └── CreateStudentEnrollmentRequest.java ✅
    │   │
    │   └── 📤 response/
    │       ├── ApiResponse.java ✅ (Estructura PRS)
    │       ├── StudentResponse.java ✅
    │       └── StudentEnrollmentResponse.java ✅
    │
    ├── 💾 repository/
    │   ├── StudentRepository.java ✅ (Interfaz reactiva)
    │   ├── StudentEnrollmentRepository.java ✅ (Interfaz reactiva)
    │   └── impl/
    │       ├── StudentRepositoryImpl.java ✅ (MongoDB reactivo)
    │       └── StudentEnrollmentRepositoryImpl.java ✅ (MongoDB reactivo)
    │
    ├── 📡 rest/
    │   ├── StudentController.java ✅ (API REST estudiantes)
    │   └── StudentEnrollmentController.java ✅ (API REST matrículas)
    │
    ├── 🚨 exception/
    │   ├── GlobalExceptionHandler.java ✅
    │   └── ResourceNotFoundException.java ✅
    │
    ├── 🛠️ util/
    │   ├── StudentMapper.java ✅
    │   ├── StudentEnrollmentMapper.java ✅
    │   └── CsvUtils.java ✅
    │
    └── ⚙️ config/
        ├── MongoConfig.java ✅
        └── WebConfig.java ✅

src/main/resources/
├── 📋 application.yml ✅ (Configuración Spring)
└── 🗄️ db/
    └── init-mongo.js ✅ (Scripts de inicialización)
```

---

## 📖 DOCUMENTACIÓN COMPLETA DE API

### 🌍 **BASE URL**
```
http://localhost:8102/api/v1
```

### 📊 **ESTRUCTURA DE RESPUESTA ESTÁNDAR PRS**
```json
{
  "metadata": {
    "status": 200,
    "message": "Descripción del resultado",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    // Datos de respuesta aquí
  }
}
```

---

## 👤 API ESTUDIANTES (/api/v1/students)

### 📋 **1. OBTENER TODOS LOS ESTUDIANTES**
```http
GET /api/v1/students
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Students retrieved successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "firstName": "Juan Carlos",
      "lastName": "González Pérez",
      "documentType": "DNI",
      "documentNumber": "78901234",
      "birthDate": "2010-03-15",
      "gender": "MALE",
      "address": "Jr. Los Pinos 123",
      "district": "Lima",
      "province": "Lima",
      "department": "Lima",
      "phone": "912345678",
      "email": "juan.gonzalez@email.com",
      "guardianName": "Carlos",
      "guardianLastName": "González",
      "guardianDocumentType": "DNI",
      "guardianDocumentNumber": "12345678",
      "guardianPhone": "987654321",
      "guardianEmail": "carlos.gonzalez@email.com",
      "guardianRelationship": "FATHER",
      "status": "ACTIVE",
      "createdAt": "2025-09-06T18:30:00.123456",
      "updatedAt": "2025-09-06T18:30:00.123456"
    }
  ]
}
```

### 🔍 **2. OBTENER ESTUDIANTE POR ID**
```http
GET /api/v1/students/{id}
```

**📤 Respuesta Exitosa:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student retrieved successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "Ana María",
    "lastName": "López García",
    // ... resto de campos del estudiante
  }
}
```

**❌ Respuesta Error (No encontrado):**
```json
{
  "metadata": {
    "status": 404,
    "message": "Student not found with ID: invalid-id",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": null
}
```

### ➕ **3. CREAR ESTUDIANTE**
```http
POST /api/v1/students
Content-Type: application/json
```

**📥 Request Body:**
```json
{
  "firstName": "Ana María",
  "lastName": "López García",
  "documentType": "DNI",
  "documentNumber": "87654321",
  "birthDate": "2010-05-15",
  "gender": "FEMALE",
  "address": "Av. Principal 123",
  "district": "Lima",
  "province": "Lima",
  "department": "Lima",
  "phone": "987654321",
  "email": "ana.lopez@email.com",
  "guardianName": "Carlos",
  "guardianLastName": "López",
  "guardianDocumentType": "DNI",
  "guardianDocumentNumber": "12345678",
  "guardianPhone": "987654321",
  "guardianEmail": "carlos.lopez@email.com",
  "guardianRelationship": "FATHER"
}
```

**📤 Respuesta Exitosa:**
```json
{
  "metadata": {
    "status": 201,
    "message": "Student created successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "nuevo-uuid-generado",
    "firstName": "Ana María",
    "lastName": "López García",
    "documentType": "DNI",
    "documentNumber": "87654321",
    "birthDate": "2010-05-15",
    "gender": "FEMALE",
    "address": "Av. Principal 123",
    "district": "Lima",
    "province": "Lima",
    "department": "Lima",
    "phone": "987654321",
    "email": "ana.lopez@email.com",
    "guardianName": "Carlos",
    "guardianLastName": "López",
    "guardianDocumentType": "DNI",
    "guardianDocumentNumber": "12345678",
    "guardianPhone": "987654321",
    "guardianEmail": "carlos.lopez@email.com",
    "guardianRelationship": "FATHER",
    "status": "ACTIVE",
    "createdAt": "2025-09-06T18:45:30.123456",
    "updatedAt": "2025-09-06T18:45:30.123456"
  }
}
```

**❌ Respuesta Error (Validación):**
```json
{
  "metadata": {
    "status": 400,
    "message": "Validation failed",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "errors": [
      {
        "field": "firstName",
        "message": "First name is required"
      },
      {
        "field": "documentNumber",
        "message": "Document number is required"
      }
    ]
  }
}
```

### ✏️ **4. ACTUALIZAR ESTUDIANTE**
```http
PUT /api/v1/students/{id}
Content-Type: application/json
```

**📥 Request Body:**
```json
{
  "firstName": "Ana María Actualizada",
  "lastName": "López García",
  "documentType": "DNI",
  "documentNumber": "87654321",
  "birthDate": "2010-05-15",
  "gender": "FEMALE",
  "address": "Nueva Dirección 456",
  "district": "Callao",
  "province": "Callao",
  "department": "Lima",
  "phone": "987654322",
  "email": "ana.lopez.updated@email.com",
  "guardianName": "Carlos",
  "guardianLastName": "López",
  "guardianDocumentType": "DNI",
  "guardianDocumentNumber": "12345678",
  "guardianPhone": "987654321",
  "guardianEmail": "carlos.lopez@email.com",
  "guardianRelationship": "FATHER"
}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student updated successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "Ana María Actualizada",
    "lastName": "López García",
    // ... resto de campos actualizados
    "updatedAt": "2025-09-06T18:45:30.123456"
  }
}
```

### 🗑️ **5. ELIMINAR ESTUDIANTE (Lógico)**
```http
DELETE /api/v1/students/{id}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 204,
    "message": "Student deleted successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": null
}
```

### 🔍 **6. BUSCAR POR NÚMERO DE DOCUMENTO**
```http
GET /api/v1/students/document/{documentNumber}
```

**Ejemplo:** `GET /api/v1/students/document/78901234`

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student retrieved successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "Juan Carlos",
    "lastName": "González Pérez",
    "documentNumber": "78901234",
    // ... resto de campos
  }
}
```

### 🔍 **7. FILTRAR POR STATUS**
```http
GET /api/v1/students/status/{status}
```

**Valores permitidos:** `ACTIVE`, `INACTIVE`, `TRANSFERRED`, `GRADUATED`, `DECEASED`

**Ejemplo:** `GET /api/v1/students/status/ACTIVE`

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Students retrieved by status successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "uuid-1",
      "firstName": "Juan Carlos",
      "status": "ACTIVE",
      // ... resto de campos
    },
    {
      "id": "uuid-2",
      "firstName": "Ana María",
      "status": "ACTIVE",
      // ... resto de campos
    }
  ]
}
```

### 🔍 **8. FILTRAR POR GÉNERO**
```http
GET /api/v1/students/gender/{gender}
```

**Valores permitidos:** `MALE`, `FEMALE`

**Ejemplo:** `GET /api/v1/students/gender/FEMALE`

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Students retrieved by gender successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "uuid-1",
      "firstName": "Ana María",
      "gender": "FEMALE",
      // ... resto de campos
    }
  ]
}
```

### 🔍 **9. BUSCAR POR NOMBRE**
```http
GET /api/v1/students/search/firstname/{firstName}
```

**Ejemplo:** `GET /api/v1/students/search/firstname/Juan`

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Students retrieved by first name successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "uuid-1",
      "firstName": "Juan Carlos",
      // ... resto de campos
    }
  ]
}
```

### 🔍 **10. BUSCAR POR APELLIDO**
```http
GET /api/v1/students/search/lastname/{lastName}
```

**Ejemplo:** `GET /api/v1/students/search/lastname/González`

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Students retrieved by last name successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "uuid-1",
      "firstName": "Juan Carlos",
      "lastName": "González Pérez",
      // ... resto de campos
    }
  ]
}
```

---

## 📚 API MATRÍCULAS (/api/v1/enrollments)

### 📋 **1. OBTENER TODAS LAS MATRÍCULAS**
```http
GET /api/v1/enrollments
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student enrollments retrieved successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "enrollment-uuid-1",
      "studentId": "student-uuid-1",
      "classroomId": "classroom-2024-001",
      "enrollmentNumber": "ENR-2024-001",
      "enrollmentDate": "2024-03-01",
      "status": "ACTIVE",
      "createdAt": "2024-03-01T08:00:00.000000",
      "updatedAt": "2024-03-01T08:00:00.000000"
    },
    {
      "id": "enrollment-uuid-2",
      "studentId": "student-uuid-2",
      "classroomId": "classroom-2024-002",
      "enrollmentNumber": "ENR-2024-002",
      "enrollmentDate": "2024-03-01",
      "status": "COMPLETED",
      "createdAt": "2024-03-01T08:00:00.000000",
      "updatedAt": "2024-12-15T15:30:00.000000"
    }
  ]
}
```

### 🔍 **2. OBTENER MATRÍCULA POR ID**
```http
GET /api/v1/enrollments/{id}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student enrollment retrieved successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "enrollment-uuid-1",
    "studentId": "student-uuid-1",
    "classroomId": "classroom-2024-001",
    "enrollmentNumber": "ENR-2024-001",
    "enrollmentDate": "2024-03-01",
    "status": "ACTIVE",
    "createdAt": "2024-03-01T08:00:00.000000",
    "updatedAt": "2024-03-01T08:00:00.000000"
  }
}
```

### ➕ **3. CREAR MATRÍCULA**
```http
POST /api/v1/enrollments
Content-Type: application/json
```

**📥 Request Body:**
```json
{
  "studentId": "550e8400-e29b-41d4-a716-446655440000",
  "classroomId": "classroom-2025-001",
  "enrollmentNumber": "ENR-2025-001",
  "enrollmentDate": "2025-03-01"
}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 201,
    "message": "Student enrollment created successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "nuevo-enrollment-uuid",
    "studentId": "550e8400-e29b-41d4-a716-446655440000",
    "classroomId": "classroom-2025-001",
    "enrollmentNumber": "ENR-2025-001",
    "enrollmentDate": "2025-03-01",
    "status": "ACTIVE",
    "createdAt": "2025-09-06T18:45:30.123456",
    "updatedAt": "2025-09-06T18:45:30.123456"
  }
}
```

**❌ Respuesta Error (Validación):**
```json
{
  "metadata": {
    "status": 400,
    "message": "Validation failed",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "errors": [
      {
        "field": "studentId",
        "message": "Student ID is required"
      },
      {
        "field": "enrollmentNumber",
        "message": "Enrollment number is required"
      }
    ]
  }
}
```

### ✏️ **4. ACTUALIZAR STATUS DE MATRÍCULA**
```http
PUT /api/v1/enrollments/{id}/status/{status}
```

**Valores permitidos:** `ACTIVE`, `COMPLETED`, `TRANSFERRED`, `WITHDRAWN`, `SUSPENDED`

**Ejemplo:** `PUT /api/v1/enrollments/enrollment-uuid-1/status/COMPLETED`

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student enrollment status updated successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "enrollment-uuid-1",
    "studentId": "student-uuid-1",
    "classroomId": "classroom-2024-001",
    "enrollmentNumber": "ENR-2024-001",
    "enrollmentDate": "2024-03-01",
    "status": "COMPLETED",
    "createdAt": "2024-03-01T08:00:00.000000",
    "updatedAt": "2025-09-06T18:45:30.123456"
  }
}
```

### 🗑️ **5. ELIMINAR MATRÍCULA**
```http
DELETE /api/v1/enrollments/{id}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 204,
    "message": "Student enrollment deleted successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": null
}
```

### 🔍 **6. OBTENER MATRÍCULAS POR ESTUDIANTE**
```http
GET /api/v1/enrollments/student/{studentId}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student enrollments retrieved by student ID successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "enrollment-uuid-1",
      "studentId": "student-uuid-1",
      "classroomId": "classroom-2024-001",
      "enrollmentNumber": "ENR-2024-001",
      "enrollmentDate": "2024-03-01",
      "status": "COMPLETED",
      // ... resto de campos
    },
    {
      "id": "enrollment-uuid-2",
      "studentId": "student-uuid-1",
      "classroomId": "classroom-2025-001",
      "enrollmentNumber": "ENR-2025-001",
      "enrollmentDate": "2025-03-01",
      "status": "ACTIVE",
      // ... resto de campos
    }
  ]
}
```

### 🔍 **7. OBTENER MATRÍCULAS POR AULA**
```http
GET /api/v1/enrollments/classroom/{classroomId}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student enrollments retrieved by classroom ID successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "enrollment-uuid-1",
      "studentId": "student-uuid-1",
      "classroomId": "classroom-2024-001",
      // ... resto de campos
    },
    {
      "id": "enrollment-uuid-3",
      "studentId": "student-uuid-3",
      "classroomId": "classroom-2024-001",
      // ... resto de campos
    }
  ]
}
```

### 🔍 **8. BUSCAR POR NÚMERO DE MATRÍCULA**
```http
GET /api/v1/enrollments/enrollment-number/{enrollmentNumber}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student enrollment retrieved by enrollment number successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": {
    "id": "enrollment-uuid-1",
    "studentId": "student-uuid-1",
    "classroomId": "classroom-2024-001",
    "enrollmentNumber": "ENR-2024-001",
    // ... resto de campos
  }
}
```

### 🔍 **9. FILTRAR POR STATUS DE MATRÍCULA**
```http
GET /api/v1/enrollments/status/{status}
```

**📤 Respuesta:**
```json
{
  "metadata": {
    "status": 200,
    "message": "Student enrollments retrieved by status successfully",
    "timestamp": "2025-09-06T18:45:30.123456"
  },
  "data": [
    {
      "id": "enrollment-uuid-1",
      "studentId": "student-uuid-1",
      "status": "ACTIVE",
      // ... resto de campos
    }
  ]
}
```

---

## 🚀 INSTALACIÓN Y EJECUCIÓN

### 📋 **Prerequisitos**
- **Java 17** o superior
- **Maven 3.6+**
- **MongoDB** (local o cloud)
- **Docker** (opcional)

### 🔧 **Configuración**

1. **Clonar el repositorio:**
```bash
git clone https://github.com/Omarrivv/vg-ms-students.git
cd vg-ms-students
```

2. **Configurar MongoDB** en `application.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://usuario:password@cluster.mongodb.net/?retryWrites=true&w=majority
      database: vg_ms_students
```

3. **Compilar y ejecutar:**
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Generar JAR
mvn clean package -DskipTests

# Ejecutar aplicación
java -jar target/vg-ms-students-1.0.jar
```

### 🐳 **Docker**

```bash
# Construir imagen
docker build -t vg-ms-students:1.0 .

# Ejecutar contenedor
docker run -p 8102:8102 \
  -e PORT=8102 \
  -e SPRING_DATA_MONGODB_URI=tu-mongodb-uri \
  vg-ms-students:1.0
```

---

## 🧪 EJEMPLOS PRÁCTICOS CON CURL

### 👤 **Gestión de Estudiantes**

```bash
# 1. Crear estudiante
curl -X POST http://localhost:8102/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "María Elena",
    "lastName": "Rodríguez Sánchez",
    "documentType": "DNI",
    "documentNumber": "12345678",
    "birthDate": "2012-08-20",
    "gender": "FEMALE",
    "address": "Calle Las Flores 456",
    "district": "San Isidro",
    "province": "Lima",
    "department": "Lima",
    "phone": "987654321",
    "email": "maria.rodriguez@email.com",
    "guardianName": "Elena",
    "guardianLastName": "Sánchez",
    "guardianDocumentType": "DNI",
    "guardianDocumentNumber": "87654321",
    "guardianPhone": "912345678",
    "guardianEmail": "elena.sanchez@email.com",
    "guardianRelationship": "MOTHER"
  }'

# 2. Obtener todos los estudiantes
curl http://localhost:8102/api/v1/students

# 3. Buscar por documento
curl http://localhost:8102/api/v1/students/document/12345678

# 4. Filtrar por género
curl http://localhost:8102/api/v1/students/gender/FEMALE

# 5. Actualizar estudiante
curl -X PUT http://localhost:8102/api/v1/students/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "María Elena",
    "lastName": "Rodríguez Sánchez",
    "documentType": "DNI",
    "documentNumber": "12345678",
    "birthDate": "2012-08-20",
    "gender": "FEMALE",
    "address": "Nueva Dirección 789",
    "district": "Miraflores",
    "province": "Lima",
    "department": "Lima",
    "phone": "987654322",
    "email": "maria.rodriguez.updated@email.com",
    "guardianName": "Elena",
    "guardianLastName": "Sánchez",
    "guardianDocumentType": "DNI",
    "guardianDocumentNumber": "87654321",
    "guardianPhone": "912345678",
    "guardianEmail": "elena.sanchez@email.com",
    "guardianRelationship": "MOTHER"
  }'
```

### 📚 **Gestión de Matrículas**

```bash
# 1. Crear matrícula
curl -X POST http://localhost:8102/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "uuid-del-estudiante",
    "classroomId": "aula-2025-primero-a",
    "enrollmentNumber": "MAT-2025-001",
    "enrollmentDate": "2025-03-01"
  }'

# 2. Obtener matrículas por estudiante
curl http://localhost:8102/api/v1/enrollments/student/{studentId}

# 3. Filtrar por aula
curl http://localhost:8102/api/v1/enrollments/classroom/aula-2025-primero-a

# 4. Actualizar status
curl -X PUT http://localhost:8102/api/v1/enrollments/{id}/status/COMPLETED

# 5. Buscar por número de matrícula
curl http://localhost:8102/api/v1/enrollments/enrollment-number/MAT-2025-001
```

---

## 🔧 STACK TECNOLÓGICO

| Componente | Tecnología | Versión | Descripción |
|------------|------------|---------|-------------|
| **Framework** | Spring Boot | 3.1.1 | Framework principal Java |
| **Programación Reactiva** | Spring WebFlux | 6.x | APIs no bloqueantes |
| **Base de Datos** | MongoDB | 5.x | Base de datos NoSQL |
| **Driver BD** | Spring Data MongoDB Reactive | 4.x | Acceso reactivo a MongoDB |
| **JDK** | OpenJDK | 17 | Plataforma Java |
| **Build Tool** | Maven | 3.9.x | Gestión de dependencias |
| **Contenedor** | Docker | Latest | Containerización |
| **Validaciones** | Jakarta Validation | 3.x | Validación de beans |
| **Mapping** | MapStruct | 1.5.x | Mapeo entre objetos |
| **Logging** | Logback | 1.4.x | Sistema de logs |

---

## 📊 ESTADO DEL PROYECTO

```
✅ COMPLETADO - Implementación PRS completa
✅ COMPLETADO - Arquitectura hexagonal
✅ COMPLETADO - APIs REST reactivas
✅ COMPLETADO - Validaciones de negocio
✅ COMPLETADO - Manejo centralizado de errores
✅ COMPLETADO - Configuración Docker
✅ COMPLETADO - Base de datos MongoDB
✅ COMPLETADO - Documentación completa
✅ COMPLETADO - Mappers y utilidades
✅ COMPLETADO - Testing unitario básico
```

---

## 🎯 ROADMAP FUTURO

### 🔄 **Versión 1.1**
- [ ] **Swagger/OpenAPI 3.0** documentación interactiva
- [ ] **Spring Security** autenticación JWT
- [ ] **Redis Cache** optimización consultas
- [ ] **Tests de integración** TestContainers

### 📊 **Versión 1.2**
- [ ] **Reportes PDF** estudiantes/matrículas
- [ ] **Importación masiva** CSV/Excel
- [ ] **Notificaciones** email/SMS
- [ ] **Métricas** Micrometer/Prometheus

### 🚀 **Versión 2.0**
- [ ] **Event Sourcing** auditoría completa
- [ ] **CQRS Pattern** separación comando/consulta  
- [ ] **GraphQL API** alternativa a REST
- [ ] **Microservices** decomposición modular

---

## 📞 SOPORTE

- **Repositorio:** [https://github.com/Omarrivv/vg-ms-students](https://github.com/Omarrivv/vg-ms-students)
- **Autor:** Omar Rivera
- **Email:** omar.rivera@vallegrande.edu.pe
- **Institución:** Valle Grande

---

**🚀 El microservicio vg-ms-students está completamente implementado con todas las especificaciones PRS y listo para producción!**
# pruebascanales_revision_intermedia
