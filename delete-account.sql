-- Script SQL para eliminar una cuenta de profesor
-- CUIDADO: Esto eliminará permanentemente la cuenta y todos sus datos relacionados

USE GestionDocenteDB;

-- Primero, verificar que existe la cuenta
SELECT id, name, lastname, email, role 
FROM professors 
WHERE email = 'juanfranciscopaezz@gmail.com';

-- Eliminar la cuenta (MySQL manejará las relaciones de foreign key según la configuración)
DELETE FROM professors 
WHERE email = 'juanfranciscopaezz@gmail.com';

-- Verificar que se eliminó
SELECT COUNT(*) as total_professors 
FROM professors 
WHERE email = 'juanfranciscopaezz@gmail.com';

