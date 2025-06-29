// Types matching the backend microservices models

export enum ReadingStatus {
  WANT_TO_READ = 'WANT_TO_READ',
  CURRENTLY_READING = 'CURRENTLY_READING',
  READ = 'READ',
  DNF = 'DNF'
}

export enum Shelf {
  WANT_TO_READ = 'WANT_TO_READ',
  CURRENTLY_READING = 'CURRENTLY_READING',
  COMPLETED = 'COMPLETED'
}

export interface Book {
  id: string;
  title: string;
  author: string;
  description: string;
  coverUrl: string;
  publishedYear: number;
  genre: string[];
  isbn: string;
  pages: number;
  averageRating: number;
  ratingsCount: number;
  language: string;
  publisher: string;
}

export interface UserBook extends Book {
  status: ReadingStatus;
  startedAt?: string;
  finishedAt?: string;
  currentPage?: number;
  totalPages?: number;
  rating?: number;
  review?: string;
  thumbnailUrl?: string;
  addedAt?: string;
}

export interface SearchBooksResponse {
  books: Book[];
  totalItems: number;
  currentPage: number;
  totalPages: number;
}

export interface UserLibraryResponse {
  books: UserBook[];
  totalItems: number;
  currentPage: number;
  totalPages: number;
}

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

export interface RateRequest {
  userBookId: string;
  rating: number;
}