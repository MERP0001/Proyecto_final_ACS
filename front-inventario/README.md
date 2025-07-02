# Sistema de Gestión de Inventarios - Frontend

Este es el frontend del Sistema de Gestión de Inventarios desarrollado con Next.js 15, TypeScript, Tailwind CSS y shadcn/ui.

## 🚀 Características

- **Interfaz moderna y responsive** con componentes de shadcn/ui
- **Autenticación JWT** con Spring Boot backend
- **Gestión completa de productos** (CRUD)
- **Dashboard con métricas** en tiempo real
- **Filtros y búsqueda avanzada** de productos
- **Formularios con validación** usando react-hook-form y zod
- **Diseño accesible** siguiendo las mejores prácticas de UX

## 🛠️ Tecnologías

- **Framework**: Next.js 15 con App Router
- **Lenguaje**: TypeScript
- **Estilos**: Tailwind CSS
- **Componentes**: shadcn/ui
- **Formularios**: react-hook-form + zod
- **HTTP Client**: Axios
- **Iconos**: Lucide React

## 📋 Prerequisitos

- Node.js 18 o superior
- npm o yarn
- Backend Spring Boot ejecutándose en puerto 8080

## 🚀 Instalación y Configuración

1. **Clonar el repositorio e ir al directorio frontend**:
   ```bash
   cd frontend
   ```

2. **Instalar dependencias**:
   ```bash
   npm install
   ```

3. **Configurar variables de entorno**:
   - El frontend está configurado para conectarse al backend en `http://localhost:8080/api`
   - Si necesitas cambiar la URL, modifica `NEXT_PUBLIC_API_URL` en `next.config.js`

4. **Ejecutar en modo desarrollo**:
   ```bash
   npm run dev
   ```

5. **Acceder a la aplicación**:
   - Abre tu navegador en `http://localhost:3000`

## 🔐 Credenciales de Prueba

Para acceder al sistema, usa las siguientes credenciales:

- **Usuario**: `admin`
- **Contraseña**: `admin123`

## 📱 Páginas y Funcionalidades

### 1. Login (`/login`)
- Formulario de autenticación con validación
- Manejo de errores de inicio de sesión
- Redirección automática tras login exitoso

### 2. Dashboard (`/dashboard`)
- Métricas principales del inventario
- Gráficos y estadísticas
- Productos con stock bajo
- Actividad reciente

### 3. Gestión de Productos (`/productos`)
- Lista paginada de productos
- Filtros por nombre y categoría
- Búsqueda en tiempo real
- Acciones CRUD por producto

### 4. Nuevo Producto (`/productos/nuevo`)
- Formulario completo con validación
- Categorías predefinidas
- Campos requeridos y opcionales
- Navegación de regreso

## 🎨 Componentes Principales

### Layout
- **AppSidebar**: Navegación lateral colapsible
- **NavMain**: Menú de navegación principal
- **NavUser**: Perfil de usuario con dropdown

### Formularios
- **ProductoForm**: Formulario de creación/edición de productos
- **LoginForm**: Formulario de autenticación

### Tablas
- **ProductosTable**: Tabla con filtros y acciones
- **Dashboard Cards**: Tarjetas de métricas

## 🔧 Scripts Disponibles

```bash
# Ejecutar en desarrollo
npm run dev

# Compilar para producción
npm run build

# Iniciar servidor de producción
npm start

# Ejecutar linter
npm run lint
```

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── app/                    # Páginas (App Router)
│   │   ├── (dashboard)/        # Grupo de rutas del dashboard
│   │   │   ├── dashboard/      # Página principal del dashboard
│   │   │   ├── productos/      # Gestión de productos
│   │   │   └── layout.tsx      # Layout del dashboard
│   │   ├── login/              # Página de login
│   │   ├── globals.css         # Estilos globales
│   │   ├── layout.tsx          # Layout raíz
│   │   └── page.tsx            # Página de inicio
│   ├── components/             # Componentes reutilizables
│   │   ├── layout/             # Componentes de layout
│   │   └── ui/                 # Componentes de shadcn/ui
│   ├── lib/                    # Utilidades y configuraciones
│   ├── services/               # Servicios de API
│   └── types/                  # Definiciones de tipos TypeScript
├── public/                     # Archivos estáticos
└── next.config.js             # Configuración de Next.js
```

## 🔗 API Integration

El frontend se conecta con el backend Spring Boot a través de:

- **Base URL**: `http://localhost:8080/api`
- **Autenticación**: JWT Bearer token
- **Endpoints**:
  - `POST /auth/login` - Autenticación
  - `GET /productos` - Lista de productos
  - `POST /productos` - Crear producto
  - `PUT /productos/{id}` - Actualizar producto
  - `DELETE /productos/{id}` - Eliminar producto

## 🎯 Próximas Funcionalidades

- [ ] Edición inline de productos
- [ ] Exportación de datos a CSV/Excel
- [ ] Gráficos interactivos con Chart.js
- [ ] Notificaciones push
- [ ] Modo oscuro
- [ ] Internacionalización (i18n)

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está desarrollado como parte del curso de Aseguramiento de Calidad de Software de PUCMM.

## 👥 Autores

- Proyecto desarrollado para PUCMM
- Sistema de Gestión de Inventarios con QAS
