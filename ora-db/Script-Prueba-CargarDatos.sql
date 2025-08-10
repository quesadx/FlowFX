-- Script de prueba para FlowFx
-- 1. Primero insertamos personas (necesarias para las relaciones)
INSERT INTO person (per_id, per_first_name, per_last_name, email, status, is_admin)
VALUES (108123456, 'Juan', 'Pérez', 'juan.perez@test.com', 'A', 'Y');

INSERT INTO person (per_id, per_first_name, per_last_name, email, status, is_admin)
VALUES (108789012, 'María', 'González', 'maria.gonzalez@test.com', 'A', 'N');

-- Verificar la inserción de personas
SELECT per_id, per_first_name, per_last_name, email, status, is_admin 
FROM person;

-- 2. Insertamos un proyecto (usa trigger para ID)
INSERT INTO project (
    project_name, leader_user_id, tech_leader_id, sponsor_id,
    planned_start_date, planned_end_date, status
)
VALUES (
    'Proyecto Piloto', 
    108123456,  -- Juan como líder
    108789012,  -- María como líder técnico
    108123456,  -- Juan como sponsor
    SYSDATE,    -- Fecha inicio
    SYSDATE + 30, -- Fecha fin (30 días después)
    'P'         -- Estado Pendiente
);

-- Verificar la inserción del proyecto y el trigger de ID
SELECT project_id, project_name, status, 
       planned_start_date, planned_end_date
FROM project;

-- 3. Insertamos una actividad del proyecto
INSERT INTO project_activity (
    project_id, description, responsible_id,
    planned_start_date, planned_end_date,
    status, execution_order, created_by
)
SELECT 
    project_id,
    'Actividad inicial del proyecto',
    108789012,  -- María como responsable
    SYSDATE,
    SYSDATE + 7,
    'P',
    1,
    108123456   -- Juan como creador
FROM project
WHERE project_name = 'Proyecto Piloto';

-- Verificar la inserción de la actividad y el trigger de ID
SELECT activity_id, description, status, 
       planned_start_date, planned_end_date
FROM project_activity;

-- 4. Insertamos un seguimiento
INSERT INTO project_tracking (
    project_id, observations,
    tracking_date, progress_percentage, created_by
)
SELECT 
    project_id,
    'Inicio del proyecto, estableciendo planes preliminares',
    SYSDATE,
    10.5,
    108123456   -- Juan como creador
FROM project
WHERE project_name = 'Proyecto Piloto';

-- Verificar la inserción del seguimiento y el trigger de ID
SELECT tracking_id, observations, tracking_date, progress_percentage
FROM project_tracking;

-- 5. Insertamos una notificación
INSERT INTO notification (
    project_id, activity_id, subject, message,
    status, event_type
)
SELECT 
    p.project_id,
    a.activity_id,
    'Inicio de Proyecto',
    'Se ha iniciado el proyecto piloto',
    'P',
    'P'
FROM project p
JOIN project_activity a ON p.project_id = a.project_id
WHERE p.project_name = 'Proyecto Piloto';

-- Verificar la inserción de la notificación y el trigger de ID
SELECT notification_id, subject, status, event_type
FROM notification;

-- 6. Insertamos un destinatario de la notificación
INSERT INTO notification_recipient (
    notification_id, email, name, role
)
SELECT 
    n.notification_id,
    'destinatario@test.com',
    'Destinatario Prueba',
    'U'
FROM notification n
JOIN project p ON n.project_id = p.project_id
WHERE p.project_name = 'Proyecto Piloto';

-- Verificar la inserción del destinatario
SELECT nr.notification_id, nr.email, nr.name, nr.role
FROM notification_recipient nr;

-- 7. Verificar las secuencias
SELECT sequence_name, last_number, increment_by 
FROM user_sequences 
WHERE sequence_name IN (
    'SEQ_NOTIFICATION_ID',
    'SEQ_PROJECT_ID',
    'SEQ_ACTIVITY_ID',
    'SEQ_TRACKING_ID'
);

-- 8. Verificar los triggers
SELECT trigger_name, status, triggering_event
FROM user_triggers
WHERE table_name IN (
    'NOTIFICATION',
    'PROJECT',
    'PROJECT_ACTIVITY',
    'PROJECT_TRACKING'
);

-- 9. Verificar las relaciones (conteo de registros relacionados)
SELECT 
    'Proyectos' as tipo, COUNT(*) as cantidad
FROM project
UNION ALL
SELECT 
    'Actividades', COUNT(*)
FROM project_activity
UNION ALL
SELECT 
    'Seguimientos', COUNT(*)
FROM project_tracking
UNION ALL
SELECT 
    'Notificaciones', COUNT(*)
FROM notification
UNION ALL
SELECT 
    'Destinatarios', COUNT(*)
FROM notification_recipient;