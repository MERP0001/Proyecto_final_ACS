"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Plus, Search, MoreHorizontal, Edit, Trash2, Package, Clock, TrendingUp, TrendingDown } from "lucide-react";
import { productosService } from "@/services/productos";
import { ProductoDTO, PaginatedResponse, ProductoFilters, MovementHistory } from "@/types";
import { PaginationControls } from "@/components/ui/pagination-controls";
import { useGetHistorial } from "@/services/historial";

export default function ProductosPage() {
  const router = useRouter();
  const [paginatedData, setPaginatedData] = useState<PaginatedResponse<ProductoDTO>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 10,
    number: 0,
    first: true,
    last: false,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtroNombre, setFiltroNombre] = useState("");
  const [filtroNombreDebounced, setFiltroNombreDebounced] = useState("");
  const [filtroCategoria, setFiltroCategoria] = useState("");
  
  // Estados de paginación
  const [currentPage, setCurrentPage] = useState(1); // UI usa 1-based, API usa 0-based
  const [pageSize, setPageSize] = useState(10);

  // Estado para categorías disponibles
  const [categorias, setCategorias] = useState<string[]>([]);

  // Hook para obtener actividad reciente (últimos 5 movimientos)
  const { data: historialData, isLoading: isHistorialLoading } = useGetHistorial({ 
    page: 0, 
    size: 5, 
    sort: "fecha,desc" 
  });

  // Debouncing para el filtro de nombre (500ms de retraso)
  useEffect(() => {
    const timer = setTimeout(() => {
      setFiltroNombreDebounced(filtroNombre);
    }, 500);

    return () => clearTimeout(timer);
  }, [filtroNombre]);

  // Cargar categorías al inicializar
  useEffect(() => {
    const cargarCategorias = async () => {
      try {
        const categoriasData = await productosService.getCategorias();
        setCategorias(categoriasData);
      } catch (err) {
        console.error("Error al cargar categorías:", err);
      }
    };
    
    cargarCategorias();
  }, []);

  // Función para cargar productos
  const cargarProductos = async (page: number, size: number, filters?: ProductoFilters) => {
    setIsLoading(true);
    try {
      let response: PaginatedResponse<ProductoDTO>;
      
      if (filters && (filters.nombre || filters.categoria)) {
        // Usar searchProductos si hay filtros activos
        response = await productosService.searchProductos(filters, page - 1, size);
      } else {
        // Usar getProductos para carga normal
        response = await productosService.getProductos(page - 1, size);
      }
      
      setPaginatedData(response);
      setError(null);
    } catch (err) {
      console.error("Error al cargar productos:", err);
      setError("Error al cargar los productos. Por favor, intente nuevamente.");
    } finally {
      setIsLoading(false);
    }
  };

  // Efecto para cargar productos cuando cambian los parámetros
  // Ahora usa filtroNombreDebounced en lugar de filtroNombre
  useEffect(() => {
    const filters: ProductoFilters = {};
    
    if (filtroNombreDebounced.trim()) {
      filters.nombre = filtroNombreDebounced.trim();
    }
    if (filtroCategoria) {
      filters.categoria = filtroCategoria;
    }
    
    cargarProductos(currentPage, pageSize, filters);
  }, [currentPage, pageSize, filtroNombreDebounced, filtroCategoria]);

  // Handlers para paginación
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(1); // Reset a la primera página cuando cambia el tamaño
  };

  // Handlers para filtros
  const handleFiltroNombreChange = (value: string) => {
    setFiltroNombre(value);
    setCurrentPage(1); // Reset a la primera página cuando cambia el filtro
  };

  const handleFiltroCategoriaChange = (value: string) => {
    setFiltroCategoria(value);
    setCurrentPage(1); // Reset a la primera página cuando cambia el filtro
  };

  const getStockBadge = (cantidad: number) => {
    if (cantidad < 10) return <Badge variant="destructive">Stock Bajo</Badge>;
    if (cantidad <= 20) return <Badge variant="secondary">Stock Medio</Badge>;
    return <Badge variant="default">Stock Alto</Badge>;
  };

  const handleEliminarProducto = async (producto: ProductoDTO) => {
    if (confirm("¿Está seguro de que desea eliminar este producto?")) {
      try {
        await productosService.deleteProducto(producto.id!);
        // Recargar la página actual después de eliminar
        const filters: ProductoFilters = {};
        if (filtroNombreDebounced.trim()) filters.nombre = filtroNombreDebounced.trim();
        if (filtroCategoria) filters.categoria = filtroCategoria;
        
        await cargarProductos(currentPage, pageSize, filters);
      } catch (err) {
        console.error("Error al eliminar producto:", err);
        setError("Error al eliminar el producto");
      }
    }
  };

  // Función para formatear fechas
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // Componente de actividad reciente
  const ActividadReciente = () => {
    const movimientos = historialData?.content || [];
    
    if (isHistorialLoading) {
      return (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-5 w-5" />
              Actividad Reciente
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-center h-20">
              <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-gray-900"></div>
            </div>
          </CardContent>
        </Card>
      );
    }

    if (movimientos.length === 0) {
      return (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-5 w-5" />
              Actividad Reciente
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-muted-foreground text-center py-4">
              No hay movimientos recientes
            </p>
          </CardContent>
        </Card>
      );
    }

    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock className="h-5 w-5" />
            Actividad Reciente
          </CardTitle>
          <CardDescription>
            Últimos movimientos de inventario
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {movimientos.map((movimiento: MovementHistory) => (
              <div key={movimiento.id} className="flex items-center justify-between p-3 rounded-lg border">
                <div className="flex items-center gap-3">
                  <div className={`p-2 rounded-full ${
                    movimiento.tipoMovimiento === 'ENTRADA' 
                      ? 'bg-green-100 text-green-600' 
                      : 'bg-red-100 text-red-600'
                  }`}>
                    {movimiento.tipoMovimiento === 'ENTRADA' ? 
                      <TrendingUp className="h-4 w-4" /> : 
                      <TrendingDown className="h-4 w-4" />
                    }
                  </div>
                  <div>
                    <div className="font-medium text-sm">{movimiento.producto.nombre}</div>
                    <div className="text-xs text-muted-foreground">
                      {movimiento.tipoMovimiento} • {movimiento.cantidad} unidades
                    </div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-xs text-muted-foreground">
                    {formatDate(movimiento.fecha)}
                  </div>
                  <div className="text-xs text-muted-foreground">
                    por {movimiento.usuario.nombreCompleto}
                  </div>
                </div>
              </div>
            ))}
          </div>
          <div className="mt-4 text-center">
            <Button 
              variant="outline" 
              size="sm" 
              onClick={() => router.push('/historial')}
            >
              Ver todo el historial
            </Button>
          </div>
        </CardContent>
      </Card>
    );
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {error && (
        <div className="bg-destructive/15 text-destructive px-4 py-3 rounded-md">
          {error}
        </div>
      )}
      
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Productos</h2>
          <p className="text-muted-foreground">
            Gestiona el inventario de productos
          </p>
        </div>
        <Button onClick={() => router.push("/productos/nuevo")}>
          <Plus className="mr-2 h-4 w-4" />
          Agregar Producto
        </Button>
      </div>

      {/* Actividad Reciente */}
      <ActividadReciente />

      {/* Filtros y búsqueda */}
      <Card>
        <CardHeader>
          <CardTitle>Filtros</CardTitle>
          <CardDescription>
            Busca y filtra productos por nombre y categoría
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Buscar por nombre..."
                  value={filtroNombre}
                  onChange={(e) => handleFiltroNombreChange(e.target.value)}
                  className="pl-8"
                />
              </div>
            </div>
            <div className="w-48">
              <select
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                value={filtroCategoria}
                onChange={(e) => handleFiltroCategoriaChange(e.target.value)}
                aria-label="Filtrar por categoría"
              >
                <option value="">Todas las categorías</option>
                {categorias.map((categoria) => (
                  <option key={categoria} value={categoria}>
                    {categoria}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Tabla de productos */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Package className="h-5 w-5" />
            Lista de Productos ({paginatedData.totalElements})
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Producto</TableHead>
                <TableHead>Categoría</TableHead>
                <TableHead>Precio</TableHead>
                <TableHead>Stock</TableHead>
                <TableHead>Unidad</TableHead>
                <TableHead>Estado</TableHead>
                <TableHead>Fecha Creación</TableHead>
                <TableHead className="text-right">Acciones</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {paginatedData.content.map((producto) => (
                <TableRow key={producto.id}>
                  <TableCell>
                    <div>
                      <div className="font-medium">{producto.nombre}</div>
                      <div className="text-sm text-muted-foreground">
                        {producto.descripcion}
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{producto.categoria}</Badge>
                  </TableCell>
                  <TableCell>${producto.precio.toFixed(2)}</TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      <span>{producto.cantidadActual} {producto.unidadMedida}</span>
                      {getStockBadge(producto.cantidadActual)}
                    </div>
                  </TableCell>
                  <TableCell>{producto.unidadMedida}</TableCell>
                  <TableCell>
                    <Badge variant={producto.activo ? "default" : "secondary"}>
                      {producto.activo ? "Activo" : "Inactivo"}
                    </Badge>
                  </TableCell>
                  <TableCell>{new Date(producto.fechaCreacion!).toLocaleDateString()}</TableCell>
                  <TableCell className="text-right">
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="h-8 w-8 p-0">
                          <span className="sr-only">Abrir menú</span>
                          <MoreHorizontal className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuLabel>Acciones</DropdownMenuLabel>
                        <DropdownMenuItem
                          onClick={() => navigator.clipboard.writeText(producto.id!.toString())}
                        >
                          Copiar ID del producto
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem onClick={() => router.push(`/productos/${producto.id}/editar`)}>
                          <Edit className="mr-2 h-4 w-4" />
                          Editar
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => router.push(`/productos/${producto.id}/stock`)}>
                          <Package className="mr-2 h-4 w-4" />
                          Actualizar Stock
                        </DropdownMenuItem>
                        <DropdownMenuItem 
                          className="text-destructive"
                          onClick={() => handleEliminarProducto(producto)}
                        >
                          <Trash2 className="mr-2 h-4 w-4" />
                          Eliminar
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          
          {/* Controles de paginación */}
          <div className="mt-4">
            <PaginationControls
              currentPage={currentPage}
              totalPages={paginatedData.totalPages}
              totalElements={paginatedData.totalElements}
              pageSize={pageSize}
              onPageChange={handlePageChange}
              onPageSizeChange={handlePageSizeChange}
            />
          </div>
        </CardContent>
      </Card>
    </div>
  );
} 