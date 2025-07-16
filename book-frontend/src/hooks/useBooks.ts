import { useState, useCallback } from 'react';
import * as bookApi from '../api/bookApi';

export function useBooks() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const searchBooks = useCallback(
    async (query: string, page = 0, size = 10) => {
      try {
        setLoading(true);
        setError(null);
        const response = await bookApi.searchBooks(query, page, size);
        return response;
      } catch (err) {
        setError(err as Error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  const getBookById = useCallback(
    async (bookId: string) => {
      try {
        setLoading(true);
        setError(null);
        const response = await bookApi.getBookById(bookId);
        return response;
      } catch (err) {
        setError(err as Error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    []
  );

  return {
    loading,
    error,
    searchBooks,
    getBookById,
  };
}
