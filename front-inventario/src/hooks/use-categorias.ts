"use client";

import { useState, useEffect } from 'react';
import { categoriasService } from '@/services/categorias';
import { Categoria } from '@/types';

export function useCategorias() {
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const cargarCategorias = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await categoriasService.getCategorias();
      // Verificar que data es un array válido
      if (Array.isArray(data)) {
        setCategorias(data);
      } else {
        console.error('Los datos de categorías no son un array:', data);
        setCategorias([]);
        setError('Formato de datos inválido');
      }
    } catch (err) {
      setError('Error al cargar categorías');
      setCategorias([]); // Asegurar que siempre sea un array
      console.error('Error en useCategorias:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    cargarCategorias();
  }, []);

  const refetch = () => {
    cargarCategorias();
  };

  return {
    categorias,
    isLoading,
    error,
    refetch
  };
}
