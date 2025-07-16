import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  sub: string;
  exp: number;
  username: string;
}

export const getToken = (): string | null => {
  return localStorage.getItem('token');
};

export const setToken = (token: string): void => {
  localStorage.setItem('token', token);
};

export const removeToken = (): void => {
  localStorage.removeItem('token');
};

export const isAuthenticated = (): boolean => {
  const token = getToken();
  if (!token) return false;

  try {
    const decoded = jwtDecode<DecodedToken>(token);
    const currentTime = Date.now() / 1000;
    return decoded.exp > currentTime;
  } catch {
    return false;
  }
};

export const getUsername = (): string | null => {
  const token = getToken();
  if (!token) return null;

  try {
    const decoded = jwtDecode<DecodedToken>(token);
    return decoded.username;
  } catch {
    return null;
  }
};
