-- Migración V2: Agregar nuevas funcionalidades
-- Fecha: 2024-12-01

-- Agregar campos a evaluations
ALTER TABLE evaluations 
ADD COLUMN grades_sent_by_email BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN custom_message VARCHAR(1000) NULL;

-- Agregar campos a courses
ALTER TABLE courses 
ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN archived_date DATETIME NULL;

-- Agregar campo a grades (opcional, nullable)
ALTER TABLE grades 
ADD COLUMN grade_value VARCHAR(50) NULL;

-- Crear tabla course_schedules
CREATE TABLE course_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id)
);

-- Crear tabla grade_scales
CREATE TABLE grade_scales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    professor_id BIGINT NULL,
    is_global BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (professor_id) REFERENCES professors(id) ON DELETE CASCADE,
    INDEX idx_professor_id (professor_id)
);

-- Crear tabla grade_scale_options
CREATE TABLE grade_scale_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    grade_scale_id BIGINT NOT NULL,
    label VARCHAR(50) NOT NULL,
    numeric_value DOUBLE NULL,
    order_index INT NOT NULL,
    FOREIGN KEY (grade_scale_id) REFERENCES grade_scales(id) ON DELETE CASCADE,
    INDEX idx_grade_scale_id (grade_scale_id)
);

-- Crear tabla email_templates
CREATE TABLE email_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    professor_id BIGINT NULL,
    is_global BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (professor_id) REFERENCES professors(id) ON DELETE CASCADE,
    INDEX idx_professor_id (professor_id)
);

-- Agregar relación evaluation-grade_scale
ALTER TABLE evaluations 
ADD COLUMN grade_scale_id BIGINT NULL,
ADD FOREIGN KEY (grade_scale_id) REFERENCES grade_scales(id) ON DELETE SET NULL,
ADD INDEX idx_grade_scale_id (grade_scale_id);

