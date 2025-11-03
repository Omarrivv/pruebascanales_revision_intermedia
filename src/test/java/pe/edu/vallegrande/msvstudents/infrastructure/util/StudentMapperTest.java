package pe.edu.vallegrande.msvstudents.infrastructure.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.msvstudents.domain.enums.DocumentType;
import pe.edu.vallegrande.msvstudents.domain.enums.Gender;
import pe.edu.vallegrande.msvstudents.domain.enums.Status;
import pe.edu.vallegrande.msvstudents.domain.model.Student;
import pe.edu.vallegrande.msvstudents.infrastructure.dto.request.CreateStudentRequest;
import pe.edu.vallegrande.msvstudents.infrastructure.dto.request.UpdateStudentRequest;
import pe.edu.vallegrande.msvstudents.infrastructure.dto.response.StudentResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para StudentMapper
 * 
 * Esta clase contiene pruebas para verificar las transformaciones correctas
 * entre las diferentes representaciones de datos de estudiantes:
 * - DTO de request a entidad
 * - Entidad a DTO de response 
 * - Actualización de entidades existentes
 * - Manejo de valores nulos y casos edge
 */
@DisplayName("StudentMapper - Pruebas de Transformación")
class StudentMapperTest {

    private CreateStudentRequest createRequest;
    private UpdateStudentRequest updateRequest;
    private Student existingStudent;
    private String institutionId;

    /**
     * Configuración inicial ejecutada antes de cada prueba
     * Prepara los objetos de prueba necesarios para los tests de mapeo
     */
    @BeforeEach
    void setUp() {
        institutionId = "inst-456";

        // Configuración de solicitud de creación completa
        createRequest = new CreateStudentRequest();
        createRequest.setFirstName("Ana");
        createRequest.setLastName("Martínez");
        createRequest.setDocumentType(DocumentType.DNI);
        createRequest.setDocumentNumber("98765432");
        createRequest.setBirthDate(LocalDate.of(2004, 12, 25));
        createRequest.setGender(Gender.FEMALE);
        createRequest.setAddress("Calle Las Flores 789");
        createRequest.setPhone("955443322");
        createRequest.setParentName("Roberto Martínez");
        createRequest.setParentPhone("966554433");
        createRequest.setParentEmail("roberto.martinez@email.com");

        // Configuración de solicitud de actualización parcial
        updateRequest = new UpdateStudentRequest();
        updateRequest.setFirstName("Ana Lucía");
        updateRequest.setAddress("Av. Nueva Dirección 123");
        updateRequest.setPhone("944332211");
        // Nota: otros campos se mantienen como null para probar actualización parcial

        // Configuración de estudiante existente
        existingStudent = new Student();
        existingStudent.setId("student-existing-123");
        existingStudent.setInstitutionId(institutionId);
        existingStudent.setFirstName("Ana");
        existingStudent.setLastName("Martínez");
        existingStudent.setDocumentType(DocumentType.DNI);
        existingStudent.setDocumentNumber("98765432");
        existingStudent.setBirthDate(LocalDate.of(2004, 12, 25));
        existingStudent.setGender(Gender.FEMALE);
        existingStudent.setAddress("Calle Las Flores 789");
        existingStudent.setPhone("955443322");
        existingStudent.setParentName("Roberto Martínez");
        existingStudent.setParentPhone("966554433");
        existingStudent.setParentEmail("roberto.martinez@email.com");
        existingStudent.setStatus(Status.ACTIVE);
        existingStudent.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        existingStudent.setUpdatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
    }

    /**
     * Prueba: Mapeo de CreateStudentRequest a Student
     * 
     * Verifica que el método toEntity:
     * - Mapee correctamente todos los campos del request al Student
     * - Genere un ID único para el nuevo estudiante
     * - Asigne el institutionId proporcionado
     * - Establezca el status por defecto como ACTIVE
     * - Configure timestamps de creación y actualización
     * 
     * Expectativa: Entidad Student creada con todos los datos correctos
     */
    @Test
    @DisplayName("toEntity() - Debería mapear CreateStudentRequest a Student correctamente")
    void shouldMapCreateRequestToEntity_WithAllFieldsCorrect() {
        // When - Ejecutar el mapeo
        Student result = StudentMapper.toEntity(createRequest, institutionId);

        // Then - Verificar todos los campos mapeados
        assertNotNull(result.getId(), "El ID debe ser generado automáticamente");
        assertFalse(result.getId().isEmpty(), "El ID no debe estar vacío");
        
        assertEquals(institutionId, result.getInstitutionId(), 
            "El institutionId debe coincidir con el proporcionado");
        assertEquals(createRequest.getFirstName(), result.getFirstName(),
            "El nombre debe coincidir");
        assertEquals(createRequest.getLastName(), result.getLastName(),
            "El apellido debe coincidir");
        assertEquals(createRequest.getDocumentType(), result.getDocumentType(),
            "El tipo de documento debe coincidir");
        assertEquals(createRequest.getDocumentNumber(), result.getDocumentNumber(),
            "El número de documento debe coincidir");
        assertEquals(createRequest.getBirthDate(), result.getBirthDate(),
            "La fecha de nacimiento debe coincidir");
        assertEquals(createRequest.getGender(), result.getGender(),
            "El género debe coincidir");
        assertEquals(createRequest.getAddress(), result.getAddress(),
            "La dirección debe coincidir");
        assertEquals(createRequest.getPhone(), result.getPhone(),
            "El teléfono debe coincidir");
        assertEquals(createRequest.getParentName(), result.getParentName(),
            "El nombre del padre debe coincidir");
        assertEquals(createRequest.getParentPhone(), result.getParentPhone(),
            "El teléfono del padre debe coincidir");
        assertEquals(createRequest.getParentEmail(), result.getParentEmail(),
            "El email del padre debe coincidir");

        // Verificar valores por defecto y generados automáticamente
        assertEquals(Status.ACTIVE, result.getStatus(),
            "El status por defecto debe ser ACTIVE");
        assertNotNull(result.getCreatedAt(),
            "La fecha de creación debe ser establecida");
        assertNotNull(result.getUpdatedAt(),
            "La fecha de actualización debe ser establecida");
        assertTrue(result.getCreatedAt().isEqual(result.getUpdatedAt()) || 
                  result.getCreatedAt().isBefore(result.getUpdatedAt()),
            "Las fechas de creación y actualización deben ser lógicas");
    }

    /**
     * Prueba: Mapeo de Student a StudentResponse
     * 
     * Verifica que el método toResponse:
     * - Copie todos los campos del Student al Response
     * - Convierta enums a strings apropiadamente
     * - Mantenga la integridad de los datos
     * - No modifique la entidad original
     * 
     * Expectativa: StudentResponse con todos los datos de la entidad
     */
    @Test
    @DisplayName("toResponse() - Debería mapear Student a StudentResponse correctamente")
    void shouldMapEntityToResponse_WithAllFieldsCorrect() {
        // When - Ejecutar el mapeo
        StudentResponse result = StudentMapper.toResponse(existingStudent);

        // Then - Verificar mapeo completo
        assertEquals(existingStudent.getId(), result.getId(),
            "El ID debe coincidir");
        assertEquals(existingStudent.getInstitutionId(), result.getInstitutionId(),
            "El institutionId debe coincidir");
        assertEquals(existingStudent.getFirstName(), result.getFirstName(),
            "El nombre debe coincidir");
        assertEquals(existingStudent.getLastName(), result.getLastName(),
            "El apellido debe coincidir");
        assertEquals(existingStudent.getDocumentType(), result.getDocumentType(),
            "El tipo de documento debe coincidir");
        assertEquals(existingStudent.getDocumentNumber(), result.getDocumentNumber(),
            "El número de documento debe coincidir");
        assertEquals(existingStudent.getBirthDate(), result.getBirthDate(),
            "La fecha de nacimiento debe coincidir");
        assertEquals(existingStudent.getGender(), result.getGender(),
            "El género debe coincidir");
        assertEquals(existingStudent.getAddress(), result.getAddress(),
            "La dirección debe coincidir");
        assertEquals(existingStudent.getPhone(), result.getPhone(),
            "El teléfono debe coincidir");
        assertEquals(existingStudent.getParentName(), result.getParentName(),
            "El nombre del padre debe coincidir");
        assertEquals(existingStudent.getParentPhone(), result.getParentPhone(),
            "El teléfono del padre debe coincidir");
        assertEquals(existingStudent.getParentEmail(), result.getParentEmail(),
            "El email del padre debe coincidir");
        assertEquals(existingStudent.getStatus(), result.getStatus(),
            "El status debe coincidir");
        assertEquals(existingStudent.getCreatedAt(), result.getCreatedAt(),
            "La fecha de creación debe coincidir");
        assertEquals(existingStudent.getUpdatedAt(), result.getUpdatedAt(),
            "La fecha de actualización debe coincidir");

        // Verificar que la entidad original no fue modificada
        assertEquals("Ana", existingStudent.getFirstName(),
            "La entidad original no debe ser modificada");
    }

    /**
     * Prueba: Actualización parcial de Student
     * 
     * Verifica que el método updateEntity:
     * - Actualice solo los campos no nulos del UpdateRequest
     * - Mantenga los valores originales para campos nulos
     * - Actualice automáticamente el timestamp updatedAt
     * - No modifique campos como createdAt o ID
     * 
     * Expectativa: Estudiante actualizado parcialmente con valores correctos
     */
    @Test
    @DisplayName("updateEntity() - Debería actualizar solo campos no nulos")
    void shouldUpdateEntity_OnlyNonNullFields() {
        // Given - Capturar valores originales
        String originalLastName = existingStudent.getLastName();
        DocumentType originalDocType = existingStudent.getDocumentType();
        LocalDateTime originalCreatedAt = existingStudent.getCreatedAt();
        LocalDateTime originalUpdatedAt = existingStudent.getUpdatedAt();

        // When - Ejecutar actualización parcial
        Student result = StudentMapper.updateEntity(existingStudent, updateRequest);

        // Then - Verificar campos actualizados
        assertEquals(updateRequest.getFirstName(), result.getFirstName(),
            "El nombre debe ser actualizado");
        assertEquals(updateRequest.getAddress(), result.getAddress(),
            "La dirección debe ser actualizada");
        assertEquals(updateRequest.getPhone(), result.getPhone(),
            "El teléfono debe ser actualizado");

        // Verificar campos no modificados (fueron null en updateRequest)
        assertEquals(originalLastName, result.getLastName(),
            "El apellido no debe cambiar cuando el update es null");
        assertEquals(originalDocType, result.getDocumentType(),
            "El tipo de documento no debe cambiar cuando el update es null");

        // Verificar campos especiales
        assertEquals(originalCreatedAt, result.getCreatedAt(),
            "La fecha de creación nunca debe cambiar");
        assertTrue(result.getUpdatedAt().isAfter(originalUpdatedAt),
            "La fecha de actualización debe ser actualizada");

        // Verificar que es el mismo objeto (modificación in-place)
        assertSame(existingStudent, result,
            "Debe retornar la misma instancia modificada");
    }

    /**
     * Prueba: Actualización completa de Student
     * 
     * Verifica el comportamiento cuando se proporciona un UpdateRequest
     * con todos los campos poblados:
     * - Todos los campos deben ser actualizados
     * - El timestamp updatedAt debe ser actualizado
     * - Los campos inmutables deben mantenerse
     * 
     * Expectativa: Estudiante completamente actualizado
     */
    @Test
    @DisplayName("updateEntity() - Debería actualizar todos los campos cuando están presentes")
    void shouldUpdateAllFields_WhenAllFieldsProvided() {
        // Given - Crear request con todos los campos
        UpdateStudentRequest completeUpdateRequest = new UpdateStudentRequest();
        completeUpdateRequest.setFirstName("Nueva Ana");
        completeUpdateRequest.setLastName("Nuevo Martínez");
        completeUpdateRequest.setDocumentType(DocumentType.PASSPORT);
        completeUpdateRequest.setDocumentNumber("ABC123456");
        completeUpdateRequest.setBirthDate(LocalDate.of(2005, 1, 1));
        completeUpdateRequest.setGender(Gender.MALE);
        completeUpdateRequest.setAddress("Nueva Dirección 999");
        completeUpdateRequest.setPhone("999888777");
        completeUpdateRequest.setParentName("Nuevo Padre");
        completeUpdateRequest.setParentPhone("888777666");
        completeUpdateRequest.setParentEmail("nuevo@email.com");
        completeUpdateRequest.setStatus(Status.INACTIVE);

        LocalDateTime originalCreatedAt = existingStudent.getCreatedAt();
        String originalId = existingStudent.getId();

        // When - Ejecutar actualización completa
        Student result = StudentMapper.updateEntity(existingStudent, completeUpdateRequest);

        // Then - Verificar todos los campos actualizados
        assertEquals(completeUpdateRequest.getFirstName(), result.getFirstName());
        assertEquals(completeUpdateRequest.getLastName(), result.getLastName());
        assertEquals(completeUpdateRequest.getDocumentType(), result.getDocumentType());
        assertEquals(completeUpdateRequest.getDocumentNumber(), result.getDocumentNumber());
        assertEquals(completeUpdateRequest.getBirthDate(), result.getBirthDate());
        assertEquals(completeUpdateRequest.getGender(), result.getGender());
        assertEquals(completeUpdateRequest.getAddress(), result.getAddress());
        assertEquals(completeUpdateRequest.getPhone(), result.getPhone());
        assertEquals(completeUpdateRequest.getParentName(), result.getParentName());
        assertEquals(completeUpdateRequest.getParentPhone(), result.getParentPhone());
        assertEquals(completeUpdateRequest.getParentEmail(), result.getParentEmail());
        assertEquals(completeUpdateRequest.getStatus(), result.getStatus());

        // Verificar campos inmutables
        assertEquals(originalId, result.getId(),
            "El ID nunca debe cambiar");
        assertEquals(originalCreatedAt, result.getCreatedAt(),
            "La fecha de creación nunca debe cambiar");
        assertTrue(result.getUpdatedAt().isAfter(originalCreatedAt),
            "La fecha de actualización debe ser posterior a la creación");
    }

    /**
     * Prueba: Generación de IDs únicos
     * 
     * Verifica que el método toEntity genere IDs únicos:
     * - Cada llamada debe producir un ID diferente
     * - Los IDs no deben ser nulos ni vacíos
     * - Deben seguir un formato válido (UUID)
     * 
     * Expectativa: IDs únicos y válidos en múltiples creaciones
     */
    @Test
    @DisplayName("toEntity() - Debería generar IDs únicos para múltiples estudiantes")
    void shouldGenerateUniqueIds_ForMultipleStudents() {
        // When - Crear múltiples estudiantes
        Student student1 = StudentMapper.toEntity(createRequest, institutionId);
        Student student2 = StudentMapper.toEntity(createRequest, institutionId);
        Student student3 = StudentMapper.toEntity(createRequest, institutionId);

        // Then - Verificar unicidad de IDs
        assertNotEquals(student1.getId(), student2.getId(),
            "Los IDs deben ser únicos entre estudiantes");
        assertNotEquals(student2.getId(), student3.getId(),
            "Los IDs deben ser únicos entre estudiantes");
        assertNotEquals(student1.getId(), student3.getId(),
            "Los IDs deben ser únicos entre estudiantes");

        // Verificar formato válido (UUID)
        assertDoesNotThrow(() -> {
            java.util.UUID.fromString(student1.getId());
            java.util.UUID.fromString(student2.getId());
            java.util.UUID.fromString(student3.getId());
        }, "Los IDs deben ser UUIDs válidos");
    }

    /**
     * Prueba: Manejo de valores nulos en el mapeo
     * 
     * Verifica el comportamiento robusto ante valores nulos:
     * - toResponse debe manejar campos nulos sin fallar
     * - updateEntity debe ignorar campos nulos apropiadamente
     * - No debe haber NullPointerExceptions
     * 
     * Expectativa: Mapeo robusto sin errores por valores nulos
     */
    @Test
    @DisplayName("Mapeo debe manejar valores nulos correctamente")
    void shouldHandleNullValues_Gracefully() {
        // Given - Student con algunos campos nulos
        Student studentWithNulls = new Student();
        studentWithNulls.setId("test-id");
        studentWithNulls.setFirstName("Test");
        studentWithNulls.setLastName(null);  // Campo nulo
        studentWithNulls.setAddress(null);   // Campo nulo
        studentWithNulls.setStatus(Status.ACTIVE);

        // When & Then - toResponse no debe fallar con nulos
        assertDoesNotThrow(() -> {
            StudentResponse response = StudentMapper.toResponse(studentWithNulls);
            assertEquals("Test", response.getFirstName());
            assertNull(response.getLastName());
            assertNull(response.getAddress());
        }, "toResponse debe manejar campos nulos");

        // When & Then - updateEntity con request parcialmente nulo
        UpdateStudentRequest partialRequest = new UpdateStudentRequest();
        partialRequest.setFirstName("Updated Name");
        // otros campos son null

        assertDoesNotThrow(() -> {
            Student updated = StudentMapper.updateEntity(studentWithNulls, partialRequest);
            assertEquals("Updated Name", updated.getFirstName());
            assertNull(updated.getLastName()); // Debe mantenerse nulo
        }, "updateEntity debe manejar requests con campos nulos");
    }
}