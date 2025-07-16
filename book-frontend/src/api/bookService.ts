import api from './index';

export interface Book {
  id: string;
  title: string;
  author: string;
  description: string;
  isbn: string;
  coverUrl: string;
  pageCount: number;
  publishedDate: string;
}

export interface SearchResponse {
  books: Book[];
  totalItems: number;
}

const bookService = {
  search: async (query: string, page: number = 0): Promise<SearchResponse> => {
    const response = await api.get(`/books/search?q=${query}&page=${page}`);
    return response.data;
  },

  getBookById: async (id: string): Promise<Book> => {
    const response = await api.get(`/books/${id}`);
    return response.data;
  },
};

export default bookService;
