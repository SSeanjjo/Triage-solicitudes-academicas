# 01 — Introducción: Node.js, Angular y el proyecto

## ¿Qué es Node.js?

**Node.js** es un entorno de ejecución de JavaScript que funciona fuera del navegador. Normalmente JavaScript solo corre dentro de Chrome o Firefox, pero Node.js lo permite ejecutar en tu computador directamente.

En este proyecto **no usas Node.js para programar la aplicación**, sino como herramienta de trabajo:
- Para instalar paquetes (librerías de código) con el comando `npm`
- Para compilar y empaquetar el código Angular
- Para levantar el servidor de desarrollo

Piénsalo como la "maquinaria de fábrica" que construye tu aplicación, aunque no forma parte del producto final.

---

## ¿Qué es npm?

**npm** (Node Package Manager) es el gestor de paquetes de Node.js. Funciona como una tienda de código:
- Cada librería que usa el proyecto (Angular, Angular Material, etc.) se descarga con `npm install`
- Los paquetes instalados van a la carpeta `node_modules/`
- Las dependencias del proyecto están listadas en `package.json`

---

## ¿Qué es Angular?

**Angular** es un framework (marco de trabajo) para construir aplicaciones web. Está hecho por Google y usa **TypeScript** como lenguaje principal.

Un framework es como un conjunto de reglas y herramientas que te dicen cómo organizar tu código. En lugar de escribir HTML, CSS y JavaScript en archivos sueltos, Angular te da:

- **Componentes:** Bloques reutilizables de UI (cada pantalla, cada botón, cada tarjeta)
- **Servicios:** Clases que manejan la lógica de negocio y la comunicación con el backend
- **Rutas:** Un sistema para navegar entre pantallas sin recargar la página
- **Formularios reactivos:** Una manera estructurada de manejar formularios con validación
- **CLI (Interfaz de línea de comandos):** Comandos para crear archivos, compilar y servir la app

---

## ¿Qué es TypeScript?

**TypeScript** es JavaScript con tipos. Un "tipo" es simplemente decirle al código qué clase de dato esperas:

```typescript
// JavaScript normal — no sabes qué tipo es 'nombre'
let nombre = "Sebastian";

// TypeScript — explícitamente dices que es texto (string)
let nombre: string = "Sebastian";
```

Esto ayuda a detectar errores antes de ejecutar el código. TypeScript se "compila" (traduce) a JavaScript normal para que el navegador lo pueda entender.

---

## ¿Qué es Angular Material?

**Angular Material** es una colección de componentes de interfaz visual listos para usar, basados en las guías de diseño de Google (Material Design). Incluye botones, tarjetas, tablas, formularios, menús, íconos, etc.

En este proyecto todos los elementos visuales (botones azules, tarjetas con sombra, tablas con paginación, chips de colores) vienen de Angular Material.

---

## ¿Cómo encaja todo?

```
Tu código (TypeScript + HTML + SCSS)
        ↓
   Angular compila
        ↓
 JavaScript + HTML + CSS (que entiende el navegador)
        ↓
  Navegador muestra la aplicación
        ↓
  El usuario interactúa → se hacen llamadas HTTP al backend (Java/Spring Boot en :8080)
```

---

## ¿Qué hace esta aplicación?

El **Sistema de Triage Académico** permite gestionar solicitudes académicas (registros, homologaciones, cancelaciones, consultas) con tres tipos de usuarios:

| Rol | Puede hacer |
|---|---|
| **ESTUDIANTE** | Crear solicitudes, ver sus solicitudes |
| **RESPONSABLE** | Clasificar, asignar y atender solicitudes |
| **ADMINISTRADOR** | Todo lo anterior + gestionar usuarios |

El sistema incluye **inteligencia artificial** para sugerir el tipo y prioridad de una solicitud basándose en su descripción.
