"use client";

import { useState } from "react";
import { productosService } from "@/services/productos";
import { ProductoDTO } from "@/types";
import { RoleGuard } from "@/components/auth/role-guard";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { AlertTriangle, Search } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";

export default function StockBajoPage() {
    const [minimo, setMinimo] = useState(10);
    const [productos, setProductos] = useState<ProductoDTO[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [hasSearched, setHasSearched] = useState(false);

    const handleSearch = async () => {
        setIsLoading(true);
        setError(null);
        setHasSearched(true);
        try {
            const data = await productosService.getProductosStockBajo(minimo);
            setProductos(data);
        } catch (err) {
            console.error("Error al buscar productos con stock bajo:", err);
            setError("No se pudo cargar el reporte. Inténtalo de nuevo.");
        } finally {
            setIsLoading(false);
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.valueAsNumber;
        if (!isNaN(value)) {
            setMinimo(value);
        }
    }

    return (
        <RoleGuard allowedRoles={["ADMINISTRADOR"]}>
            <div className="space-y-6">
                <div>
                    <h2 className="text-3xl font-bold tracking-tight">Reporte de Stock Bajo</h2>
                    <p className="text-muted-foreground">
                        Encuentra productos que necesitan ser reabastecidos.
                    </p>
                </div>

                <Card>
                    <CardHeader>
                        <CardTitle>Filtro de Búsqueda</CardTitle>
                        <CardDescription>
                            Define el umbral para considerar un producto con stock bajo.
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="flex items-center gap-4">
                            <Input
                                type="number"
                                value={minimo}
                                onChange={handleInputChange}
                                placeholder="Ej: 10"
                                className="max-w-xs"
                                min="0"
                            />
                            <Button onClick={handleSearch} disabled={isLoading}>
                                {isLoading ? (
                                    <>
                                        <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-background border-t-transparent" />
                                        Buscando...
                                    </>
                                ) : (
                                    <>
                                        <Search className="mr-2 h-4 w-4" />
                                        Buscar
                                    </>
                                )}
                            </Button>
                        </div>
                    </CardContent>
                </Card>

                {error && <p className="text-destructive">{error}</p>}

                {hasSearched && !isLoading && (
                    <Card>
                        <CardHeader>
                            <CardTitle>Resultados</CardTitle>
                            <CardDescription>
                                Se encontraron {productos.length} productos con stock igual o inferior a {minimo}.
                            </CardDescription>
                        </CardHeader>
                        <CardContent>
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Producto</TableHead>
                                        <TableHead>Categoría</TableHead>
                                        <TableHead className="text-right">Stock Actual</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {productos.length > 0 ? (
                                        productos.map((producto) => (
                                            <TableRow key={producto.id}>
                                                <TableCell className="font-medium">{producto.nombre}</TableCell>
                                                <TableCell>{producto.categoria}</TableCell>
                                                <TableCell className="text-right">
                                                    <Badge variant={producto.cantidadActual <= minimo / 2 ? "destructive" : "secondary"}>
                                                        {producto.cantidadActual} {producto.unidadMedida}
                                                    </Badge>
                                                </TableCell>
                                            </TableRow>
                                        ))
                                    ) : (
                                        <TableRow>
                                            <TableCell colSpan={3} className="text-center h-24">
                                                No se encontraron productos con stock bajo.
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </CardContent>
                    </Card>
                )}

                {isLoading && (
                     <Card>
                        <CardHeader>
                            <Skeleton className="h-6 w-1/3" />
                            <Skeleton className="h-4 w-2/3 mt-2" />
                        </CardHeader>
                        <CardContent>
                           <div className="space-y-4">
                               <Skeleton className="h-10 w-full" />
                               <Skeleton className="h-10 w-full" />
                               <Skeleton className="h-10 w-full" />
                           </div>
                        </CardContent>
                    </Card>
                )}
            </div>
        </RoleGuard>
    );
} 