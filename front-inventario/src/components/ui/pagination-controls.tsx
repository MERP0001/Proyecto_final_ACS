import React from 'react';
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";

interface PaginationControlsProps {
  currentPage: number;
  totalPages: number;
  totalElements: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
  pageSizeOptions?: number[];
  showInfo?: boolean;
}

export const PaginationControls: React.FC<PaginationControlsProps> = ({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
  onPageSizeChange,
  pageSizeOptions = [5, 10, 20, 50],
  showInfo = true,
}) => {
  const generatePageNumbers = () => {
    const pages = [];
    const delta = 2; // Páginas a mostrar a cada lado de la actual

    // Siempre mostrar la primera página
    pages.push(1);

    // Calcular el rango alrededor de la página actual
    const start = Math.max(2, currentPage - delta);
    const end = Math.min(totalPages - 1, currentPage + delta);

    // Agregar puntos suspensivos si hay un gap después de la primera página
    if (start > 2) {
      pages.push('...');
    }

    // Agregar páginas del rango
    for (let i = start; i <= end; i++) {
      if (i !== 1 && i !== totalPages) {
        pages.push(i);
      }
    }

    // Agregar puntos suspensivos si hay un gap antes de la última página
    if (end < totalPages - 1) {
      pages.push('...');
    }

    // Siempre mostrar la última página si hay más de una página
    if (totalPages > 1) {
      pages.push(totalPages);
    }

    return pages;
  };

  const startItem = (currentPage - 1) * pageSize + 1;
  const endItem = Math.min(currentPage * pageSize, totalElements);

  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-between px-2">
      {/* Información de resultados */}
      {showInfo && (
        <div className="flex items-center space-x-4">
          <p className="text-sm text-muted-foreground">
            Mostrando {startItem} a {endItem} de {totalElements} resultados
          </p>
          <div className="flex items-center space-x-2">
            <span className="text-sm text-muted-foreground">Mostrar</span>
            <Select value={pageSize.toString()} onValueChange={(value) => onPageSizeChange(Number(value))}>
              <SelectTrigger className="w-20">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {pageSizeOptions.map((size) => (
                  <SelectItem key={size} value={size.toString()}>
                    {size}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <span className="text-sm text-muted-foreground">por página</span>
          </div>
        </div>
      )}

      {/* Controles de paginación */}
      <Pagination>
        <PaginationContent>
                     <PaginationItem>
             <PaginationPrevious 
               href="#"
               size="default"
               onClick={(e) => {
                 e.preventDefault();
                 if (currentPage > 1) onPageChange(currentPage - 1);
               }}
               className={currentPage <= 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
             />
           </PaginationItem>
          
          {generatePageNumbers().map((page, index) => (
            <PaginationItem key={index}>
              {page === '...' ? (
                <PaginationEllipsis />
              ) : (
                                 <PaginationLink
                   href="#"
                   size="icon"
                   onClick={(e) => {
                     e.preventDefault();
                     onPageChange(page as number);
                   }}
                   isActive={currentPage === page}
                   className="cursor-pointer"
                 >
                   {page}
                 </PaginationLink>
              )}
            </PaginationItem>
          ))}
          
                     <PaginationItem>
             <PaginationNext 
               href="#"
               size="default"
               onClick={(e) => {
                 e.preventDefault();
                 if (currentPage < totalPages) onPageChange(currentPage + 1);
               }}
               className={currentPage >= totalPages ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
             />
           </PaginationItem>
        </PaginationContent>
      </Pagination>
    </div>
  );
}; 