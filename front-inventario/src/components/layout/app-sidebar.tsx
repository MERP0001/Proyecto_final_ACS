"use client"

import * as React from "react"
import {
  Package,
  Home,
  Search,
  AlertTriangle,
  BarChart3,
  Settings,
  Users,
} from "lucide-react"

import { NavMain } from "@/components/layout/nav-main"
import { NavUser } from "@/components/layout/nav-user"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar"
import { useAuth } from "@/contexts/auth-context"

// This is sample data.
const navMain = [
  {
    title: "Dashboard",
    url: "/dashboard",
    icon: Home,
  },
  {
    title: "Productos",
    url: "/productos",
    icon: Package,
    items: [
      {
        title: "Lista de Productos",
        url: "/productos",
      },
      {
        title: "Agregar Producto",
        url: "/productos/nuevo",
        role: "ADMINISTRADOR",
      },
    ],
  },
  {
    title: "Historial",
    url: "/historial",
    icon: Search,
  },
  {
    title: "Reportes",
    url: "/reportes",
    icon: BarChart3,
    items: [
      {
        title: "Stock Bajo",
        url: "/reportes/stock-bajo",
        role: "ADMINISTRADOR",
      },
    ],
  },
  {
    title: "Usuarios",
    url: "/usuarios",
    icon: Users,
    role: "ADMINISTRADOR",
  },
  {
    title: "Configuración",
    url: "/configuracion",
    icon: Settings,
  },
]

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { user } = useAuth()

  // Filtra los items del menú basados en el rol del usuario
  const filteredNav = navMain.map(item => {
    if (!item.items) {
      return item;
    }
    return {
      ...item,
      items: item.items.filter(subItem => !subItem.role || subItem.role === user?.role)
    };
  }).filter(item => {
    // Oculta las secciones principales si todos sus sub-items fueron filtrados
    if (item.items && item.items.length === 0) {
      // Excepción para "Productos", que siempre debe ser visible
      return item.url === '/productos';
    }
    return true;
  });


  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <div className="flex items-center gap-2 px-4 py-2">
          <Package className="h-6 w-6" />
          <span className="font-semibold">Sistema Inventarios</span>
        </div>
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={filteredNav} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser
          user={{
            name: user?.nombreCompleto || "Usuario",
            email: user?.email || "",
          }}
        />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
} 