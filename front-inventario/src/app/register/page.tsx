"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Package, LogIn, AlertCircle, UserPlus } from "lucide-react";
import { useAuth } from "@/contexts/auth-context";

const registerSchema = z
    .object({
        username: z
            .string()
            .min(3, "El nombre de usuario debe tener al menos 3 caracteres")
            .max(50, "El nombre de usuario no puede exceder los 50 caracteres")
            .regex(
                /^[a-zA-Z0-9_]+$/,
                "Solo se permiten letras, números y guiones bajos"
            ),
        password: z
            .string()
            .min(6, "La contraseña debe tener al menos 6 caracteres")
            .max(50, "La contraseña no puede exceder los 50 caracteres"),
        confirmPassword: z.string(),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: "Las contraseñas no coinciden",
        path: ["confirmPassword"],
    });

type RegisterFormData = z.infer<typeof registerSchema>;

export default function RegisterPage() {
    const router = useRouter();
    const { register, error, isLoading, clearError } = useAuth();
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const form = useForm<RegisterFormData>({
        resolver: zodResolver(registerSchema),
        defaultValues: {
            username: "",
            password: "",
            confirmPassword: "",
        },
        mode: "onChange",
    });

    const onSubmit = async (data: RegisterFormData) => {
        try {
            // La función de registro ahora solo necesita username y password
            await register({ username: data.username, password: data.password });
            // Si el registro es exitoso, redirigir al login
            router.push("/login");
        } catch (err) {
            // El error se maneja en el AuthContext
            console.error("Error en el formulario de registro:", err);
        }
    };

    return (
        <div className="w-full min-h-screen lg:grid lg:grid-cols-2">
            <div className="hidden bg-gray-100 lg:flex lg:items-center lg:justify-center p-12">
                <div className="max-w-md text-center">
                    <Package className="mx-auto h-24 w-24 text-primary" />
                    <h2 className="mt-6 text-4xl font-bold tracking-tight text-gray-900">
                        Sistema de Inventarios
                    </h2>
                    <p className="mt-4 text-lg text-gray-600">
                        Crea una cuenta para empezar a gestionar tu inventario.
                    </p>
                </div>
            </div>
            <div className="flex items-center justify-center p-6 sm:p-12">
                <div className="w-full max-w-sm space-y-6">
                    <div className="text-center">
                        <h1 className="text-3xl font-bold">Crear una cuenta</h1>
                        <p className="text-balance text-gray-500">
                            Ingresa tus datos para registrarte en el sistema
                        </p>
                    </div>
                    <Card className="border-0 shadow-none sm:border sm:shadow-lg">
                        <CardContent className="pt-6">
                            <Form {...form}>
                                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                                    {error && (
                                        <Alert variant="destructive">
                                            <AlertCircle className="h-4 w-4" />
                                            <AlertDescription>{error}</AlertDescription>
                                        </Alert>
                                    )}

                                    <FormField
                                        control={form.control}
                                        name="username"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Nombre de Usuario</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        {...field}
                                                        placeholder="Elige un nombre de usuario"
                                                        autoComplete="username"
                                                        onChange={(e) => {
                                                            clearError();
                                                            field.onChange(e);
                                                        }}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={form.control}
                                        name="password"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Contraseña</FormLabel>
                                                <div className="relative">
                                                    <FormControl>
                                                        <Input
                                                            {...field}
                                                            type={showPassword ? "text" : "password"}
                                                            placeholder="Crea una contraseña segura"
                                                            autoComplete="new-password"
                                                            onChange={(e) => {
                                                                clearError();
                                                                field.onChange(e);
                                                            }}
                                                        />
                                                    </FormControl>
                                                    <button
                                                        type="button"
                                                        onClick={() => setShowPassword(!showPassword)}
                                                        className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700 text-sm"
                                                    >
                                                        {showPassword ? "Ocultar" : "Mostrar"}
                                                    </button>
                                                </div>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <FormField
                                        control={form.control}
                                        name="confirmPassword"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Confirmar Contraseña</FormLabel>
                                                <div className="relative">
                                                    <FormControl>
                                                        <Input
                                                            {...field}
                                                            type={showConfirmPassword ? "text" : "password"}
                                                            placeholder="Vuelve a escribir la contraseña"
                                                            autoComplete="new-password"
                                                            onChange={(e) => {
                                                                clearError();
                                                                field.onChange(e);
                                                            }}
                                                        />
                                                    </FormControl>
                                                    <button
                                                        type="button"
                                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                                        className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700 text-sm"
                                                    >
                                                        {showConfirmPassword ? "Ocultar" : "Mostrar"}
                                                    </button>
                                                </div>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />

                                    <Button
                                        type="submit"
                                        disabled={isLoading || !form.formState.isValid}
                                        className="w-full"
                                    >
                                        {isLoading ? (
                                            <>
                                                <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-background border-t-transparent" />
                                                Creando cuenta...
                                            </>
                                        ) : (
                                            <>
                                                <UserPlus className="mr-2 h-4 w-4" />
                                                Crear Cuenta
                                            </>
                                        )}
                                    </Button>
                                </form>
                            </Form>

                            <div className="mt-6 text-center text-sm">
                                ¿Ya tienes una cuenta?{" "}
                                <Link href="/login" className="font-semibold text-primary hover:underline">
                                    Inicia sesión aquí
                                </Link>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    );
}
