import { AppSidebar } from "@/components/layout/app-sidebar"
import {
  SidebarInset,
  SidebarTrigger,
} from "@/components/ui/sidebar"
import { UserAuthNav } from "@/components/auth/user-auth-nav"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-16 shrink-0 items-center gap-2 border-b px-4">
          <SidebarTrigger className="-ml-1" />
          <div className="flex-1">
            <h1 className="font-semibold">Sistema de Gestión de Inventarios</h1>
          </div>
          <UserAuthNav />
        </header>
        <div className="flex flex-1 flex-col gap-4 p-4">
          {children}
        </div>
      </SidebarInset>
    </>
  )
} 