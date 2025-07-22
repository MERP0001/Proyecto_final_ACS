"use client";

import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuLabel, 
  DropdownMenuSeparator, 
  DropdownMenuTrigger 
} from "@/components/ui/dropdown-menu";
import { useAuth } from "@/contexts/auth-context";
import { LogOut, User } from "lucide-react";

export function UserAuthNav() {
  const { user, logout } = useAuth();

  if (!user) return null;

  // Obtener iniciales del nombre completo o usar el username
  const getInitials = () => {
    if (user.nombreCompleto) {
      return user.nombreCompleto
        .split(" ")
        .map(name => name[0])
        .join("")
        .toUpperCase()
        .substring(0, 2);
    }
    // Usar username si está disponible, o "U" como fallback
    return (user.username || "U").substring(0, 2).toUpperCase();
  };

  return (
    <div className="flex items-center gap-2">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="relative h-10 w-10 rounded-full">
            <Avatar className="h-9 w-9">
              <AvatarFallback>{getInitials()}</AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent className="w-56" align="end" forceMount>
          <DropdownMenuLabel>Mi Cuenta</DropdownMenuLabel>
          <DropdownMenuSeparator />
          <div className="flex flex-col space-y-1 leading-none p-2">
            <p className="font-medium">{user.nombreCompleto || user.username || "Usuario"}</p>
            {user.email && (
              <p className="text-xs text-muted-foreground">{user.email}</p>
            )}
            {user.role && (
              <span className="mt-1 inline-flex items-center rounded-md bg-blue-50 px-2 py-1 text-xs font-medium text-blue-700 ring-1 ring-inset ring-blue-700/10">
                {user.role}
              </span>
            )}
          </div>
        </DropdownMenuContent>
      </DropdownMenu>
      
      <Button 
        variant="ghost" 
        size="sm"
        onClick={() => logout()}
        className="gap-2"
      >
        <LogOut className="h-4 w-4" />
        <span>Cerrar Sesión</span>
      </Button>
    </div>
  );
} 