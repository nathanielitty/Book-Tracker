import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import * as libraryApi from '../api/libraryApi';
import { useAuth } from './useAuth';

export function useLibrary() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const { userId } = useAuth();
  const navigate = useNavigate();

  const getUserBooks = useCallback(
    async (status?: libraryApi.ReadingStatus, page = 0, size = 10) => {
      if (!userId) {
        navigate('/login');
        return null;
      }

      try {
        setLoading(true);
        setError(null);
        const response = await libraryApi.getUserBooks(userId, status, page, size);
        return response;
      } catch (err) {
        setError(err as Error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [userId, navigate]
  );

  const addBookToLibrary = useCallback(
    async (bookId: string, status: libraryApi.ReadingStatus) => {
      if (!userId) {
        navigate('/login');
        return null;
      }

      try {
        setLoading(true);
        setError(null);
        const response = await libraryApi.addBookToLibrary(userId, bookId, status);
        return response;
      } catch (err) {
        setError(err as Error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [userId, navigate]
  );

  const updateBookStatus = useCallback(
    async (bookId: string, status: libraryApi.ReadingStatus) => {
      if (!userId) {
        navigate('/login');
        return null;
      }

      try {
        setLoading(true);
        setError(null);
        const response = await libraryApi.updateBookStatus(userId, bookId, status);
        return response;
      } catch (err) {
        setError(err as Error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [userId, navigate]
  );

  const updateReadingProgress = useCallback(
    async (bookId: string, currentPage: number, totalPages: number) => {
      if (!userId) {
        navigate('/login');
        return null;
      }

      try {
        setLoading(true);
        setError(null);
        const response = await libraryApi.updateReadingProgress(userId, bookId, currentPage, totalPages);
        return response;
      } catch (err) {
        setError(err as Error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [userId, navigate]
  );

  const addReviewAndRating = useCallback(
    async (bookId: string, rating: number, review?: string) => {
      if (!userId) {
        navigate('/login');
        return null;
      }

      try {
        setLoading(true);
        setError(null);
        const response = await libraryApi.addReviewAndRating(userId, bookId, rating, review);
        return response;
      } catch (err) {
        setError(err as Error);
        return null;
      } finally {
        setLoading(false);
      }
    },
    [userId, navigate]
  );

  return {
    loading,
    error,
    getUserBooks,
    addBookToLibrary,
    updateBookStatus,
    updateReadingProgress,
    addReviewAndRating,
  };
}
