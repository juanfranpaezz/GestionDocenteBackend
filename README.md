# Gestión Docente - Frontend

Aplicación web desarrollada en Angular para la gestión académica de profesores. Permite administrar cursos, estudiantes, evaluaciones, notas y asistencias.

## Sobre el Programa

Gestión Docente es un sistema web diseñado para que los profesores puedan administrar sus cursos de manera centralizada. El programa permite crear cursos, agregar estudiantes, definir evaluaciones con diferentes tipos y pesos, cargar calificaciones y registrar asistencias.

El sistema calcula automáticamente los promedios de los estudiantes basándose en los pesos asignados a cada tipo de evaluación. También permite definir notas de aprobación y promoción por curso, y personalizar estos valores por evaluación. Las calificaciones se muestran con colores según su estado: rojo para desaprobado, verde para aprobado y azul para promocionado.

Los profesores pueden enviar emails personalizados a sus estudiantes o notificaciones automáticas cuando se cargan las calificaciones de una evaluación. El sistema también permite exportar las planillas de notas y asistencias a Excel con nombres de archivo descriptivos.

La aplicación incluye autenticación con roles, donde los profesores pueden gestionar sus propios cursos y los administradores tienen acceso adicional para gestionar otros profesores. La interfaz es responsive y cuenta con modo claro y oscuro.

## Requisitos

- Node.js 18 o superior
- npm 9 o superior
- Backend ejecutándose en http://localhost:8080

## Instalación

```bash
npm install
```

## Ejecución

```bash
npm start
```

La aplicación estará disponible en http://localhost:4200

## Configuración

La URL del backend está configurada en `src/app/core/config/api.config.ts`. Si el backend corre en otro puerto, actualiza la configuración allí.

## Estructura del Proyecto

```
src/
├── app/
│   ├── components/     # Componentes reutilizables
│   ├── core/          # Servicios, guards, interceptors, modelos
│   ├── pages/         # Páginas principales
│   └── app.routes.ts  # Configuración de rutas
├── assets/            # Recursos estáticos
└── main.ts           # Punto de entrada
```

## Funcionalidades

- Autenticación con JWT
- Gestión de cursos (crear, editar, eliminar, listar)
- Gestión de estudiantes por curso
- Sistema de evaluaciones con tipos y pesos
- Planilla de notas con promedios automáticos
- Control de asistencias con porcentajes
- Exportación a Excel de notas y asistencias
- Envío de emails personalizados y de calificaciones
- Modo claro/oscuro
- Diseño responsive

## Tecnologías

- Angular 20
- TypeScript
- Angular Material
- RxJS
- ExcelJS

## Build

```bash
npm run build
```

El build se genera en la carpeta `dist/`.
