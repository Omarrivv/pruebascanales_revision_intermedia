# Documentación de Pruebas Unitarias - Microservicio de Estudiantes

## Índice
1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Arquitectura de Pruebas](#arquitectura-de-pruebas)
3. [Herramientas y Frameworks](#herramientas-y-frameworks)
4. [Prueba 1: StudentServiceImplTest](#prueba-1-studentserviceimpltest)
5. [Prueba 2: StudentControllerTest](#prueba-2-studentcontrollertest)
6. [Prueba 3: StudentMapperTest](#prueba-3-studentmappertest)
7. [Patrones y Mejores Prácticas](#patrones-y-mejores-prácticas)
8. [Ejecución de Pruebas](#ejecución-de-pruebas)
9. [Cobertura de Código](#cobertura-de-código)
10. [Troubleshooting](#troubleshooting)

---

## Resumen Ejecutivo

Este documento presenta la implementación completa de **3 pruebas unitarias** para el microservicio de estudiantes desarrollado en Spring Boot con WebFlux y MongoDB. Las pruebas cubren las tres capas principales de la arquitectura:

- **Capa de Servicio**: `StudentServiceImpl` - Lógica de negocio
- **Capa de Controlador**: `StudentController` - API REST endpoints
- **Capa de Utilidades**: `StudentMapper` - Transformaciones de datos

### Cobertura de Funcionalidades Probadas

| Componente | Funcionalidades Cubiertas | Escenarios de Prueba |
|------------|---------------------------|---------------------|
| **StudentService** | Creación, búsqueda, validación de duplicados | 6 escenarios |
| **StudentController** | Endpoints REST, validación de headers, manejo de errores | 7 escenarios |
| **StudentMapper** | Mapeo de DTOs, actualización parcial, generación de IDs | 6 escenarios |

---

## Arquitectura de Pruebas

### Estructura de Directorios

```
src/test/java/pe/edu/vallegrande/msvstudents/
├── application/
│   └── service/
│       └── impl/
│           └── StudentServiceImplTest.java
├── infrastructure/
│   ├── rest/
│   │   └── StudentControllerTest.java
│   └── util/
│       └── StudentMapperTest.java
```

### Patrón de Pruebas Utilizado

Las pruebas siguen el patrón **AAA (Arrange-Act-Assert)**:

```java
@Test
void testMethod() {
    // Arrange (Given) - Preparación de datos
    // Act (When) - Ejecución del método bajo prueba
    // Assert (Then) - Verificación de resultados
}
```

---

## Herramientas y Frameworks

### Dependencias de Testing

```xml
<!-- Spring Boot Test Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Reactor Test para WebFlux -->
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Frameworks y Librerías

| Framework | Propósito | Uso en las Pruebas |
|-----------|-----------|-------------------|
| **JUnit 5** | Framework base de testing | Anotaciones `@Test`, `@BeforeEach`, `@DisplayName` |
| **Mockito** | Mocking y stubbing | `@Mock`, `@InjectMocks`, `when().thenReturn()` |
| **Spring Test** | Testing de Spring Context | `@WebFluxTest`, `WebTestClient` |
| **Reactor Test** | Testing de streams reactivos | `StepVerifier` |
| **AssertJ** | Assertions fluidas | Incluido en spring-boot-starter-test |

---

## Prueba 1: StudentServiceImplTest

### Descripción General

Prueba unitaria para la capa de servicio que contiene la lógica de negocio principal del manejo de estudiantes.

### Componentes Probados

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService - Pruebas Unitarias")
class StudentServiceImplTest {
    @Mock private StudentRepository studentRepository;
    @Mock private StudentEnrollmentRepository studentEnrollmentRepository;
    @InjectMocks private StudentServiceImpl studentService;
}
```

### Escenarios de Prueba

#### 1. Creación Exitosa de Estudiante

**Objetivo**: Verificar que un estudiante se crea correctamente cuando no existe duplicado.

```java
@Test
@DisplayName("Debería crear un estudiante exitosamente cuando no existe duplicado")
void shouldCreateStudentSuccessfully_WhenNoDuplicateExists()
```

**Flujo de la Prueba**:
1. **Arrange**: Configura mocks para simular que no existe estudiante duplicado
2. **Act**: Llama al método `createStudent()`
3. **Assert**: Verifica que la respuesta contiene los datos correctos

**Técnicas Utilizadas**:
- **Mockito**: `when().thenReturn()` para simular comportamiento del repositorio
- **StepVerifier**: Para probar flujos reactivos (Mono/Flux)
- **Assertions**: Verificación detallada de cada campo del resultado

#### 2. Manejo de Estudiantes Duplicados

**Objetivo**: Verificar que se lance excepción al intentar crear estudiante duplicado.

```java
@Test
@DisplayName("Debería lanzar excepción cuando intenta crear estudiante duplicado")
void shouldThrowException_WhenStudentAlreadyExists()
```

**Aspectos Clave**:
- Simula la existencia previa de un estudiante
- Verifica que se lance `IllegalArgumentException`
- Comprueba que el mensaje de error sea descriptivo

#### 3. Búsqueda por ID

**Objetivo**: Verificar la búsqueda exitosa de estudiantes por ID.

```java
@Test
@DisplayName("Debería encontrar estudiante por ID cuando existe")
void shouldFindStudentById_WhenStudentExists()
```

**Validaciones**:
- Mapeo correcto de entidad a DTO de respuesta
- Preservación de todos los campos de datos
- Comportamiento reactivo correcto

#### 4. Manejo de Recursos No Encontrados

**Objetivo**: Verificar manejo de errores cuando no se encuentra un estudiante.

```java
@Test
@DisplayName("Debería lanzar ResourceNotFoundException cuando estudiante no existe")
void shouldThrowResourceNotFoundException_WhenStudentNotFound()
```

**Características**:
- Prueba de caso de error específico
- Verificación de tipo y mensaje de excepción
- Uso de `Mono.empty()` para simular resultado vacío

### Configuración de Datos de Prueba

```java
@BeforeEach
void setUp() {
    // Configuración detallada de objetos de prueba
    testStudent = new Student();
    testStudent.setId("student-123");
    testStudent.setFirstName("Juan");
    // ... más configuración
    
    createRequest = new CreateStudentRequest();
    createRequest.setFirstName("Juan");
    // ... configuración del request
}
```

### Patrones de Testing Aplicados

1. **Test Fixtures**: Datos de prueba reutilizables configurados en `@BeforeEach`
2. **Mocking**: Simulación de dependencias externas
3. **Reactive Testing**: Uso de `StepVerifier` para flujos asincrónicos
4. **Descriptive Naming**: Nombres de métodos que describen el comportamiento esperado

---

## Prueba 2: StudentControllerTest

### Descripción General

Prueba de integración para la capa de controlador REST, validando endpoints HTTP, manejo de headers de seguridad y serialización JSON.

### Componentes Probados

```java
@WebFluxTest(StudentController.class)
@ContextConfiguration(classes = {StudentController.class, GlobalExceptionHandler.class})
@DisplayName("StudentController - Pruebas de Integración")
class StudentControllerTest {
    @Autowired private WebTestClient webTestClient;
    @MockBean private StudentService studentService;
}
```

### Configuración de Testing

#### WebFluxTest
- Carga solo el contexto web mínimo necesario
- Configura automáticamente `WebTestClient`
- Optimiza velocidad de ejecución de pruebas

#### MockBean
- Reemplaza el bean de servicio real con un mock
- Permite control total sobre respuestas del servicio
- Aislamiento de la capa de controlador

### Escenarios de Prueba

#### 1. Creación Exitosa con Headers Válidos

**Endpoint**: `POST /api/v1/students/secretary/create`

```java
@Test
@DisplayName("POST /secretary/create - Debería crear estudiante con headers válidos")
void shouldCreateStudent_WhenValidHeadersProvided()
```

**Validaciones**:
- **Headers de Seguridad**: X-User-Id, X-User-Roles, X-Institution-Id
- **Content-Type**: application/json
- **Estructura de Respuesta**: ApiResponse con datos anidados
- **Códigos HTTP**: 200 OK para éxito

**Ejemplo de Ejecución**:
```java
webTestClient.post()
    .uri("/api/v1/students/secretary/create")
    .header("X-User-Id", userId)
    .header("X-User-Roles", "SECRETARY")
    .header("X-Institution-Id", institutionId)
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(objectMapper.writeValueAsString(validCreateRequest))
    .exchange()
    .expectStatus().isOk()
    .expectBody()
    .jsonPath("$.success").isEqualTo(true)
    .jsonPath("$.data.student.firstName").isEqualTo("María");
```

#### 2. Validación de Headers de Seguridad

**Objetivo**: Verificar que endpoints rechacen requests sin headers requeridos.

```java
@Test
@DisplayName("POST /secretary/create - Debería fallar sin headers requeridos")
void shouldFailToCreateStudent_WhenMissingRequiredHeaders()
```

**Casos de Prueba**:
- Sin ningún header → 401 Unauthorized
- Solo con X-User-Id → 401 Unauthorized  
- Sin X-Institution-Id → 401 Unauthorized

#### 3. Listado de Estudiantes por Institución

**Endpoint**: `GET /api/v1/students/secretary`

```java
@Test
@DisplayName("GET /secretary - Debería obtener estudiantes de la institución")
void shouldGetStudentsByInstitution_WhenValidHeaders()
```

**Características**:
- Retorna lista de estudiantes en formato JSON
- Valida estructura de respuesta ApiResponse
- Verifica paginación y ordenamiento implícito

#### 4. Control de Acceso por Roles

**Objetivo**: Verificar que usuarios sin permisos sean rechazados.

```java
@Test
@DisplayName("GET /secretary - Debería fallar con rol insuficiente")
void shouldFailToGetStudents_WhenInsufficientRole()
```

**Validaciones de Seguridad**:
- STUDENT no puede acceder a endpoints de SECRETARY
- TEACHER no puede acceder a endpoints de SECRETARY
- Respuesta HTTP 403 Forbidden apropiada

### Manejo de Errores

#### Propagación de Errores del Servicio

```java
@Test
@DisplayName("POST /secretary/create - Debería manejar errores del servicio")
void shouldHandleServiceErrors_Appropriately()
```

**Mapeo de Excepciones**:
- `IllegalArgumentException` → 400 Bad Request
- `ResourceNotFoundException` → 404 Not Found
- `InsufficientPermissionsException` → 403 Forbidden
- Otras excepciones → 500 Internal Server Error

### Serialización y Deserialización

#### Validación de Datos de Entrada

```java
@Test
@DisplayName("POST /secretary/create - Debería validar datos de entrada")
void shouldValidateInputData_WhenCreatingStudent()
```

**Validaciones Bean Validation**:
- Campos requeridos no pueden estar vacíos
- Formatos de email válidos
- Longitudes de campos dentro de límites
- Tipos de datos correctos (fechas, enums)

---

## Prueba 3: StudentMapperTest

### Descripción General

Prueba unitaria para la clase utilitaria que maneja las transformaciones entre DTOs y entidades del dominio.

### Componentes Probados

```java
@DisplayName("StudentMapper - Pruebas de Transformación")
class StudentMapperTest {
    // Métodos estáticos, no requiere inyección de dependencias
    // Pruebas directas de transformación de datos
}
```

### Escenarios de Prueba

#### 1. Mapeo de CreateStudentRequest a Student

**Objetivo**: Verificar conversión completa de DTO de entrada a entidad.

```java
@Test
@DisplayName("toEntity() - Debería mapear CreateStudentRequest a Student correctamente")
void shouldMapCreateRequestToEntity_WithAllFieldsCorrect()
```

**Verificaciones Detalladas**:
- **Mapeo de Campos**: Todos los campos del request se copian correctamente
- **Generación de ID**: Se genera UUID único automáticamente
- **Valores por Defecto**: Status se establece como ACTIVE
- **Timestamps**: Se configuran createdAt y updatedAt
- **Inmutabilidad**: El request original no se modifica

**Código de Verificación**:
```java
Student result = StudentMapper.toEntity(createRequest, institutionId);

assertNotNull(result.getId(), "El ID debe ser generado automáticamente");
assertEquals(institutionId, result.getInstitutionId());
assertEquals(createRequest.getFirstName(), result.getFirstName());
assertEquals(Status.ACTIVE, result.getStatus());
assertNotNull(result.getCreatedAt());
```

#### 2. Mapeo de Student a StudentResponse

**Objetivo**: Verificar conversión de entidad a DTO de salida.

```java
@Test
@DisplayName("toResponse() - Debería mapear Student a StudentResponse correctamente")
void shouldMapEntityToResponse_WithAllFieldsCorrect()
```

**Aspectos Validados**:
- **Integridad de Datos**: Todos los campos se preservan
- **Conversión de Enums**: Se mantienen tipos apropiados
- **Inmutabilidad**: La entidad original no se modifica
- **Nullabilidad**: Campos nulos se manejan correctamente

#### 3. Actualización Parcial de Entidades

**Objetivo**: Verificar actualización selectiva de campos.

```java
@Test
@DisplayName("updateEntity() - Debería actualizar solo campos no nulos")
void shouldUpdateEntity_OnlyNonNullFields()
```

**Lógica de Actualización**:
- Solo campos no-null en el request se actualizan
- Campos null en el request se ignoran (mantienen valor original)
- updatedAt se actualiza automáticamente
- createdAt permanece inmutable

**Ejemplo de Comportamiento**:
```java
UpdateStudentRequest partialUpdate = new UpdateStudentRequest();
partialUpdate.setFirstName("Nuevo Nombre");  // Se actualiza
partialUpdate.setLastName(null);             // Se ignora

Student result = StudentMapper.updateEntity(existing, partialUpdate);
assertEquals("Nuevo Nombre", result.getFirstName());      // Actualizado
assertEquals(originalLastName, result.getLastName());     // Sin cambio
```

#### 4. Generación de IDs Únicos

**Objetivo**: Verificar unicidad en generación de identificadores.

```java
@Test
@DisplayName("toEntity() - Debería generar IDs únicos para múltiples estudiantes")
void shouldGenerateUniqueIds_ForMultipleStudents()
```

**Validaciones**:
- Múltiples llamadas producen IDs diferentes
- IDs siguen formato UUID válido
- No hay colisiones en generación concurrente

#### 5. Actualización Completa

**Objetivo**: Verificar comportamiento con todos los campos poblados.

```java
@Test
@DisplayName("updateEntity() - Debería actualizar todos los campos cuando están presentes")
void shouldUpdateAllFields_WhenAllFieldsProvided()
```

#### 6. Manejo Robusto de Valores Nulos

**Objetivo**: Verificar comportamiento ante datos faltantes.

```java
@Test
@DisplayName("Mapeo debe manejar valores nulos correctamente")
void shouldHandleNullValues_Gracefully()
```

**Casos Edge Probados**:
- Entidades con campos nulos
- Requests parcialmente poblados
- Respuestas con datos faltantes

### Patrones de Testing Específicos

#### Data Builder Pattern

```java
@BeforeEach
void setUp() {
    createRequest = new CreateStudentRequest();
    createRequest.setFirstName("Ana");
    createRequest.setLastName("Martínez");
    // Configuración completa y reutilizable
}
```

#### Property-based Testing

Verificación exhaustiva de propiedades del mapeo:
- Idempotencia: `toResponse(toEntity(request))` es consistente
- Reversibilidad parcial: Datos no se pierden en transformaciones
- Invariantes: ID y timestamps siguen reglas específicas

---

## Patrones y Mejores Prácticas

### 1. Nomenclatura Descriptiva

#### Estructura de Nombres de Métodos
```java
// Patrón: should[ExpectedBehavior]_When[Condition]
shouldCreateStudentSuccessfully_WhenNoDuplicateExists()
shouldThrowException_WhenStudentAlreadyExists()
shouldFindStudentById_WhenStudentExists()
```

#### Beneficios:
- **Legibilidad**: Nombres auto-documentados
- **Mantenibilidad**: Fácil identificación de casos de prueba
- **Reportes**: Información clara en reportes de testing

### 2. Anotaciones DisplayName

```java
@DisplayName("StudentService - Pruebas Unitarias")
class StudentServiceImplTest {
    
    @Test
    @DisplayName("Debería crear un estudiante exitosamente cuando no existe duplicado")
    void shouldCreateStudentSuccessfully_WhenNoDuplicateExists() {
        // ...
    }
}
```

**Ventajas**:
- Descripciones en lenguaje natural
- Reportes de prueba más informativos
- Documentación integrada

### 3. Patrón AAA (Arrange-Act-Assert)

```java
@Test
void testExample() {
    // Arrange (Given) - Configuración
    when(repository.findById("id")).thenReturn(Mono.just(student));
    
    // Act (When) - Ejecución
    Mono<StudentResponse> result = service.findById("id");
    
    // Assert (Then) - Verificación
    StepVerifier.create(result)
        .assertNext(response -> assertEquals("name", response.getFirstName()))
        .verifyComplete();
}
```

### 4. Test Fixtures Reutilizables

```java
@BeforeEach
void setUp() {
    // Configuración común para todas las pruebas
    institutionId = "inst-123";
    
    testStudent = createTestStudent();
    createRequest = createValidRequest();
    updateRequest = createPartialUpdateRequest();
}

private Student createTestStudent() {
    Student student = new Student();
    // Configuración detallada
    return student;
}
```

### 5. Mocking Estratégico

#### Principios:
- **Mock External Dependencies**: Repositorios, servicios externos
- **Don't Mock Value Objects**: DTOs, entities simples
- **Verify Interactions**: Uso de `verify()` cuando sea necesario

```java
@Test
void shouldCallRepositoryWithCorrectParameters() {
    // Given
    when(repository.save(any(Student.class))).thenReturn(Mono.just(student));
    
    // When
    service.createStudent(request, institutionId);
    
    // Then
    verify(repository).save(argThat(s -> 
        s.getInstitutionId().equals(institutionId) &&
        s.getStatus() == Status.ACTIVE
    ));
}
```

### 6. Testing Reactivo con StepVerifier

#### Verificación de Flujos Exitosos
```java
StepVerifier.create(service.findById("id"))
    .assertNext(response -> {
        assertEquals("expected", response.getFirstName());
        assertNotNull(response.getId());
    })
    .verifyComplete();
```

#### Verificación de Errores
```java
StepVerifier.create(service.findById("nonexistent"))
    .expectErrorMatches(throwable -> 
        throwable instanceof ResourceNotFoundException &&
        throwable.getMessage().contains("not found")
    )
    .verify();
```

### 7. Assertions Específicas y Detalladas

```java
// ❌ Assertion genérica
assertEquals(expected, actual);

// ✅ Assertions específicas con mensajes descriptivos
assertEquals(expected.getId(), actual.getId(), 
    "El ID debe coincidir con el esperado");
assertTrue(actual.getCreatedAt().isAfter(startTime), 
    "La fecha de creación debe ser posterior al inicio de la prueba");
assertNotNull(actual.getUpdatedAt(), 
    "La fecha de actualización debe ser establecida");
```

---

## Ejecución de Pruebas

### Comandos Maven

#### Ejecutar Todas las Pruebas
```bash
mvn test
```

#### Ejecutar Pruebas Específicas por Clase
```bash
mvn test -Dtest=StudentServiceImplTest
mvn test -Dtest=StudentControllerTest
mvn test -Dtest=StudentMapperTest
```

#### Ejecutar Prueba Específica por Método
```bash
mvn test -Dtest=StudentServiceImplTest#shouldCreateStudentSuccessfully_WhenNoDuplicateExists
```

#### Ejecutar con Reportes Detallados
```bash
mvn test -Dmaven.surefire.debug=true
```

### Configuración de Profile de Testing

#### application-test.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/test_students_db
  
logging:
  level:
    pe.edu.vallegrande.msvstudents: DEBUG
    org.springframework.test: DEBUG
```

### Resultados Esperados

#### Salida Exitosa
```
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

#### Distribución de Pruebas por Clase
- **StudentServiceImplTest**: 6 pruebas
- **StudentControllerTest**: 7 pruebas  
- **StudentMapperTest**: 6 pruebas
- **Total**: 19 pruebas

---

## Cobertura de Código

### Herramientas Recomendadas

#### JaCoCo Maven Plugin
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Generar Reporte de Cobertura
```bash
mvn clean test jacoco:report
```

#### Visualizar Resultados
- **Ubicación**: `target/site/jacoco/index.html`
- **Navegador**: Abrir archivo en navegador web

### Métricas de Cobertura Esperadas

| Componente | Cobertura de Líneas | Cobertura de Ramas |
|------------|--------------------|--------------------|
| StudentServiceImpl | 85-95% | 80-90% |
| StudentController | 80-90% | 75-85% |
| StudentMapper | 95-100% | 90-100% |
| **Promedio General** | **85-95%** | **80-90%** |

### Interpretación de Métricas

#### Cobertura de Líneas (Line Coverage)
- **Verde (>80%)**: Cobertura excelente
- **Amarillo (60-80%)**: Cobertura aceptable
- **Rojo (<60%)**: Cobertura insuficiente

#### Cobertura de Ramas (Branch Coverage)
- Mide ejecución de todas las ramas condicionales
- Especialmente importante en lógica de validación
- Objetivo: >75% para código crítico

---

## Troubleshooting

### Problemas Comunes y Soluciones

#### 1. Error: MockBean no se Inicializa

**Síntoma**:
```
No qualifying bean of type 'StudentService' available
```

**Solución**:
```java
@WebFluxTest(StudentController.class)
@MockBean(StudentService.class)  // ✅ Agregar esta anotación
class StudentControllerTest {
    // ...
}
```

#### 2. StepVerifier Falla con Timeout

**Síntoma**:
```
VerifyError: expectation failed (expected onNext; actual onComplete)
```

**Solución**:
```java
// ❌ Incorrecto
StepVerifier.create(service.findAll())
    .expectNext(student1, student2)  // Orden incorrecto
    .verifyComplete();

// ✅ Correcto
StepVerifier.create(service.findAll())
    .recordWith(ArrayList::new)
    .thenConsumeWhile(x -> true)
    .consumeRecordedWith(list -> {
        assertEquals(2, list.size());
        assertTrue(list.contains(student1));
    })
    .verifyComplete();
```

#### 3. Headers de Seguridad no se Validan

**Síntoma**:
```
Test passes but should fail with security validation
```

**Solución**:
```java
// ✅ Asegurar que GlobalExceptionHandler esté incluido
@ContextConfiguration(classes = {
    StudentController.class, 
    GlobalExceptionHandler.class  // Necesario para manejo de errores
})
```

#### 4. JSON Serialization Falla

**Síntoma**:
```
HttpMessageNotWritableException: No converter for return value of type
```

**Solución**:
```java
// ✅ Configurar ObjectMapper explícitamente
@Autowired
private ObjectMapper objectMapper;

@Test
void test() {
    String json = objectMapper.writeValueAsString(request);
    webTestClient.post()
        .bodyValue(json)  // Usar string JSON en lugar de objeto
        .exchange();
}
```

#### 5. MongoDB Embedded Conflictos

**Síntoma**:
```
Unable to start embedded MongoDB
```

**Solución**:
```java
// ✅ Usar @MockBean para repositorios en lugar de MongoDB embedded
@WebFluxTest(StudentController.class)
class StudentControllerTest {
    @MockBean private StudentRepository repository;  // Mock instead of real DB
}
```

### Debug y Logging

#### Habilitar Logging Detallado

```yaml
# application-test.yml
logging:
  level:
    pe.edu.vallegrande.msvstudents: DEBUG
    org.springframework.test.web.reactive: DEBUG
    reactor.netty.http.client: DEBUG
```

#### Debugging Interactivo

```java
@Test
void debugTest() {
    System.out.println("Debug: " + actualValue);  // Logging temporal
    
    StepVerifier.create(service.method())
        .expectNextMatches(result -> {
            System.out.println("Received: " + result);  // Debug en lambda
            return result != null;
        })
        .verifyComplete();
}
```

### Validación de Dependencias

#### Verificar Versiones Compatibles

```bash
mvn dependency:tree | grep -E "(junit|mockito|reactor-test)"
```

#### Resolución de Conflictos

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.junit</groupId>
            <artifactId>junit-bom</artifactId>
            <version>5.8.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

## Conclusión

Esta implementación de pruebas unitarias proporciona:

### ✅ Cobertura Completa
- **3 capas arquitectónicas** cubiertas
- **19 escenarios de prueba** implementados
- **Casos edge y manejo de errores** incluidos

### ✅ Calidad y Mantenibilidad
- **Patrones de testing** establecidos
- **Documentación detallada** incluida
- **Facilidad de extensión** para nuevas funcionalidades

### ✅ Integración con CI/CD
- **Ejecución automatizable** con Maven
- **Reportes de cobertura** configurados
- **Feedback rápido** en desarrollo

### Próximos Pasos Recomendados

1. **Integrar con Pipeline CI/CD**
2. **Configurar Quality Gates** basados en cobertura
3. **Implementar pruebas de integración** complementarias
4. **Añadir pruebas de performance** con WebTestClient
5. **Documentar casos de prueba** adicionales para nuevas features

Esta base sólida de testing garantiza la confiabilidad y mantenibilidad del microservicio de estudiantes a medida que evoluciona.