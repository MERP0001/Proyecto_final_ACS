"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { UserForm } from "@/components/users/user-form";
import { userService } from "@/services/users";
import { AuthRequest } from "@/types";

export default function NuevoUsuarioPage() {
  const router = useRouter();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (values: AuthRequest) => userService.createUser(values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      router.push("/usuarios");
    },
    onError: (error) => {
      console.error("Error al crear el usuario:", error);
    },
  });

  const handleSubmit = (values: any) => {
    const userData: AuthRequest = {
      ...values,
      password: `tempPassword${Date.now()}`,
    };
    mutation.mutate(userData);
  };

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">Agregar Nuevo Usuario</h1>
      <UserForm
        onSubmit={handleSubmit}
        isSubmitting={mutation.isPending}
      />
    </div>
  );
} 