"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useParams, useRouter } from "next/navigation";
import { UserForm } from "@/components/users/user-form";
import { userService } from "@/services/users";
import { User } from "@/types";

export default function EditarUsuarioPage() {
  const router = useRouter();
  const params = useParams();
  const userId = Number(params.id);
  const queryClient = useQueryClient();

  const { data: user, isLoading, error } = useQuery<User>({
    queryKey: ['user', userId],
    queryFn: () => userService.getUserById(userId),
    enabled: !!userId,
  });

  const mutation = useMutation({
    mutationFn: (values: Partial<User>) => userService.updateUser(userId, values),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      queryClient.invalidateQueries({ queryKey: ['user', userId] });
      router.push("/usuarios");
    },
    onError: (error) => {
      console.error("Error al actualizar el usuario:", error);
    },
  });

  const handleSubmit = (values: Partial<User>) => {
    mutation.mutate(values);
  };

  if (isLoading) return <div>Cargando...</div>;
  if (error) return <div>Error al cargar el usuario.</div>;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">Editar Usuario</h1>
      {user && (
        <UserForm
          initialData={user}
          onSubmit={handleSubmit}
          isSubmitting={mutation.isPending}
        />
      )}
    </div>
  );
} 