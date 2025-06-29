import axios from 'axios';

const bookApi = axios.create({
  baseURL: import.meta.env.VITE_BOOK_SERVICE_URL || 'http://localhost:8080/api/books',
});

bookApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface Book {
  id: string;
  title: string;
  author: string;
  isbn: string;
  coverImage?: string;
  description?: string;
  publishedYear?: number;
  pageCount?: number;
  genres?: string[];
}

export interface SearchBooksResponse {
  books: Book[];
  totalItems: number;
  currentPage: number;
  totalPages: number;
}

export const searchBooks = async (
  query: string,
  page = 0,
  size = 10
): Promise<SearchBooksResponse> => {
  const response = await bookApi.get<SearchBooksResponse>('/search', {
    params: { query, page, size },
  });
  return response.data;
};

export const getBookById = async (bookId: string): Promise<Book> => {
  const response = await bookApi.get<Book>(`/${bookId}`);
  return response.data;
};

export default bookApi;
