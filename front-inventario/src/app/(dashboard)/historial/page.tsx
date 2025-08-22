"use client";
import { useGetHistorial } from "@/services/historial";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

export default function HistorialPage() {
  const { data, isLoading } = useGetHistorial();

  if (isLoading) return <div>Cargando historial...</div>;

  const historial = data?.content || [];

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

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Historial de Movimientos</h2>
        <p className="text-muted-foreground">
          Registro de todas las entradas y salidas de productos
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Movimientos de Inventario</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Producto</TableHead>
                <TableHead>Usuario</TableHead>
                <TableHead>Tipo de Movimiento</TableHead>
                <TableHead>Cantidad</TableHead>
                <TableHead>Fecha</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {historial.map((movimiento: any) => (
                <TableRow key={movimiento.id}>
                  <TableCell>
                    <div>
                      <div className="font-medium">{movimiento.producto.nombre}</div>
                      <div className="text-sm text-muted-foreground">
                        {movimiento.producto.descripcion}
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>{movimiento.usuario.nombreCompleto}</TableCell>
                  <TableCell>
                    <Badge variant={movimiento.tipoMovimiento === "ENTRADA" ? "default" : "destructive"}>
                      {movimiento.tipoMovimiento}
                    </Badge>
                  </TableCell>
                  <TableCell>{movimiento.cantidad}</TableCell>
                  <TableCell>{formatDate(movimiento.fecha)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
} 