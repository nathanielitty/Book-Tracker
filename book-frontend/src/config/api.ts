export const config = {
  get apiUrl() {
    if (import.meta.env.DEV) {
      return 'http://localhost:8081/api'; // Direct to auth-service for testing
    } else {
      return import.meta.env.VITE_API_URL || 'http://localhost:8080';
    }
  },
  
  endpoints: {
    auth: {
      login: '/auth/login',
      register: '/auth/register',
      me: '/auth/me',
    },
    books: {
      search: '/books/search',
      details: '/books',
      recommendations: '/books/recommendations',
    },
    library: {
      books: '/library/books',
      rate: '/library/books/rate',
    },
    analytics: {
      userStats: '/analytics/users',
      progress: '/analytics/users',
      trends: '/analytics/users',
    },
    notifications: {
      user: '/notifications/users',
      markRead: '/notifications',
      markAllRead: '/notifications/users',
    }
  },
  
  isDevelopment: import.meta.env.DEV,
  isProduction: import.meta.env.PROD,
  
  timeout: 10000,
};

export default config;
