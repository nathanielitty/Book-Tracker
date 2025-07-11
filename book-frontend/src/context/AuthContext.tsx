import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { login as apiLogin, register as apiRegister } from '../api/authApi';

// Helper function to decode JWT and extract userId
function extractUserIdFromToken(token: string): string | null {
  try {
    console.log('🔍 [AuthContext] Extracting userId from token:', token.substring(0, 50) + '...');
    const payload = token.split('.')[1];
    console.log('🔍 [AuthContext] JWT payload (base64):', payload);
    const decodedPayload = JSON.parse(atob(payload));
    console.log('🔍 [AuthContext] Decoded payload:', decodedPayload);
    const userId = decodedPayload.sub || null;
    console.log('🔍 [AuthContext] Extracted userId:', userId);
    return userId;
  } catch (error) {
    console.error('Error decoding JWT token:', error);
    return null;
  }
}

interface AuthContextType {
  token: string | null;
  userId: string | null;
  username: string | null;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

export const AuthContext = createContext<AuthContextType>({
  token: null,
  userId: null,
  username: null,
  login: async () => {},
  register: async () => {},
  logout: () => {},
  isAuthenticated: false,
});

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [userId, setUserId] = useState<string | null>(localStorage.getItem('userId'));
  const [username, setUsername] = useState<string | null>(localStorage.getItem('username'));
  const navigate = useNavigate();

  const logout = React.useCallback(() => {
    setToken(null);
    setUserId(null);
    setUsername(null);
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    navigate('/login');
  }, [navigate]);

  useEffect(() => {
    if (token && userId && username) {
      localStorage.setItem('token', token);
      localStorage.setItem('userId', userId);
      localStorage.setItem('username', username);
    } else {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      localStorage.removeItem('username');
    }
  }, [token, userId, username]);

  const login = async (username: string, password: string) => {
    const response = await apiLogin({ username, password });
    const extractedUserId = extractUserIdFromToken(response.token);
    
    if (!extractedUserId) {
      throw new Error('Invalid token received from server');
    }
    
    setToken(response.token);
    setUserId(extractedUserId);
    setUsername(response.username);
    navigate('/dashboard');
  };

  const register = async (username: string, email: string, password: string) => {
    const response = await apiRegister({ username, email, password });
    const extractedUserId = extractUserIdFromToken(response.token);
    
    if (!extractedUserId) {
      throw new Error('Invalid token received from server');
    }
    
    setToken(response.token);
    setUserId(extractedUserId);
    setUsername(response.username);
    navigate('/dashboard');
  };

  return (
    <AuthContext.Provider value={{
      token,
      userId,
      username,
      login,
      register,
      logout,
      isAuthenticated: !!token,
    }}>
      {children}
    </AuthContext.Provider>
  );
};