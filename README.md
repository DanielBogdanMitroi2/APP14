# APP14 - Firebase CRUD Estudiantes

Aplicación Android en Java conectada a Firebase Realtime Database para operaciones CRUD de estudiantes.

## Requisitos previos
1. Android Studio instalado
2. Proyecto Firebase creado (`APP14_2025-26`)
3. Realtime Database habilitada
4. Archivo `google-services.json` descargado

## Instalación
1. Clonar el repositorio
2. Copiar `google-services.json` dentro de `app/`
3. Sincronizar Gradle
4. Ejecutar la app en emulador o dispositivo

## Funcionalidades
- ✅ Agregar estudiantes
- ✅ Leer estudiantes en tiempo real
- ✅ Actualizar estudiantes
- ✅ Eliminar estudiantes
- ✅ Limpiar formulario

## Estructura del estudiante
- `id` (String)
- `nombre` (String)
- `email` (String)
- `edad` (int)

## Ruta Firebase
La app trabaja sobre la ruta:

```json
{
  "estudiantes": {
    "estudiante1": {
      "nombre": "Juan",
      "email": "juan@email.com",
      "edad": 20
    }
  }
}
```
