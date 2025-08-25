"use client";

import { useCategorias } from '@/hooks/use-categorias';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Loader2 } from "lucide-react";

interface CategoriaSelectProps {
  value?: string;
  onValueChange?: (value: string) => void;
  placeholder?: string;
  disabled?: boolean;
}

export function CategoriaSelect({ 
  value, 
  onValueChange, 
  placeholder = "Selecciona una categoría",
  disabled = false
}: CategoriaSelectProps) {
  const { categorias, isLoading, error } = useCategorias();

  // Asegurar que categorias siempre sea un array
  const categoriasSeguras = Array.isArray(categorias) ? categorias : [];

  if (error) {
    return (
      <Select value={value} onValueChange={onValueChange} disabled>
        <SelectTrigger>
          <SelectValue placeholder="Error al cargar categorías" />
        </SelectTrigger>
      </Select>
    );
  }

  return (
    <Select value={value} onValueChange={onValueChange} disabled={disabled || isLoading}>
      <SelectTrigger>
        <SelectValue placeholder={
          isLoading ? (
            <div className="flex items-center gap-2">
              <Loader2 className="h-4 w-4 animate-spin" />
              Cargando categorías...
            </div>
          ) : placeholder
        } />
      </SelectTrigger>
      <SelectContent>
        {categoriasSeguras.length > 0 ? (
          categoriasSeguras.map((categoria) => (
            <SelectItem key={categoria.id} value={categoria.nombre}>
              {categoria.nombre}
            </SelectItem>
          ))
        ) : (
          <SelectItem value="no-disponible" disabled>
            No hay categorías disponibles
          </SelectItem>
        )}
      </SelectContent>
    </Select>
  );
}