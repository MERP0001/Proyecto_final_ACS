"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { User } from "@/types";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const formSchema = z.object({
  username: z.string().min(3, "El nombre de usuario debe tener al menos 3 caracteres"),
  email: z.string().email("Email no válido"),
  nombreCompleto: z.string().min(3, "El nombre completo es requerido"),
  role: z.enum(["ADMINISTRADOR", "USUARIO"]),
  activo: z.boolean(),
});

type UserFormValues = z.infer<typeof formSchema>;

interface UserFormProps {
  initialData?: User;
  onSubmit: (values: UserFormValues) => void;
  isSubmitting: boolean;
}

export function UserForm({ initialData, onSubmit, isSubmitting }: UserFormProps) {
  const form = useForm<UserFormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: initialData ? {
      ...initialData,
      role: initialData.role || "USUARIO",
    } : {
      username: "",
      email: "",
      nombreCompleto: "",
      role: "USUARIO",
      activo: true,
    },
  });

  return (
    <Card>
      <CardHeader>
        <CardTitle>{initialData ? "Editar Usuario" : "Crear Usuario"}</CardTitle>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nombre de Usuario</FormLabel>
                  <FormControl>
                    <Input placeholder="john.doe" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <Input type="email" placeholder="john.doe@example.com" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="nombreCompleto"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nombre Completo</FormLabel>
                  <FormControl>
                    <Input placeholder="John Doe" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="role"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Rol</FormLabel>
                  <Select onValueChange={field.onChange} defaultValue={field.value}>
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecciona un rol" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value="ADMINISTRADOR">Administrador</SelectItem>
                      <SelectItem value="USUARIO">Usuario</SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="activo"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Estado</FormLabel>
                  <Select
                    onValueChange={(value) => field.onChange(value === "true")}
                    defaultValue={String(field.value)}
                  >
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Selecciona un estado" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectItem value="true">Activo</SelectItem>
                      <SelectItem value="false">Inactivo</SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Guardando..." : "Guardar Cambios"}
            </Button>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
} 