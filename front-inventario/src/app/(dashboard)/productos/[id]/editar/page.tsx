"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { productosService } from "@/services/productos";
import { ProductoForm, ProductoDTO } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ArrowLeft, Save, Loader2 } from "lucide-react";
import { use } from "react";

const categorias = [
  "Electrónicos",
  "Accesorios",
  "Monitores",
  "Smartphones",
  "Laptops",
  "Componentes PC",
  "Audio",
  "Gaming",
];

const unidadesMedida = [
  "unidad",
  "kg",
  "litro",
  "metro",
  "par",
  "caja",
  "paquete"
];

export default function EditarProductoPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [producto, setProducto] = useState<ProductoDTO | null>(null);

  const id = parseInt(params.id);

  const form = useForm<ProductoForm>({
    defaultValues: {
      nombre: "",
      descripcion: "",
      categoria: "",
      precio: 0,
      cantidadInicial: 0,
      unidadMedida: "unidad",
    },
  });

  useEffect(() => {
    const fetchProducto = async () => {
      try {
        const data = await productosService.getProducto(id);
        setProducto(data);
        form.reset({
          nombre: data.nombre,
          descripcion: data.descripcion || "",
          categoria: data.categoria,
          precio: data.precio,
          cantidadInicial: data.cantidadInicial,
          unidadMedida: data.unidadMedida,
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

  const onSubmit = async (data: ProductoForm) => {
    setIsSaving(true);
    setError(null);
    try {
      await productosService.updateProducto(id, data);
      router.push("/productos");
      router.refresh();
    } catch (error) {
      console.error("Error al actualizar producto:", error);
      setError("Error al actualizar el producto. Por favor, intente nuevamente.");
    } finally {
      setIsSaving(false);
    }
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
          <h2 className="text-3xl font-bold tracking-tight">Editar Producto</h2>
          <p className="text-muted-foreground">
            Modifica la información del producto
          </p>
        </div>
      </div>

      <div className="max-w-2xl">
        <Card>
          <CardHeader>
            <CardTitle>Información del Producto</CardTitle>
            <CardDescription>
              Actualiza los campos que deseas modificar
            </CardDescription>
          </CardHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
              <CardContent className="space-y-6">
                <FormField
                  control={form.control}
                  name="nombre"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Nombre del Producto</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormDescription>
                        Nombre descriptivo y único del producto
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="descripcion"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Descripción</FormLabel>
                      <FormControl>
                        <Textarea
                          className="resize-none"
                          {...field}
                        />
                      </FormControl>
                      <FormDescription>
                        Descripción detallada del producto y sus características
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <div className="grid grid-cols-2 gap-4">
                  <FormField
                    control={form.control}
                    name="categoria"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Categoría</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Selecciona una categoría" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {categorias.map((categoria) => (
                              <SelectItem key={categoria} value={categoria}>
                                {categoria}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="precio"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Precio</FormLabel>
                        <FormControl>
                          <Input
                            type="number"
                            min="0.01"
                            step="0.01"
                            onChange={(e) => field.onChange(parseFloat(e.target.value))}
                            value={field.value}
                          />
                        </FormControl>
                        <FormDescription>
                          Precio unitario del producto
                        </FormDescription>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <FormField
                    control={form.control}
                    name="cantidadInicial"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Cantidad Inicial</FormLabel>
                        <FormControl>
                          <Input
                            type="number"
                            min="0"
                            step="1"
                            onChange={(e) => field.onChange(parseInt(e.target.value))}
                            value={field.value}
                          />
                        </FormControl>
                        <FormDescription>
                          Cantidad inicial en inventario
                        </FormDescription>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="unidadMedida"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Unidad de Medida</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Selecciona una unidad" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {unidadesMedida.map((unidad) => (
                              <SelectItem key={unidad} value={unidad}>
                                {unidad.charAt(0).toUpperCase() + unidad.slice(1)}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
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
                      Guardar Cambios
                    </>
                  )}
                </Button>
              </CardFooter>
            </form>
          </Form>
        </Card>
      </div>
    </div>
  );
} 