"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
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
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Package, LogIn, AlertCircle } from "lucide-react";
import { useAuth } from "@/contexts/auth-context";

// Schema de validación mejorado
const loginSchema = z.object({
    username: z
        .string()
        .min(3, "El nombre de usuario debe tener al menos 3 caracteres")
        .max(50, "El nombre de usuario no puede exceder los 50 caracteres")
        .regex(/^[a-zA-Z0-9_]+$/, "Solo se permiten letras, números y guiones bajos"),
    password: z
        .string()
        .min(6, "La contraseña debe tener al menos 6 caracteres")
        .max(50, "La contraseña no puede exceder los 50 caracteres")
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginPage() {
    const router = useRouter();
    const { login, error, isLoading, clearError } = useAuth();
    const [showPassword, setShowPassword] = useState(false);

    const form = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
        defaultValues: {
            username: "",
            password: "",
        },
        mode: "onChange", // Validar mientras el usuario escribe
    });

    const onSubmit = async (data: LoginFormData) => {
        try {
            await login(data);
            // La redirección se maneja en el AuthContext
        } catch (error) {
            // El error se maneja en el AuthContext
            console.error("Error en el formulario de login:", error);
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
                        Gestiona tu inventario de forma eficiente y centralizada.
                    </p>
                </div>
            </div>
            <div className="flex items-center justify-center p-6 sm:p-12">
                <div className="w-full max-w-sm space-y-6">
                    <div className="text-center">
                        <h1 className="text-3xl font-bold">Iniciar Sesión</h1>
                        <p className="text-balance text-gray-500">
                            Ingresa tus credenciales para acceder al sistema
                        </p>
                    </div>
                    <Card className="border-0 shadow-none sm:border sm:shadow-lg">
                        <CardContent className="pt-6">
                            <Form {...form}>
                                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
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
                                                        placeholder="Ingresa tu nombre de usuario"
                                                        autoComplete="username"
                                                        onChange={(e) => {
                                                            clearError();
                                                            field.onChange(e);
                                                        }}
                                                        className="px-4 py-2"
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
                                                            placeholder="Ingresa tu contraseña"
                                                            autoComplete="current-password"
                                                            onChange={(e) => {
                                                                clearError();
                                                                field.onChange(e);
                                                            }}
                                                            className="px-4 py-2"
                                                        />
                                                    </FormControl>
                                                    <button
                                                        type="button"
                                                        onClick={() => setShowPassword(!showPassword)}
                                                        className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                                                    >
                                                        {showPassword ? "Ocultar" : "Mostrar"}
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
                                                Iniciando sesión...
                                            </>
                                        ) : (
                                            <>
                                                <LogIn className="mr-2 h-4 w-4" />
                                                Iniciar Sesión
                                            </>
                                        )}
                                    </Button>
                                </form>
                            </Form>

                            <div className="mt-6 text-center">
                                <p className="text-sm text-gray-600">
                                    Credenciales de prueba:
                                </p>
                                <p className="text-sm font-mono bg-gray-100 p-2 rounded mt-2">
                                    Usuario: <strong>admin</strong><br />
                                    Contraseña: <strong>admin123</strong>
                                </p>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    );
} 