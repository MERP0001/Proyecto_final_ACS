"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { PlusCircle, MoreHorizontal, Pencil, Trash2 } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Badge } from "@/components/ui/badge";
import { User } from "@/types";
import { userService } from "@/services/users";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";

export default function UsuariosPage() {
  const queryClient = useQueryClient();
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [userToDelete, setUserToDelete] = useState<User | null>(null);

  const { data: users, isLoading, error } = useQuery<User[]>({
    queryKey: ['users'],
    queryFn: userService.getAllUsers,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => userService.deleteUser(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      setShowDeleteDialog(false);
      setUserToDelete(null);
    },
    onError: (error) => {
      console.error("Error al eliminar el usuario", error);
      // Aquí podrías mostrar una notificación de error
    },
  });

  const handleDeleteClick = (user: User) => {
    setUserToDelete(user);
    setShowDeleteDialog(true);
  };

  const confirmDelete = () => {
    if (userToDelete) {
      deleteMutation.mutate(userToDelete.id);
    }
  };

  if (isLoading) return <div>Cargando usuarios...</div>;
  if (error) return <div>Error al cargar los usuarios.</div>;

  return (
    <>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">Gestión de Usuarios</h1>
        <Button asChild>
          <Link href="/usuarios/nuevo">
            <PlusCircle className="mr-2 h-4 w-4" />
            Agregar Usuario
          </Link>
        </Button>
      </div>

      <div className="rounded-lg border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Nombre de Usuario</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Nombre Completo</TableHead>
              <TableHead>Rol</TableHead>
              <TableHead>Estado</TableHead>
              <TableHead>Acciones</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {users?.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.username}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>{user.nombreCompleto}</TableCell>
                <TableCell>
                  <Badge>{user.role}</Badge>
                </TableCell>
                <TableCell>
                  <Badge variant={user.activo ? "default" : "destructive"}>
                    {user.activo ? "Activo" : "Inactivo"}
                  </Badge>
                </TableCell>
                <TableCell>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button variant="ghost" className="h-8 w-8 p-0">
                        <span className="sr-only">Abrir menú</span>
                        <MoreHorizontal className="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem asChild>
                        <Link href={`/usuarios/${user.id}/editar`}>
                          <Pencil className="mr-2 h-4 w-4" />
                          Editar
                        </Link>
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        onClick={() => handleDeleteClick(user)}
                        className="text-red-600"
                      >
                        <Trash2 className="mr-2 h-4 w-4" />
                        Eliminar
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
      <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>¿Estás seguro?</AlertDialogTitle>
            <AlertDialogDescription>
              Esta acción marcará al usuario como inactivo, pero no lo eliminará permanentemente.
              Puedes reactivarlo más tarde.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setUserToDelete(null)}>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDelete}>
              Confirmar
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
} 