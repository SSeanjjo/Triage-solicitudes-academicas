# 03 — Cómo instalar y ejecutar el proyecto

## Requisitos previos

Antes de correr el proyecto necesitas tener instalado:

1. **Node.js** (versión 20 o superior)
   - Descárgalo en: https://nodejs.org
   - Elige la versión **LTS** (Long Term Support)
   - La instalación de Node.js incluye `npm` automáticamente

2. **El backend corriendo**
   - El backend Java/Spring Boot debe estar activo en `http://localhost:8080`
   - Sin el backend, la app carga pero no puede obtener ni guardar datos

Para verificar que Node.js está instalado correctamente, abre una terminal y escribe:
```bash
node --version   # Debe mostrar algo como: v20.x.x
npm --version    # Debe mostrar algo como: 11.x.x
```

---

## Pasos para ejecutar por primera vez

### Paso 1: Abrir una terminal en la carpeta del frontend

Navega hasta la carpeta `frontend/` del proyecto. Puedes hacerlo de dos maneras:
- Abre la terminal directamente en esa carpeta
- O escribe en la terminal:
```bash
cd "ruta/hasta/gestion-solicitudes-academicas/frontend"
```

### Paso 2: Instalar las dependencias

Este comando descarga todas las librerías que el proyecto necesita (Angular, Material, etc.) y las coloca en la carpeta `node_modules/`:

```bash
npm install
```

> **Nota:** Este paso puede tardar varios minutos la primera vez. La carpeta `node_modules/` puede pesar varios cientos de MB — es normal. Solo necesitas ejecutar este comando una vez (o cuando cambien las dependencias en `package.json`).

### Paso 3: Iniciar el servidor de desarrollo

```bash
npm start
```

Este comando:
1. Compila el código TypeScript a JavaScript
2. Levanta un servidor local en `http://localhost:4200`
3. Activa el proxy hacia el backend en `localhost:8080`
4. Observa cambios en los archivos y recarga automáticamente (Hot Reload)

Cuando veas este mensaje en la terminal, la aplicación está lista:
```
✔ Browser application bundle generation complete.
Application bundle generation complete. [X.XXX seconds]
Watch mode enabled. Watching for file changes...
  ➜ Local:   http://localhost:4200/
```

Abre tu navegador en `http://localhost:4200`.

---

## Comandos disponibles

| Comando | Descripción |
|---|---|
| `npm start` | Servidor de desarrollo con proxy al backend |
| `npm run build` | Compila la app para producción (genera la carpeta `dist/`) |
| `npm test` | Ejecuta las pruebas unitarias con Vitest |
| `npm run watch` | Compila en modo observación (sin servidor) |

---

## Solución de problemas comunes

### "Cannot find module" o "node_modules not found"
Ejecuta `npm install` nuevamente.

### El puerto 4200 ya está en uso
Otro proceso está usando ese puerto. Puedes especificar otro puerto:
```bash
ng serve --port 4201
```

### No se muestran datos (tablas vacías, errores en consola)
El backend no está corriendo. Verifica que el servidor Java esté activo en `localhost:8080`.

### Errores de CORS en la consola del navegador
Verifica que el archivo `proxy.conf.json` existe y que el comando `npm start` lo está usando. El archivo `angular.json` debe tener configurado `"proxyConfig": "proxy.conf.json"` en la sección `serve`.

### Cambios en el código no se reflejan
El Hot Reload debería funcionar automáticamente. Si no, detén el servidor (Ctrl+C) y vuelve a ejecutar `npm start`.

---

## ¿Cómo funciona el Hot Reload?

Cuando el servidor de desarrollo está corriendo con `npm start`, Angular observa todos los archivos `.ts`, `.html` y `.scss`. Si modificas cualquier archivo y guardas, Angular:
1. Recompila solo los archivos afectados
2. Recarga la página automáticamente en el navegador

Esto permite ver los cambios casi instantáneamente sin necesidad de detener y reiniciar el servidor.

---

## Compilar para producción

Para crear una versión optimizada lista para desplegar en un servidor real:

```bash
npm run build
```

Esto genera la carpeta `dist/frontend/browser/` con archivos estáticos (HTML, JS, CSS) que puedes subir a cualquier servidor web (Nginx, Apache, etc.).

Las diferencias respecto al modo desarrollo:
- El código está **minificado** (comprimido, ilegible para humanos)
- Los archivos tienen **hashes** en el nombre para evitar caché obsoleto
- Las optimizaciones de Angular están activadas (tree-shaking, etc.)
