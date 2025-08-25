"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { productosService } from "@/services/productos";
import { ProductoForm } from "@/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
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
import { ArrowLeft, Save } from "lucide-react";
import { RoleGuard } from "@/components/auth/role-guard";
import { CategoriaSelect } from "@/components/categorias/categoria-select";

const unidadesMedida = [
  "unidad",
  "kg",
  "litro",
  "metro",
  "par",
  "caja",
  "paquete"
];

export default function NuevoProductoPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

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

  const onSubmit = async (data: ProductoForm) => {
    setIsLoading(true);
    setError(null);
    try {
      await productosService.createProducto(data);
      router.push("/productos");
      router.refresh();
    } catch (error) {
      console.error("Error al crear producto:", error);
      setError("Error al crear el producto. Por favor, intente nuevamente.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <RoleGuard allowedRoles={["ADMINISTRADOR","USER"]}>
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
            <h2 className="text-3xl font-bold tracking-tight">Nuevo Producto</h2>
            <p className="text-muted-foreground">
              Agrega un nuevo producto al inventario
            </p>
          </div>
        </div>

        <div className="max-w-2xl">
          <Card>
            <CardHeader>
              <CardTitle>Información del Producto</CardTitle>
              <CardDescription>
                Completa todos los campos requeridos para agregar un nuevo producto
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                  <FormField
                    control={form.control}
                    name="nombre"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Nombre del Producto</FormLabel>
                        <FormControl>
                          <Input placeholder="Ej: Laptop Dell XPS 13" {...field} />
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
                            placeholder="Describe las características principales del producto..."
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
                          <FormControl>
                            <CategoriaSelect
                              value={field.value}
                              onValueChange={field.onChange}
                              placeholder="Selecciona una categoría"
                            />
                          </FormControl>
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
                              placeholder="0.00"
                              onChange={(e) => {
                                const value = e.target.valueAsNumber;
                                field.onChange(isNaN(value) ? 0 : value);
                              }}
                              value={isNaN(field.value) ? "" : field.value.toString()}
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
                              placeholder="0"
                              onChange={(e) => {
                                const value = e.target.valueAsNumber;
                                field.onChange(isNaN(value) ? 0 : value);
                              }}
                              value={isNaN(field.value) ? "" : field.value.toString()}
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
                                  {unidad}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                          <FormDescription>
                            Unidad de medida del producto
                          </FormDescription>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <div className="flex gap-4">
                    <Button
                      type="submit"
                      disabled={isLoading}
                    >
                      {isLoading ? (
                        <span>Guardando...</span>
                      ) : (
                        <>
                          <Save className="w-4 h-4 mr-2" />
                          <span>Guardar Producto</span>
                        </>
                      )}
                    </Button>
                  </div>
                </form>
              </Form>
            </CardContent>
          </Card>
        </div>
      </div>
    </RoleGuard>
  );
} 