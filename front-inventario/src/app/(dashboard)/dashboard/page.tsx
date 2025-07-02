"use client";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Package, TrendingUp, AlertTriangle, DollarSign } from "lucide-react";

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>
        <p className="text-muted-foreground">
          Resumen general del inventario y métricas principales
        </p>
      </div>

      {/* Tarjetas de métricas principales */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Total Productos
            </CardTitle>
            <Package className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">1,234</div>
            <p className="text-xs text-muted-foreground">
              +20.1% desde el mes pasado
            </p>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Valor Total
            </CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">$45,231.89</div>
            <p className="text-xs text-muted-foreground">
              +12.5% desde el mes pasado
            </p>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Stock Bajo
            </CardTitle>
            <AlertTriangle className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">12</div>
            <p className="text-xs text-muted-foreground">
              Productos requieren reposición
            </p>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">
              Categorías
            </CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">8</div>
            <p className="text-xs text-muted-foreground">
              Categorías activas
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Sección de alertas y actividad reciente */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
        <Card className="col-span-4">
          <CardHeader>
            <CardTitle>Productos con Stock Bajo</CardTitle>
            <CardDescription>
              Productos que requieren reposición inmediata
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {[
                { nombre: "Laptop Dell XPS 13", stock: 2, categoria: "Electrónicos" },
                { nombre: "Mouse Logitech MX Master", stock: 5, categoria: "Accesorios" },
                { nombre: "Teclado Mecánico RGB", stock: 3, categoria: "Accesorios" },
                { nombre: "Monitor 4K 27 pulgadas", stock: 1, categoria: "Monitores" },
              ].map((producto, index) => (
                <div key={index} className="flex items-center space-x-4">
                  <div className="flex-1 space-y-1">
                    <p className="text-sm font-medium leading-none">
                      {producto.nombre}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      {producto.categoria}
                    </p>
                  </div>
                  <Badge variant={producto.stock <= 2 ? "destructive" : "secondary"}>
                    {producto.stock} unidades
                  </Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
        
        <Card className="col-span-3">
          <CardHeader>
            <CardTitle>Actividad Reciente</CardTitle>
            <CardDescription>
              Últimas actualizaciones del inventario
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {[
                { accion: "Producto agregado", item: "iPhone 15 Pro", tiempo: "Hace 2 horas" },
                { accion: "Stock actualizado", item: "Samsung Galaxy S24", tiempo: "Hace 4 horas" },
                { accion: "Producto eliminado", item: "iPhone 12", tiempo: "Hace 1 día" },
                { accion: "Categoría creada", item: "Smartphones Premium", tiempo: "Hace 2 días" },
              ].map((actividad, index) => (
                <div key={index} className="flex items-center space-x-4">
                  <div className="w-2 h-2 bg-blue-500 rounded-full" />
                  <div className="flex-1 space-y-1">
                    <p className="text-sm font-medium leading-none">
                      {actividad.accion}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      {actividad.item}
                    </p>
                  </div>
                  <p className="text-xs text-muted-foreground">
                    {actividad.tiempo}
                  </p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
} 