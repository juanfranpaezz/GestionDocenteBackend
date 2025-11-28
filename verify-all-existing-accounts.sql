-- Script SQL para marcar todas las cuentas existentes como verificadas
-- Ejecutar este script en la base de datos MySQL

USE GestionDocenteDB;

-- Marcar todos los profesores como verificados
-- Usamos id > 0 para satisfacer el modo safe update de MySQL
UPDATE professors 
SET email_verified = true,
    verification_token = NULL,
    token_expiry_date = NULL
WHERE id > 0 AND (email_verified IS NULL OR email_verified = false);

-- Verificar el resultado
SELECT id, name, lastname, email, email_verified 
FROM professors;

