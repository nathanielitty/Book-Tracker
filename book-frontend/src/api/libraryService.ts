import api from './index';
import { getToken } from '../utils/auth';
import { jwtDecode } from 'jwt-decode';

export interface UserBook {
  id: string;
  userId: string;
  bookId: string;
  status: 'WANT_TO_READ' | 'CURRENTLY_READING' | 'READ';
  currentPage?: number;
  totalPages?: number;
  startedAt?: string;
  finishedAt?: string;
  rating?: number;
  review?: string;
}

interface DecodedToken {
  sub: string;
  exp: number;
  username: string;
}

// Helper function to get current user ID from JWT token
const getCurrentUserId = (): string => {
  const token = getToken();
  if (!token) {
    throw new Error('No authentication token found');
  }

  try {
    const decoded = jwtDecode<DecodedToken>(token);
    return decoded.sub;
  } catch {
    throw new Error('Invalid authentication token');
  }
};

const libraryService = {
  addToLibrary: async (bookId: string, status: UserBook['status']): Promise<UserBook> => {
    const userId = getCurrentUserId();
    const response = await api.post(`/api/v1/library/users/${userId}/books/${bookId}?status=${status}`);
    return response.data;
  },

  updateStatus: async (bookId: string, status: UserBook['status']): Promise<UserBook> => {
    const userId = getCurrentUserId();
    const response = await api.put(`/api/v1/library/users/${userId}/books/${bookId}/status?status=${status}`);
    return response.data;
  },

  updateProgress: async (bookId: string, currentPage: number, totalPages: number): Promise<UserBook> => {
    const userId = getCurrentUserId();
    const response = await api.put(`/api/v1/library/users/${userId}/books/${bookId}/progress`, { currentPage, totalPages });
    return response.data;
  },

  addReview: async (bookId: string, rating: number, review: string): Promise<UserBook> => {
    const userId = getCurrentUserId();
    const response = await api.put(`/api/v1/library/users/${userId}/books/${bookId}/review`, { rating, review });
    return response.data;
  },

  getUserBooks: async (status?: UserBook['status']): Promise<UserBook[]> => {
    const userId = getCurrentUserId();
    const url = status ? `/api/v1/library/users/${userId}/books?status=${status}` : `/api/v1/library/users/${userId}/books`;
    const response = await api.get(url);
    return response.data.content || response.data; // Handle paginated response
  },

  getBookDetails: async (bookId: string): Promise<UserBook> => {
    const userId = getCurrentUserId();
    const response = await api.get(`/api/v1/library/users/${userId}/books/${bookId}`);
    return response.data;
  },
};

export default libraryService;
