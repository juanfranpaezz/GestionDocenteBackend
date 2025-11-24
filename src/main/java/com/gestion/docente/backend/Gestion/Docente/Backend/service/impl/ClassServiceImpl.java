package com.gestion.docente.backend.Gestion.Docente.Backend.service.impl;

import com.gestion.docente.backend.Gestion.Docente.Backend.dto.ClassDTO;
import com.gestion.docente.backend.Gestion.Docente.Backend.model.Class;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ClassRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.repository.ProfessorRepository;
import com.gestion.docente.backend.Gestion.Docente.Backend.security.SecurityUtils;
import com.gestion.docente.backend.Gestion.Docente.Backend.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassServiceImpl implements ClassService {
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Override
    public List<ClassDTO> getAllClasses() {
        // Obtener el profesor autenticado y filtrar solo sus clases
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        List<Class> classes = classRepository.findByProfessorId(currentProfessorId);
        
        return classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ClassDTO getClassById(Long id) {
        // 1. Obtener el profesor autenticado
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Buscar clase por ID
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La clase con ID " + id + " no existe"));
        
        // 3. Validar ownership: la clase debe pertenecer al profesor autenticado
        if (!classEntity.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No tiene acceso a esta clase");
        }
        
        // 4. Convertir a DTO y retornar
        return convertToDTO(classEntity);
    }
    
    @Override
    public ClassDTO createClass(ClassDTO classDTO) {
        // 1. Obtener el profesor autenticado desde el JWT
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Si se envía professorId en el DTO, validar que coincida con el del JWT
        if (classDTO.getProfessorId() != null && !currentProfessorId.equals(classDTO.getProfessorId())) {
            throw new IllegalArgumentException("No puede crear clases para otros profesores");
        }
        
        // 3. Validar que el profesor exista
        if (!professorRepository.existsById(currentProfessorId)) {
            throw new IllegalArgumentException("El profesor autenticado no existe en la base de datos");
        }
        
        // 4. Asignar el professorId del JWT al DTO
        classDTO.setProfessorId(currentProfessorId);
        
        // 5. Crear nueva entidad Class
        Class classEntity = convertToEntity(classDTO);
        
        // 6. Guardar en la base de datos
        Class savedClass = classRepository.save(classEntity);
        
        // 7. Convertir a DTO y retornar
        return convertToDTO(savedClass);
    }
    
    @Override
    public ClassDTO updateClass(Long id, ClassDTO classDTO) {
        // 1. Obtener el profesor autenticado
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Buscar la clase existente
        Class existingClass = classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La clase con ID " + id + " no existe"));
        
        // 3. Validar ownership: la clase debe pertenecer al profesor autenticado
        if (!existingClass.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede actualizar clases de otros profesores");
        }
        
        // 4. Validar que el professorId del DTO coincida con el del JWT (si se envía)
        if (classDTO.getProfessorId() != null && !currentProfessorId.equals(classDTO.getProfessorId())) {
            throw new IllegalArgumentException("No puede cambiar el profesor de la clase");
        }
        
        // 5. Actualizar los campos
        existingClass.setName(classDTO.getName());
        existingClass.setDescription(classDTO.getDescription());
        // El professorId no se cambia, se mantiene el original
        
        // 6. Guardar los cambios
        Class updatedClass = classRepository.save(existingClass);
        
        // 7. Convertir a DTO y retornar
        return convertToDTO(updatedClass);
    }
    
    @Override
    public void deleteClass(Long id) {
        // 1. Obtener el profesor autenticado
        Long currentProfessorId = SecurityUtils.getCurrentProfessorId();
        
        // 2. Buscar la clase existente
        Class existingClass = classRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La clase con ID " + id + " no existe"));
        
        // 3. Validar ownership: la clase debe pertenecer al profesor autenticado
        if (!existingClass.getProfessorId().equals(currentProfessorId)) {
            throw new IllegalArgumentException("No puede eliminar clases de otros profesores");
        }
        
        // 4. Eliminar la clase
        classRepository.deleteById(id);
    }
    
    /**
     * Convierte una entidad Class a ClassDTO
     */
    private ClassDTO convertToDTO(Class classEntity) {
        ClassDTO dto = new ClassDTO();
        dto.setId(classEntity.getId());
        dto.setName(classEntity.getName());
        dto.setDescription(classEntity.getDescription());
        dto.setProfessorId(classEntity.getProfessorId());
        return dto;
    }
    
    /**
     * Convierte un ClassDTO a entidad Class
     */
    private Class convertToEntity(ClassDTO dto) {
        Class classEntity = new Class();
        classEntity.setId(dto.getId());
        classEntity.setName(dto.getName());
        classEntity.setDescription(dto.getDescription());
        classEntity.setProfessorId(dto.getProfessorId());
        return classEntity;
    }
}

