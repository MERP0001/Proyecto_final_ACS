"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { productosService } from "@/services/productos";
import { ProductoDTO } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
  CardFooter,
} from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { ArrowLeft, Save, Loader2 } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { use } from "react";

interface StockUpdateForm {
  cantidad: number;
}

export default function ActualizarStockPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const [producto, setProducto] = useState<ProductoDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showConfirmDialog, setShowConfirmDialog] = useState(false);
  const [formData, setFormData] = useState<StockUpdateForm | null>(null);

  const id = parseInt(params.id);

  const form = useForm<StockUpdateForm>({
    defaultValues: {
      cantidad: 0,
    },
  });

  useEffect(() => {
    const fetchProducto = async () => {
      try {
        const data = await productosService.getProducto(id);
        setProducto(data);
        form.reset({
          cantidad: 0,
        });
      } catch (err) {
        console.error("Error al cargar el producto:", err);
        setError("No se pudo cargar la información del producto.");
      } finally {
        setIsLoading(false);
      }
    };

    if (id) {
      fetchProducto();
    }
  }, [id, form]);

  const onSubmit = (data: StockUpdateForm) => {
    setFormData(data);
    setShowConfirmDialog(true);
  };

  const handleConfirmUpdate = async () => {
    if (!formData) return;
    
    setIsSaving(true);
    try {
      await productosService.updateStock(id, formData.cantidad);
      router.push("/productos");
      router.refresh();
    } catch (err) {
      console.error("Error al actualizar el stock:", err);
      setError("Error al actualizar el stock. Por favor, intente nuevamente.");
    } finally {
      setIsSaving(false);
      setShowConfirmDialog(false);
    }
  };

  const getStockBadge = (cantidad: number) => {
    if (cantidad <= 5) return <Badge variant="destructive">Stock Bajo</Badge>;
    if (cantidad <= 15) return <Badge variant="secondary">Stock Medio</Badge>;
    return <Badge variant="default">Stock Alto</Badge>;
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
      </div>
    );
  }

  if (!producto) {
    return (
      <div className="flex flex-col items-center justify-center h-full">
        <h2 className="text-2xl font-bold mb-2">Producto no encontrado</h2>
        <Button onClick={() => router.back()}>Volver</Button>
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
      <div className="flex items-center gap-4">
        <Button
          variant="outline"
          size="icon"
          onClick={() => router.back()}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <div>
          <h2 className="text-3xl font-bold tracking-tight">Actualizar Stock</h2>
          <p className="text-muted-foreground">
            Actualiza la cantidad en inventario del producto
          </p>
        </div>
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Información del Producto</CardTitle>
            <CardDescription>
              Detalles actuales del producto seleccionado
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <h3 className="font-medium">Nombre</h3>
              <p className="text-lg">{producto.nombre}</p>
            </div>
            <div>
              <h3 className="font-medium">Categoría</h3>
              <Badge variant="outline">{producto.categoria}</Badge>
            </div>
            <div>
              <h3 className="font-medium">Stock Actual</h3>
              <div className="flex items-center gap-2 mt-1">
                <p className="text-2xl font-bold">{producto.cantidadInicial} {producto.unidadMedida}</p>
                {getStockBadge(producto.cantidadInicial)}
              </div>
            </div>
            <div>
              <h3 className="font-medium">Precio</h3>
              <p className="text-lg">${producto.precio.toFixed(2)}</p>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Actualizar Inventario</CardTitle>
            <CardDescription>
              Indica la cantidad que deseas agregar o restar del inventario
            </CardDescription>
          </CardHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <CardContent>
                <FormField
                  control={form.control}
                  name="cantidad"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Cantidad a modificar</FormLabel>
                      <FormControl>
                        <Input
                          type="number"
                          placeholder="0"
                          onChange={(e) => field.onChange(parseInt(e.target.value))}
                          value={field.value}
                        />
                      </FormControl>
                      <FormDescription>
                        Usa valores positivos para agregar al inventario y negativos para restar
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </CardContent>
              <CardFooter className="flex justify-end">
                <Button type="submit" disabled={isSaving}>
                  {isSaving ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Guardando...
                    </>
                  ) : (
                    <>
                      <Save className="mr-2 h-4 w-4" />
                      Actualizar Stock
                    </>
                  )}
                </Button>
              </CardFooter>
            </form>
          </Form>
        </Card>
      </div>

      <AlertDialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar actualización de stock</AlertDialogTitle>
            <AlertDialogDescription>
              {formData && formData.cantidad > 0
                ? `¿Estás seguro de que deseas agregar ${formData.cantidad} ${producto.unidadMedida}(s) al inventario?`
                : formData && formData.cantidad < 0
                ? `¿Estás seguro de que deseas restar ${Math.abs(formData.cantidad)} ${producto.unidadMedida}(s) del inventario?`
                : "No se realizará ningún cambio en el inventario."}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={handleConfirmUpdate}>
              {isSaving ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Guardando...
                </>
              ) : (
                "Confirmar"
              )}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
} 