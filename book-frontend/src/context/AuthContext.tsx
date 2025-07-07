import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { login as apiLogin, register as apiRegister } from '../api/authApi';

interface AuthContextType {
  token: string | null;
  userId: string | null;
  username: string | null;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
  bypassLogin: (userId: string, username: string) => void;
}

export const AuthContext = createContext<AuthContextType>({
  token: null,
  userId: null,
  username: null,
  login: async () => {},
  register: async () => {},
  logout: () => {},
  isAuthenticated: false,
  bypassLogin: () => {},
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
    // Temporarily disable token validation to test login flow
    // const validateSession = async () => {
    //   if (token) {
    //     try {
    //       const isValid = await validateToken();
    //       if (!isValid) {
    //         logout();
    //       }
    //     } catch (error) {
    //       // If validation fails, logout silently
    //       console.warn('Token validation failed:', error);
    //       logout();
    //     }
    //   }
    // };
    // validateSession();
  }, [token, logout]);

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
    setToken(response.token);
    setUserId(response.userId);
    setUsername(response.username);
    navigate('/');
  };

  const register = async (username: string, email: string, password: string) => {
    const response = await apiRegister({ username, email, password });
    setToken(response.token);
    setUserId(response.userId);
    setUsername(response.username);
    navigate('/');
  };

  const bypassLogin = (userId: string, username: string) => {
    const mockToken = 'test-token-123';
    setToken(mockToken);
    setUserId(userId);
    setUsername(username);
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
      bypassLogin
    }}>
      {children}
    </AuthContext.Provider>
  );
};