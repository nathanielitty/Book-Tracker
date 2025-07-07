import axios from 'axios';

const authApi = axios.create({
  baseURL: import.meta.env.VITE_AUTH_SERVICE_URL || 'http://localhost:8080/api/v1/auth',
  withCredentials: true, // This enables CORS credentials
});

authApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  userId: string;
  username: string;
}

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  const response = await authApi.post<AuthResponse>('/login', data);
  return response.data;
};

export const register = async (data: RegisterRequest): Promise<AuthResponse> => {
  const response = await authApi.post<AuthResponse>('/register', data);
  return response.data;
};

export const validateToken = async (): Promise<boolean> => {
  try {
    // Check if token exists first
    const token = localStorage.getItem('token');
    if (!token) {
      return false;
    }
    
    await authApi.get(`/validate?token=${token}`);
    return true;
  } catch {
    // Clear invalid token
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    return false;
  }
};

export default authApi;
