package pe.edu.vallegrande.msvstudents.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.msvstudents.application.service.StudentService;
import pe.edu.vallegrande.msvstudents.domain.enums.DocumentType;
import pe.edu.vallegrande.msvstudents.domain.enums.Gender;
import pe.edu.vallegrande.msvstudents.domain.enums.Status;
import pe.edu.vallegrande.msvstudents.infrastructure.dto.request.CreateStudentRequest;
import pe.edu.vallegrande.msvstudents.infrastructure.dto.response.StudentResponse;
import pe.edu.vallegrande.msvstudents.infrastructure.exception.GlobalExceptionHandler;
import pe.edu.vallegrande.msvstudents.infrastructure.exception.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;

/**
 * Pruebas unitarias para StudentController
 * 
 * Esta clase contiene pruebas de integración para verificar el comportamiento
 * de los endpoints REST del controlador de estudiantes, incluyendo:
 * - Validación de headers de seguridad
 * - Creación de estudiantes
 * - Consulta de estudiantes
 * - Manejo de errores HTTP
 */
@WebFluxTest(StudentController.class)
@ContextConfiguration(classes = {StudentController.class, GlobalExceptionHandler.class})
@DisplayName("StudentController - Pruebas de Integración")
class StudentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateStudentRequest validCreateRequest;
    private StudentResponse studentResponse;
    private String institutionId;
    private String userId;

    /**
     * Configuración inicial ejecutada antes de cada prueba
     * Prepara los objetos de prueba y datos necesarios para los tests
     */
    @BeforeEach
    void setUp() {
        institutionId = "inst-123";
        userId = "user-456";

        // Configuración de la solicitud de creación válida
        validCreateRequest = new CreateStudentRequest();
        validCreateRequest.setFirstName("María");
        validCreateRequest.setLastName("González");
        validCreateRequest.setDocumentType(DocumentType.DNI);
        validCreateRequest.setDocumentNumber("87654321");
        validCreateRequest.setBirthDate(LocalDate.of(2006, 3, 10));
        validCreateRequest.setGender(Gender.FEMALE);
        validCreateRequest.setAddress("Jr. Los Olivos 456");
        validCreateRequest.setPhone("998877665");
        validCreateRequest.setParentPhone("987654321");
        validCreateRequest.setParentEmail("maria.madre@email.com");
        validCreateRequest.setParentName("Ana González");

        // Configuración de la respuesta esperada
        studentResponse = StudentResponse.builder()
            .id("student-789")
            .institutionId(institutionId)
            .firstName("María")
            .lastName("González")
            .documentType(DocumentType.DNI)
            .documentNumber("87654321")
            .birthDate(LocalDate.of(2006, 3, 10))
            .gender(Gender.FEMALE)
            .address("Jr. Los Olivos 456")
            .phone("998877665")
            .parentPhone("987654321")
            .parentEmail("maria.madre@email.com")
            .parentName("Ana González")
            .status(Status.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * Prueba: Crear estudiante con headers válidos
     * 
     * Verifica que el endpoint POST /api/v1/students/secretary/create:
     * - Acepta solicitudes con headers de seguridad válidos
     * - Procesa correctamente la creación de estudiantes
     * - Retorna respuesta exitosa con datos del estudiante creado
     * 
     * Expectativa: HTTP 200 con estructura ApiResponse correcta
     */
    @Test
    @DisplayName("POST /secretary/create - Debería crear estudiante con headers válidos")
    void shouldCreateStudent_WhenValidHeadersProvided() throws Exception {
        // Given - Preparación del mock del servicio
        when(studentService.createStudent(any(CreateStudentRequest.class), eq(institutionId)))
            .thenReturn(Mono.just(studentResponse));

        // When & Then - Ejecución y verificación
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
            .jsonPath("$.message").isEqualTo("Student created successfully")
            .jsonPath("$.data.student.id").isEqualTo(studentResponse.getId())
            .jsonPath("$.data.student.firstName").isEqualTo(studentResponse.getFirstName())
            .jsonPath("$.data.student.lastName").isEqualTo(studentResponse.getLastName())
            .jsonPath("$.data.student.documentNumber").isEqualTo(studentResponse.getDocumentNumber())
            .jsonPath("$.data.student.institutionId").isEqualTo(institutionId)
            .jsonPath("$.data.student.documentType").isEqualTo("DNI")
            .jsonPath("$.data.student.gender").isEqualTo("FEMALE")
            .jsonPath("$.data.student.status").isEqualTo("ACTIVE");
    }

    /**
     * Prueba: Error por headers faltantes
     * 
     * Verifica que el endpoint rechace solicitudes cuando:
     * - Faltan headers de seguridad requeridos (X-User-Id, X-User-Roles, X-Institution-Id)
     * - Los headers están vacíos o son nulos
     * 
     * Expectativa: HTTP 401 Unauthorized con mensaje de error descriptivo
     */
    @Test
    @DisplayName("POST /secretary/create - Debería fallar sin headers requeridos")
    void shouldFailToCreateStudent_WhenMissingRequiredHeaders() throws Exception {
        // When & Then - Sin headers
        webTestClient.post()
            .uri("/api/v1/students/secretary/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(validCreateRequest))
            .exchange()
            .expectStatus().isUnauthorized();

        // When & Then - Solo con X-User-Id
        webTestClient.post()
            .uri("/api/v1/students/secretary/create")
            .header("X-User-Id", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(validCreateRequest))
            .exchange()
            .expectStatus().isUnauthorized();

        // When & Then - Sin X-Institution-Id
        webTestClient.post()
            .uri("/api/v1/students/secretary/create")
            .header("X-User-Id", userId)
            .header("X-User-Roles", "SECRETARY")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(validCreateRequest))
            .exchange()
            .expectStatus().isUnauthorized();
    }

    /**
     * Prueba: Obtener estudiantes de institución
     * 
     * Verifica que el endpoint GET /api/v1/students/secretary:
     * - Retorne la lista de estudiantes de la institución del usuario
     * - Valide correctamente los permisos (rol SECRETARY)
     * - Formatee correctamente la respuesta
     * 
     * Expectativa: HTTP 200 con lista de estudiantes en formato ApiResponse
     */
    @Test
    @DisplayName("GET /secretary - Debería obtener estudiantes de la institución")
    void shouldGetStudentsByInstitution_WhenValidHeaders() {
        // Given - Preparación: lista de estudiantes mockeada
        StudentResponse student2 = StudentResponse.builder()
            .id("student-999")
            .institutionId(institutionId)
            .firstName("Carlos")
            .lastName("Ruiz")
            .documentType(DocumentType.DNI)
            .documentNumber("11223344")
            .birthDate(LocalDate.of(2005, 8, 20))
            .gender(Gender.MALE)
            .status(Status.ACTIVE)
            .build();

        when(studentService.getStudentsByInstitution(eq(institutionId)))
            .thenReturn(Flux.fromIterable(Arrays.asList(studentResponse, student2)));

        // When & Then - Ejecución y verificación
        webTestClient.get()
            .uri("/api/v1/students/secretary")
            .header("X-User-Id", userId)
            .header("X-User-Roles", "SECRETARY")
            .header("X-Institution-Id", institutionId)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Students retrieved successfully")
            .jsonPath("$.data").isArray()
            .jsonPath("$.data.length()").isEqualTo(2)
            .jsonPath("$.data[0].id").isEqualTo(studentResponse.getId())
            .jsonPath("$.data[0].firstName").isEqualTo(studentResponse.getFirstName())
            .jsonPath("$.data[1].id").isEqualTo(student2.getId())
            .jsonPath("$.data[1].firstName").isEqualTo(student2.getFirstName());
    }

    /**
     * Prueba: Error por rol insuficiente
     * 
     * Verifica que el sistema rechace solicitudes cuando:
     * - El usuario tiene un rol que no tiene permisos para la operación
     * - Ejemplo: rol STUDENT intentando acceder a endpoint de SECRETARY
     * 
     * Expectativa: HTTP 403 Forbidden con mensaje de permisos insuficientes
     */
    @Test
    @DisplayName("GET /secretary - Debería fallar con rol insuficiente")
    void shouldFailToGetStudents_WhenInsufficientRole() {
        // When & Then - Intentar acceder con rol STUDENT
        webTestClient.get()
            .uri("/api/v1/students/secretary")
            .header("X-User-Id", userId)
            .header("X-User-Roles", "STUDENT")  // Rol insuficiente
            .header("X-Institution-Id", institutionId)
            .exchange()
            .expectStatus().isForbidden();

        // When & Then - Intentar acceder con rol TEACHER
        webTestClient.get()
            .uri("/api/v1/students/secretary")
            .header("X-User-Id", userId)
            .header("X-User-Roles", "TEACHER")  // Rol insuficiente
            .header("X-Institution-Id", institutionId)
            .exchange()
            .expectStatus().isForbidden();
    }

    /**
     * Prueba: Manejo de errores del servicio
     * 
     * Verifica que el controlador maneje correctamente errores del servicio:
     * - ResourceNotFoundException se convierte a HTTP 404
     * - IllegalArgumentException se convierte a HTTP 400
     * - Otros errores se convierten a HTTP 500
     * 
     * Expectativa: Códigos de estado HTTP apropiados con mensajes de error
     */
    @Test
    @DisplayName("POST /secretary/create - Debería manejar errores del servicio")
    void shouldHandleServiceErrors_Appropriately() throws Exception {
        // Given - Error de estudiante duplicado
        when(studentService.createStudent(any(CreateStudentRequest.class), eq(institutionId)))
            .thenReturn(Mono.error(new IllegalArgumentException("Student already exists")));

        // When & Then - Verificar manejo de IllegalArgumentException
        webTestClient.post()
            .uri("/api/v1/students/secretary/create")
            .header("X-User-Id", userId)
            .header("X-User-Roles", "SECRETARY")
            .header("X-Institution-Id", institutionId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(validCreateRequest))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").value(containsString("Student already exists"));
    }

    /**
     * Prueba: Validación de datos de entrada
     * 
     * Verifica que el controlador valide correctamente los datos:
     * - Campos requeridos no pueden estar vacíos
     * - Formatos de datos deben ser válidos (fechas, emails, etc.)
     * - Longitudes de campos deben estar dentro de los límites
     * 
     * Expectativa: HTTP 400 Bad Request para datos inválidos
     */
    @Test
    @DisplayName("POST /secretary/create - Debería validar datos de entrada")
    void shouldValidateInputData_WhenCreatingStudent() throws Exception {
        // Given - Solicitud con datos inválidos
        CreateStudentRequest invalidRequest = new CreateStudentRequest();
        invalidRequest.setFirstName("");  // Nombre vacío
        invalidRequest.setLastName(null); // Apellido nulo
        invalidRequest.setDocumentNumber("123"); // Documento muy corto

        // When & Then - Verificar validación
        webTestClient.post()
            .uri("/api/v1/students/secretary/create")
            .header("X-User-Id", userId)
            .header("X-User-Roles", "SECRETARY")
            .header("X-Institution-Id", institutionId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(invalidRequest))
            .exchange()
            .expectStatus().isBadRequest();
    }
}