import axios from 'axios';

const libraryApi = axios.create({
  baseURL: import.meta.env.VITE_LIBRARY_SERVICE_URL || '/api/v1/library',
});

libraryApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
    console.log('🔍 [libraryApi] Request interceptor - Token:', token.substring(0, 50) + '...');
    console.log('🔍 [libraryApi] Request interceptor - URL:', config.url);
    console.log('🔍 [libraryApi] Request interceptor - Method:', config.method);
    console.log('🔍 [libraryApi] Request interceptor - Headers:', config.headers);
  } else {
    console.log('🔍 [libraryApi] Request interceptor - No token found');
  }
  return config;
});

libraryApi.interceptors.response.use(
  (response) => {
    console.log('🔍 [libraryApi] Response interceptor - Status:', response.status);
    console.log('🔍 [libraryApi] Response interceptor - Data:', response.data);
    return response;
  },
  (error) => {
    console.log('🔍 [libraryApi] Response interceptor - Error status:', error.response?.status);
    console.log('🔍 [libraryApi] Response interceptor - Error data:', error.response?.data);
    console.log('🔍 [libraryApi] Response interceptor - Error headers:', error.response?.headers);
    return Promise.reject(error);
  }
);

export type ReadingStatus = 'WANT_TO_READ' | 'CURRENTLY_READING' | 'READ' | 'DNF';

export interface UserBook {
  // Record-level identifiers
  id: string;
  userId: string;
  // Reference to actual book
  bookId: string;
  status: ReadingStatus;
  startedAt?: string;
  finishedAt?: string;
  currentPage?: number;
  totalPages?: number;
  rating?: number;
  review?: string;
  createdAt?: string;
  updatedAt?: string;
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
  try {
    const response = await libraryApi.get(`/users/${userId}/books`, {
      params: { status, page, size },
    });
    const data = response.data;
    return {
      books: data.content,
      totalItems: data.totalElements,
      currentPage: data.number,
      totalPages: data.totalPages,
    };
  } catch (error: unknown) {
    if (
      typeof error === 'object' &&
      error !== null &&
      'response' in error &&
      (error as { response?: { status?: number } }).response &&
      (((error as { response?: { status?: number } }).response?.status === 401) || ((error as { response?: { status?: number } }).response?.status === 403))
    ) {
      throw new Error('You are not authorized. Please log in again.');
    }
    throw new Error('Failed to fetch user books.');
  }
};

export const addBookToLibrary = async (
  userId: string,
  bookId: string,
  status: ReadingStatus
): Promise<UserBook> => {
  try {
    console.log('🟡 [libraryApi] addBookToLibrary called with:', { userId, bookId, status })
    const response = await libraryApi.post<UserBook>(
      `/users/${userId}/books/${bookId}`,
      null,
      { params: { status } }
    );
    console.log('🟡 [libraryApi] addBookToLibrary response:', response.data)
    return response.data;
  } catch (error: unknown) {
    if (
      typeof error === 'object' &&
      error !== null &&
      'response' in error &&
      (error as { response?: { status?: number } }).response &&
      (((error as { response?: { status?: number } }).response?.status === 401) || ((error as { response?: { status?: number } }).response?.status === 403))
    ) {
      throw new Error('You are not authorized to add this book. Please log in again.');
    }
    throw new Error('Failed to add book to library.');
  }
};

export const updateBookStatus = async (
  userId: string,
  bookId: string,
  status: ReadingStatus
): Promise<UserBook> => {
  try {
    const response = await libraryApi.put<UserBook>(
      `/users/${userId}/books/${bookId}/status`,
      null,
      { params: { status } }
    );
    return response.data;
  } catch (error: unknown) {
    if (
      typeof error === 'object' &&
      error !== null &&
      'response' in error &&
      (error as { response?: { status?: number } }).response &&
      (((error as { response?: { status?: number } }).response?.status === 401) || ((error as { response?: { status?: number } }).response?.status === 403))
    ) {
      throw new Error('You are not authorized to update this book. Please log in again.');
    }
    throw new Error('Failed to update book status.');
  }
};

export const updateReadingProgress = async (
  userId: string,
  bookId: string,
  currentPage: number,
  totalPages: number
): Promise<UserBook> => {
  try {
    const response = await libraryApi.put<UserBook>(
      `/users/${userId}/books/${bookId}/progress`,
      null,
      { params: { currentPage, totalPages } }
    );
    return response.data;
  } catch (error: unknown) {
    if (
      typeof error === 'object' &&
      error !== null &&
      'response' in error &&
      (error as { response?: { status?: number } }).response &&
      (((error as { response?: { status?: number } }).response?.status === 401) || ((error as { response?: { status?: number } }).response?.status === 403))
    ) {
      throw new Error('You are not authorized to update progress. Please log in again.');
    }
    throw new Error('Failed to update reading progress.');
  }
};

export const addReviewAndRating = async (
  userId: string,
  bookId: string,
  rating: number,
  review?: string
): Promise<UserBook> => {
  try {
    const response = await libraryApi.put<UserBook>(
      `/users/${userId}/books/${bookId}/review`,
      null,
      { params: { rating, review } }
    );
    return response.data;
  } catch (error: unknown) {
    if (
      typeof error === 'object' &&
      error !== null &&
      'response' in error &&
      (error as { response?: { status?: number } }).response &&
      (((error as { response?: { status?: number } }).response?.status === 401) || ((error as { response?: { status?: number } }).response?.status === 403))
    ) {
      throw new Error('You are not authorized to add a review. Please log in again.');
    }
    throw new Error('Failed to add review and rating.');
  }
};
