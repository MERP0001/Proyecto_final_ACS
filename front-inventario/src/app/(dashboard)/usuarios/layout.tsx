"use client";

import { RoleGuard } from "@/components/auth/role-guard";

export default function UsuariosLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <RoleGuard allowedRoles={["ADMINISTRADOR"]}>{children}</RoleGuard>;
} 