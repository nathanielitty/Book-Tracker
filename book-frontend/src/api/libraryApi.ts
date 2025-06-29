import axios from 'axios';
import { Book } from './bookApi';

const libraryApi = axios.create({
  baseURL: import.meta.env.VITE_LIBRARY_SERVICE_URL || '/api/v1/library',
});

libraryApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export type ReadingStatus = 'WANT_TO_READ' | 'CURRENTLY_READING' | 'READ' | 'DNF';

export interface UserBook extends Book {
  status: ReadingStatus;
  startedAt?: string;
  finishedAt?: string;
  currentPage?: number;
  totalPages?: number;
  rating?: number;
  review?: string;
}

export interface UserLibraryResponse {
  books: UserBook[];
  totalItems: number;
  currentPage: number;
  totalPages: number;
}

export const getUserBooks = async (
  userId: string,
  status?: ReadingStatus,
  page = 0,
  size = 10
): Promise<UserLibraryResponse> => {
  const response = await libraryApi.get<UserLibraryResponse>(`/users/${userId}/books`, {
    params: { status, page, size },
  });
  return response.data;
};

export const addBookToLibrary = async (
  userId: string,
  bookId: string,
  status: ReadingStatus
): Promise<UserBook> => {
  const response = await libraryApi.post<UserBook>(
    `/users/${userId}/books/${bookId}`,
    null,
    { params: { status } }
  );
  return response.data;
};

export const updateBookStatus = async (
  userId: string,
  bookId: string,
  status: ReadingStatus
): Promise<UserBook> => {
  const response = await libraryApi.put<UserBook>(
    `/users/${userId}/books/${bookId}/status`,
    null,
    { params: { status } }
  );
  return response.data;
};

export const updateReadingProgress = async (
  userId: string,
  bookId: string,
  currentPage: number,
  totalPages: number
): Promise<UserBook> => {
  const response = await libraryApi.put<UserBook>(
    `/users/${userId}/books/${bookId}/progress`,
    null,
    { params: { currentPage, totalPages } }
  );
  return response.data;
};

export const addReviewAndRating = async (
  userId: string,
  bookId: string,
  rating: number,
  review?: string
): Promise<UserBook> => {
  const response = await libraryApi.put<UserBook>(
    `/users/${userId}/books/${bookId}/review`,
    null,
    { params: { rating, review } }
  );
  return response.data;
};

export default libraryApi;
