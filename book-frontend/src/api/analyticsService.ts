import api from './index';

export interface ReadingStats {
  totalBooksRead: number;
  booksReadThisMonth: number;
  pagesReadThisMonth: number;
  averageRating: number;
  readingStreak: number;
}

export interface BookProgress {
  date: string;
  pagesRead: number;
  totalPages: number;
}

const analyticsService = {
  getReadingStats: async (): Promise<ReadingStats> => {
    const response = await api.get('/analytics/stats');
    return response.data;
  },

  getReadingProgress: async (bookId: string): Promise<BookProgress[]> => {
    const response = await api.get(`/analytics/progress/${bookId}`);
    return response.data;
  },

  getMonthlyProgress: async (year: number, month: number): Promise<BookProgress[]> => {
    const response = await api.get(`/analytics/monthly/${year}/${month}`);
    return response.data;
  },
};

export default analyticsService;
