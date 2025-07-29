import apiClient from "@/lib/api";
import { MovementHistory } from "@/types";
import { useQuery } from "@tanstack/react-query";

const HISTORIAL_QUERY_KEY = ["historial"];

export interface GetHistorialParams {
  page?: number;
  size?: number;
  sort?: string;
}

const getHistorial = async (params: GetHistorialParams = {}) => {
  const response = await apiClient.get<{ content: MovementHistory[] }>(
    "/historial",
    { params }
  );
  return response.data;
};

export const useGetHistorial = (params: GetHistorialParams = {}) => {
  return useQuery({
    queryKey: [...HISTORIAL_QUERY_KEY, params],
    queryFn: () => getHistorial(params),
  });
}; 