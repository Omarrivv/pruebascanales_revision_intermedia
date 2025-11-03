package pe.edu.vallegrande.msvstudents.application.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.msvstudents.domain.enums.DocumentType;
import pe.edu.vallegrande.msvstudents.domain.enums.Gender;
import pe.edu.vallegrande.msvstudents.domain.enums.Status;
import pe.edu.vallegrande.msvstudents.domain.model.Student;
import pe.edu.vallegrande.msvstudents.infrastructure.dto.request.CreateStudentRequest;
import pe.edu.vallegrande.msvstudents.infrastructure.dto.response.StudentResponse;
import pe.edu.vallegrande.msvstudents.infrastructure.exception.ResourceNotFoundException;
import pe.edu.vallegrande.msvstudents.infrastructure.repository.StudentRepository;
import pe.edu.vallegrande.msvstudents.infrastructure.repository.StudentEnrollmentRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para StudentServiceImpl
 * 
 * Esta clase contiene pruebas para verificar el comportamiento correcto
 * del servicio de estudiantes, incluyendo la creación de estudiantes,
 * búsqueda por ID y manejo de errores.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService - Pruebas Unitarias")
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentEnrollmentRepository studentEnrollmentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student testStudent;
    private CreateStudentRequest createRequest;
    private String institutionId;

    /**
     * Configuración inicial que se ejecuta antes de cada prueba
     * Prepara los datos de prueba que serán utilizados en los tests
     */
    @BeforeEach
    void setUp() {
        institutionId = "inst-123";
        
        // Configuración del estudiante de prueba
        testStudent = new Student();
        testStudent.setId("student-123");
        testStudent.setInstitutionId(institutionId);
        testStudent.setFirstName("Juan");
        testStudent.setLastName("Pérez");
        testStudent.setDocumentType(DocumentType.DNI);
        testStudent.setDocumentNumber("12345678");
        testStudent.setBirthDate(LocalDate.of(2005, 5, 15));
        testStudent.setGender(Gender.MALE);
        testStudent.setAddress("Av. Principal 123");
        testStudent.setPhone("987654321");
        testStudent.setParentPhone("912345678");
        testStudent.setParentEmail("padre@email.com");
        testStudent.setParentName("Carlos Pérez");
        testStudent.setStatus(Status.ACTIVE);
        testStudent.setCreatedAt(LocalDateTime.now());
        testStudent.setUpdatedAt(LocalDateTime.now());

        // Configuración de la solicitud de creación
        createRequest = new CreateStudentRequest();
        createRequest.setFirstName("Juan");
        createRequest.setLastName("Pérez");
        createRequest.setDocumentType(DocumentType.DNI);
        createRequest.setDocumentNumber("12345678");
        createRequest.setBirthDate(LocalDate.of(2005, 5, 15));
        createRequest.setGender(Gender.MALE);
        createRequest.setAddress("Av. Principal 123");
        createRequest.setPhone("987654321");
        createRequest.setParentPhone("912345678");
        createRequest.setParentEmail("padre@email.com");
        createRequest.setParentName("Carlos Pérez");
    }

    /**
     * Prueba: Crear estudiante exitosamente
     * 
     * Verifica que el servicio pueda crear un nuevo estudiante cuando:
     * - No existe un estudiante con el mismo número de documento en la institución
     * - Los datos proporcionados son válidos
     * 
     * Expectativa: El estudiante se crea correctamente y retorna la respuesta esperada
     */
    @Test
    @DisplayName("Debería crear un estudiante exitosamente cuando no existe duplicado")
    void shouldCreateStudentSuccessfully_WhenNoDuplicateExists() {
        // Given - Preparación de datos
        when(studentRepository.findByDocumentNumberAndInstitutionId(
            eq(createRequest.getDocumentNumber()), 
            eq(institutionId)
        )).thenReturn(Mono.empty());
        
        when(studentRepository.save(any(Student.class)))
            .thenReturn(Mono.just(testStudent));

        // When - Ejecución del método bajo prueba
        Mono<StudentResponse> result = studentService.createStudent(createRequest, institutionId);

        // Then - Verificación de resultados
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(testStudent.getId(), response.getId());
                assertEquals(testStudent.getFirstName(), response.getFirstName());
                assertEquals(testStudent.getLastName(), response.getLastName());
                assertEquals(testStudent.getDocumentNumber(), response.getDocumentNumber());
                assertEquals(testStudent.getInstitutionId(), response.getInstitutionId());
            })
            .verifyComplete();
    }

    /**
     * Prueba: Error al crear estudiante duplicado
     * 
     * Verifica que el servicio lance una excepción cuando:
     * - Ya existe un estudiante con el mismo número de documento en la institución
     * 
     * Expectativa: Se lanza IllegalArgumentException con mensaje descriptivo
     */
    @Test
    @DisplayName("Debería lanzar excepción cuando intenta crear estudiante duplicado")
    void shouldThrowException_WhenStudentAlreadyExists() {
        // Given - Preparación: simular que ya existe un estudiante
        when(studentRepository.findByDocumentNumberAndInstitutionId(
            eq(createRequest.getDocumentNumber()), 
            eq(institutionId)
        )).thenReturn(Mono.just(testStudent));

        // When & Then - Ejecución y verificación
        StepVerifier.create(studentService.createStudent(createRequest, institutionId))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException &&
                throwable.getMessage().contains("Student with document number 12345678 already exists")
            )
            .verify();
    }

    /**
     * Prueba: Buscar estudiante por ID exitosamente
     * 
     * Verifica que el servicio pueda encontrar un estudiante cuando:
     * - Se proporciona un ID válido de un estudiante existente
     * 
     * Expectativa: Retorna la información completa del estudiante
     */
    @Test
    @DisplayName("Debería encontrar estudiante por ID cuando existe")
    void shouldFindStudentById_WhenStudentExists() {
        // Given - Preparación
        String studentId = "student-123";
        when(studentRepository.findById(eq(studentId)))
            .thenReturn(Mono.just(testStudent));

        // When - Ejecución
        Mono<StudentResponse> result = studentService.findById(studentId);

        // Then - Verificación
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(testStudent.getId(), response.getId());
                assertEquals(testStudent.getFirstName(), response.getFirstName());
                assertEquals(testStudent.getLastName(), response.getLastName());
                assertEquals(testStudent.getDocumentNumber(), response.getDocumentNumber());
                assertEquals(testStudent.getStatus(), response.getStatus());
            })
            .verifyComplete();
    }

    /**
     * Prueba: Error al buscar estudiante inexistente
     * 
     * Verifica que el servicio lance una excepción cuando:
     * - Se busca un estudiante con un ID que no existe en la base de datos
     * 
     * Expectativa: Se lanza ResourceNotFoundException con mensaje descriptivo
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException cuando estudiante no existe")
    void shouldThrowResourceNotFoundException_WhenStudentNotFound() {
        // Given - Preparación: simular estudiante no encontrado
        String nonExistentId = "non-existent-id";
        when(studentRepository.findById(eq(nonExistentId)))
            .thenReturn(Mono.empty());

        // When & Then - Ejecución y verificación
        StepVerifier.create(studentService.findById(nonExistentId))
            .expectErrorMatches(throwable -> 
                throwable instanceof ResourceNotFoundException &&
                throwable.getMessage().contains("Student not found with id: " + nonExistentId)
            )
            .verify();
    }

    /**
     * Prueba: Verificar existencia de estudiante
     * 
     * Verifica que el método existsById funcione correctamente para:
     * - IDs de estudiantes que existen
     * - IDs de estudiantes que no existen
     * 
     * Expectativa: Retorna true si existe, false si no existe
     */
    @Test
    @DisplayName("Debería verificar correctamente la existencia de estudiante")
    void shouldCheckStudentExistence_Correctly() {
        // Given - Preparación
        String existingId = "existing-student";
        String nonExistingId = "non-existing-student";
        
        when(studentRepository.existsById(eq(existingId)))
            .thenReturn(Mono.just(true));
        when(studentRepository.existsById(eq(nonExistingId)))
            .thenReturn(Mono.just(false));

        // When & Then - Para estudiante existente
        StepVerifier.create(studentService.existsById(existingId))
            .expectNext(true)
            .verifyComplete();

        // When & Then - Para estudiante no existente
        StepVerifier.create(studentService.existsById(nonExistingId))
            .expectNext(false)
            .verifyComplete();
    }
}