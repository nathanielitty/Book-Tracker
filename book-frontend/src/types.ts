// Types matching the backend models

export enum Shelf {
  WANT_TO_READ = 'WANT_TO_READ',
  CURRENTLY_READING = 'CURRENTLY_READING',
  COMPLETED = 'COMPLETED',
}

export interface Book {
  externalId: string;
  title: string;
  authors: string[];
  thumbnailUrl: string;
  description: string;
}

export interface UserBook {
  id: number;
  externalId: string;
  title: string;
  thumbnailUrl: string;
  description: string;
  shelf: Shelf;
  rating: number | null;
  addedAt: string;
}

export interface Recommendation {
  externalId: string;
  title: string;
  thumbnailUrl: string;
  score: number;
  computedAt: string;
}

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

export interface AddRequest {
  externalId: string;
  shelf: Shelf;
}

export interface RateRequest {
  userBookId: number;
  rating: number;
}