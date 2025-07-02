"use client"

import * as React from "react"
import {
  Package,
  Home,
  Search,
  AlertTriangle,
  BarChart3,
  Settings,
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

// This is sample data.
const data = {
  user: {
    name: "Admin",
    email: "admin@inventarios.com",
    avatar: "/avatars/shadcn.jpg",
  },
  navMain: [
    {
      title: "Dashboard",
      url: "/dashboard",
      icon: Home,
    },
    {
      title: "Productos",
      url: "/productos",
      icon: Package,
      isActive: true,
      items: [
        {
          title: "Lista de Productos",
          url: "/productos",
        },
        {
          title: "Agregar Producto",
          url: "/productos/nuevo",
        },
        {
          title: "Categorías",
          url: "/productos/categorias",
        },
      ],
    },
    {
      title: "Búsqueda",
      url: "/buscar",
      icon: Search,
    },
    {
      title: "Stock Bajo",
      url: "/stock-bajo",
      icon: AlertTriangle,
    },
    {
      title: "Reportes",
      url: "/reportes",
      icon: BarChart3,
    },
    {
      title: "Configuración",
      url: "/configuracion",
      icon: Settings,
    },
  ],
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <div className="flex items-center gap-2 px-4 py-2">
          <Package className="h-6 w-6" />
          <span className="font-semibold">Sistema Inventarios</span>
        </div>
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={data.user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
} 