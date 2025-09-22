-- Script para llenar la base de datos FlowFX con datos de prueba
-- Generado el 22 de septiembre de 2025
-- Inserta al menos 5 registros en cada tabla con todos los campos

-- =============================================================================
-- INSERTAR PERSONAS (PERSON)
-- =============================================================================

-- Insertar 10 personas para tener variedad de roles (usando cédulas costarricenses)
INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (101234567, 'Juan', 'Pérez', 'juan.perez@flowfx.com', 'jperez', 'password123', 'A', 'Y');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (202345678, 'María', 'González', 'maria.gonzalez@flowfx.com', 'mgonzalez', 'secure456', 'A', 'N');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (301456789, 'Carlos', 'Rodríguez', 'carlos.rodriguez@flowfx.com', 'crodriguez', 'mypass789', 'A', 'N');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (104567890, 'Ana', 'Martínez', 'ana.martinez@flowfx.com', 'amartinez', 'password321', 'A', 'N');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (205678901, 'Luis', 'López', 'luis.lopez@flowfx.com', 'llopez', 'secure654', 'A', 'Y');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (306789012, 'Carmen', 'Sánchez', 'carmen.sanchez@flowfx.com', 'csanchez', 'mypass987', 'A', 'N');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (107890123, 'Diego', 'Ramírez', 'diego.ramirez@flowfx.com', 'dramirez', 'password147', 'I', 'N');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (208901234, 'Laura', 'Torres', 'laura.torres@flowfx.com', 'ltorres', 'secure258', 'A', 'N');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (309012345, 'Roberto', 'Morales', 'roberto.morales@flowfx.com', 'rmorales', 'mypass369', 'A', 'N');

INSERT INTO person (per_id, per_first_name, per_last_name, email, username, password, status, is_admin) 
VALUES (110123456, 'Patricia', 'Vargas', 'patricia.vargas@flowfx.com', 'pvargas', 'password741', 'A', 'Y');

-- =============================================================================
-- INSERTAR PROYECTOS (PROJECT)
-- =============================================================================

INSERT INTO project (project_id, project_name, leader_user_id, tech_leader_id, sponsor_id, 
                    planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                    status, created_at, updated_at) 
VALUES (seq_project_id.NEXTVAL, 'Sistema de Gestión de Inventarios', 202345678, 301456789, 101234567, 
        DATE '2025-01-15', DATE '2025-06-30', DATE '2025-01-20', NULL, 
        'P', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO project (project_id, project_name, leader_user_id, tech_leader_id, sponsor_id, 
                    planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                    status, created_at, updated_at) 
VALUES (seq_project_id.NEXTVAL, 'Plataforma E-commerce', 104567890, 306789012, 205678901, 
        DATE '2025-03-01', DATE '2025-12-31', DATE '2025-03-05', NULL, 
        'R', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO project (project_id, project_name, leader_user_id, tech_leader_id, sponsor_id, 
                    planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                    status, created_at, updated_at) 
VALUES (seq_project_id.NEXTVAL, 'App Mobile de Seguimiento', 208901234, 309012345, 110123456, 
        DATE '2024-10-01', DATE '2025-03-15', DATE '2024-10-05', DATE '2025-03-10', 
        'C', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO project (project_id, project_name, leader_user_id, tech_leader_id, sponsor_id, 
                    planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                    status, created_at, updated_at) 
VALUES (seq_project_id.NEXTVAL, 'Sistema de Recursos Humanos', 301456789, 202345678, 101234567, 
        DATE '2025-05-01', DATE '2025-11-30', NULL, NULL, 
        'S', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO project (project_id, project_name, leader_user_id, tech_leader_id, sponsor_id, 
                    planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                    status, created_at, updated_at) 
VALUES (seq_project_id.NEXTVAL, 'Portal de Clientes', 306789012, 104567890, 205678901, 
        DATE '2025-02-15', DATE '2025-08-31', DATE '2025-02-20', NULL, 
        'P', SYSTIMESTAMP, SYSTIMESTAMP);

INSERT INTO project (project_id, project_name, leader_user_id, tech_leader_id, sponsor_id, 
                    planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                    status, created_at, updated_at) 
VALUES (seq_project_id.NEXTVAL, 'Sistema de Reportes Ejecutivos', 309012345, 208901234, 110123456, 
        DATE '2025-04-01', DATE '2025-09-30', NULL, NULL, 
        'S', SYSTIMESTAMP, SYSTIMESTAMP);

-- =============================================================================
-- INSERTAR ACTIVIDADES DE PROYECTO (PROJECT_ACTIVITY)
-- =============================================================================

-- Actividades para el proyecto 1 (Sistema de Gestión de Inventarios)
INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 1, 'Análisis de requerimientos del sistema de inventarios', 301456789, 'C', 
        DATE '2025-01-20', DATE '2025-02-10', DATE '2025-01-20', DATE '2025-02-08', 
        1, 202345678, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 1, 'Diseño de la base de datos', 301456789, 'C', 
        DATE '2025-02-11', DATE '2025-02-25', DATE '2025-02-11', DATE '2025-02-23', 
        2, 202345678, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 1, 'Desarrollo del módulo de entrada de productos', 306789012, 'P', 
        DATE '2025-02-26', DATE '2025-03-31', DATE '2025-02-26', NULL, 
        3, 202345678, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 1, 'Desarrollo del módulo de salida de productos', 208901234, 'R', 
        DATE '2025-04-01', DATE '2025-04-30', NULL, NULL, 
        4, 202345678, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 1, 'Pruebas integrales del sistema', 309012345, 'R', 
        DATE '2025-05-01', DATE '2025-05-31', NULL, NULL, 
        5, 202345678, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Actividades para el proyecto 2 (Plataforma E-commerce)
INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 2, 'Investigación de mercado y competencia', 104567890, 'C', 
        DATE '2025-03-05', DATE '2025-03-20', DATE '2025-03-05', DATE '2025-03-18', 
        1, 104567890, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 2, 'Diseño de la arquitectura del sistema', 306789012, 'P', 
        DATE '2025-03-21', DATE '2025-04-15', DATE '2025-03-21', NULL, 
        2, 104567890, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 2, 'Desarrollo del catálogo de productos', 202345678, 'R', 
        DATE '2025-04-16', DATE '2025-06-30', NULL, NULL, 
        3, 104567890, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Actividades para el proyecto 3 (App Mobile - Completado)
INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 3, 'Prototipado de interfaces móviles', 309012345, 'C', 
        DATE '2024-10-05', DATE '2024-11-15', DATE '2024-10-05', DATE '2024-11-12', 
        1, 208901234, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 3, 'Desarrollo para iOS y Android', 208901234, 'C', 
        DATE '2024-11-16', DATE '2025-02-28', DATE '2024-11-16', DATE '2025-02-25', 
        2, 208901234, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 3, 'Publicación en tiendas de aplicaciones', 110123456, 'C', 
        DATE '2025-03-01', DATE '2025-03-15', DATE '2025-03-01', DATE '2025-03-10', 
        3, 208901234, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Actividades adicionales para alcanzar los 5 registros mínimos por tabla
INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 4, 'Análisis de procesos de RRHH actuales', 202345678, 'D', 
        DATE '2025-05-01', DATE '2025-05-31', NULL, NULL, 
        1, 301456789, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO project_activity (activity_id, project_id, description, responsible_id, status, 
                             planned_start_date, planned_end_date, actual_start_date, actual_end_date, 
                             execution_order, created_by, created_at, updated_at) 
VALUES (seq_activity_id.NEXTVAL, 5, 'Configuración del entorno de desarrollo', 104567890, 'D', 
        DATE '2025-02-20', DATE '2025-03-05', NULL, NULL, 
        1, 306789012, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =============================================================================
-- INSERTAR SEGUIMIENTOS DE PROYECTO (PROJECT_TRACKING)
-- =============================================================================

-- Seguimientos para proyecto 1
INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 1, 'Proyecto iniciado correctamente. Se completó el análisis de requerimientos dentro del plazo establecido. El equipo está motivado y comprometido.', 
        DATE '2025-02-15', 25.50, 101234567, SYSTIMESTAMP);

INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 1, 'Diseño de base de datos finalizado. Se identificaron algunos retos técnicos que podrían impactar el cronograma. Reunión con stakeholders programada.', 
        DATE '2025-03-01', 45.75, 202345678, SYSTIMESTAMP);

INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 1, 'Desarrollo del módulo de entrada en progreso. El equipo de desarrollo está trabajando eficientemente. Sin bloqueos reportados hasta el momento.', 
        DATE '2025-03-15', 62.25, 101234567, SYSTIMESTAMP);

-- Seguimientos para proyecto 2
INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 2, 'Investigación de mercado completada exitosamente. Se identificaron oportunidades clave y estrategias de diferenciación. Próximo paso: arquitectura.', 
        DATE '2025-03-25', 18.30, 205678901, SYSTIMESTAMP);

INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 2, 'Diseño de arquitectura en progreso. Se están evaluando diferentes tecnologías para optimizar el rendimiento y escalabilidad de la plataforma.', 
        DATE '2025-04-10', 35.80, 104567890, SYSTIMESTAMP);

-- Seguimientos para proyecto 3 (completado)
INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 3, 'Prototipado completado satisfactoriamente. Los usuarios piloto proporcionaron feedback muy positivo sobre la interfaz y funcionalidad.', 
        DATE '2024-12-01', 45.00, 110123456, SYSTIMESTAMP);

INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 3, 'Desarrollo para ambas plataformas móviles finalizado. Las pruebas beta están dando resultados excelentes. Preparando documentación para publicación.', 
        DATE '2025-03-05', 95.50, 208901234, SYSTIMESTAMP);

INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 3, 'Proyecto completado exitosamente. La aplicación fue publicada en ambas tiendas y está recibiendo descargas positivas. Cliente muy satisfecho.', 
        DATE '2025-03-12', 100.00, 110123456, SYSTIMESTAMP);

-- Seguimientos adicionales para proyecto 4
INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 4, 'Proyecto en etapa de planificación. Se están definiendo los alcances y recursos necesarios. Sponsor aprobó el presupuesto inicial.', 
        DATE '2025-04-15', 5.00, 101234567, SYSTIMESTAMP);

-- Seguimientos adicionales para proyecto 5
INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 5, 'Configuración del ambiente de desarrollo iniciada. Se está trabajando en la definición de los requerimientos funcionales del portal.', 
        DATE '2025-02-28', 12.75, 205678901, SYSTIMESTAMP);

INSERT INTO project_tracking (tracking_id, project_id, observations, tracking_date, progress_percentage, created_by, created_at) 
VALUES (seq_tracking_id.NEXTVAL, 5, 'Avances significativos en el análisis de requerimientos. El equipo está coordinando con el área de UX para definir la experiencia del usuario.', 
        DATE '2025-03-20', 28.40, 306789012, SYSTIMESTAMP);

-- =============================================================================
-- COMMIT DE TODAS LAS TRANSACCIONES
-- =============================================================================

COMMIT;

-- =============================================================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- =============================================================================

-- Contar registros por tabla
SELECT 'PERSON' as tabla, COUNT(*) as total_registros FROM person
UNION ALL
SELECT 'PROJECT' as tabla, COUNT(*) as total_registros FROM project
UNION ALL
SELECT 'PROJECT_ACTIVITY' as tabla, COUNT(*) as total_registros FROM project_activity
UNION ALL
SELECT 'PROJECT_TRACKING' as tabla, COUNT(*) as total_registros FROM project_tracking;

-- Mostrar algunos registros de ejemplo
SELECT 'Personas registradas:' as info FROM dual;
SELECT per_id, per_first_name, per_last_name, email, status, is_admin FROM person ORDER BY per_id;

SELECT 'Proyectos registrados:' as info FROM dual;
SELECT project_id, project_name, status, 
       planned_start_date, planned_end_date, 
       actual_start_date, actual_end_date 
FROM project ORDER BY project_id;

SELECT 'Total de actividades por proyecto:' as info FROM dual;
SELECT p.project_name, COUNT(pa.activity_id) as total_actividades,
       SUM(CASE WHEN pa.status = 'C' THEN 1 ELSE 0 END) as completadas,
       SUM(CASE WHEN pa.status = 'P' THEN 1 ELSE 0 END) as en_progreso
FROM project p 
LEFT JOIN project_activity pa ON p.project_id = pa.project_id 
GROUP BY p.project_id, p.project_name 
ORDER BY p.project_id;

SELECT 'Último seguimiento por proyecto:' as info FROM dual;
SELECT p.project_name, 
       pt.tracking_date, 
       pt.progress_percentage,
       SUBSTR(pt.observations, 1, 50) || '...' as observaciones_resumen
FROM project p 
LEFT JOIN project_tracking pt ON p.project_id = pt.project_id 
WHERE pt.tracking_id IN (
    SELECT MAX(tracking_id) 
    FROM project_tracking pt2 
    WHERE pt2.project_id = pt.project_id
) 
ORDER BY p.project_id;