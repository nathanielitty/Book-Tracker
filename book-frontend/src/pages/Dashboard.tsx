import React, { useState, useEffect } from 'react';
import { completeBookList } from '../data/sampleBooks';
import { BookCarousel } from '../components/BookCarousel';
import { StatsCard } from '../components/StatsCard';
import { ProgressBar } from '../components/ProgressBar';
import { Book, BookOpen, Target, Star, TrendingUp, Clock } from 'lucide-react';
import { useLibrary } from '../hooks/useLibrary';
import { useAuth } from '../hooks/useAuth';
import { UserBook } from '../api/libraryApi';

interface DashboardStats {
  totalBooks: number;
  readBooks: number;
  currentlyReading: number;
  wantToRead: number;
  averageRating: number;
  totalPages: number;
  favoriteGenres: string[];
  readingGoal: number;
  readingProgress: number;
}

const Dashboard: React.FC = () => {
  const { userId } = useAuth();
  const { getUserBooks } = useLibrary();
  const [userBooks, setUserBooks] = useState<UserBook[]>([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState<DashboardStats>({
    totalBooks: 0,
    readBooks: 0,
    currentlyReading: 0,
    wantToRead: 0,
    averageRating: 0,
    totalPages: 0,
    favoriteGenres: [],
    readingGoal: 50,
    readingProgress: 0
  });

  const recentBooks = completeBookList
    .sort((a, b) => parseInt(b.id) - parseInt(a.id))
    .slice(0, 50);
  const topRatedBooks = completeBookList
    .sort((a, b) => b.averageRating - a.averageRating)
    .slice(0, 50);

  useEffect(() => {
    const fetchUserBooks = async () => {
      if (!userId) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const response = await getUserBooks(undefined, 0, 100); // Get all books
        if (response) {
          setUserBooks(response.books);
          
          // Calculate stats from real data
          const readBooks = response.books.filter(book => book.status === 'READ').length;
          const currentlyReading = response.books.filter(book => book.status === 'CURRENTLY_READING').length;
          const wantToRead = response.books.filter(book => book.status === 'WANT_TO_READ').length;
          const totalBooks = response.books.length;
          
          // Calculate average rating
          const booksWithRating = response.books.filter(book => book.rating && book.rating > 0);
          const averageRating = booksWithRating.length > 0 
            ? booksWithRating.reduce((sum, book) => sum + (book.rating || 0), 0) / booksWithRating.length
            : 0;
          
          // Calculate total pages
          const totalPages = response.books.reduce((sum, book) => sum + (book.totalPages || 0), 0);
          
          const calculatedStats: DashboardStats = {
            totalBooks,
            readBooks,
            currentlyReading,
            wantToRead,
            averageRating: Math.round(averageRating * 10) / 10,
            totalPages,
            favoriteGenres: ['Fantasy', 'Science Fiction', 'Fiction'], // TODO: Calculate from real data
            readingGoal: 50,
            readingProgress: readBooks
          };
          
          setStats(calculatedStats);
        }
      } catch (error) {
        console.error('Error fetching user books:', error);
        // Fall back to mock data if API fails
        const mockStats: DashboardStats = {
          totalBooks: 0,
          readBooks: 0,
          currentlyReading: 0,
          wantToRead: 0,
          averageRating: 0,
          totalPages: 0,
          favoriteGenres: [],
          readingGoal: 50,
          readingProgress: 0
        };
        setStats(mockStats);
      } finally {
        setLoading(false);
      }
    };

    fetchUserBooks();
  }, [userId, getUserBooks]);

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <div className="container mx-auto px-4 py-8">
        {/* Hero Section */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold mb-4 bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent">
            📚 Reading Dashboard
          </h1>
          <p className="text-gray-400 text-lg">
            Track your reading journey and discover your next favorite book
          </p>
        </div>

        {/* Reading Goal Progress */}
        <div className="mb-12">
          <ProgressBar 
            progress={stats.readingProgress} 
            total={stats.readingGoal} 
            className="mb-8"
          />
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-12">
          <StatsCard 
            title="Total Books" 
            value={stats.totalBooks} 
            icon={<Book size={24} />}
          />
          <StatsCard 
            title="Books Read" 
            value={stats.readBooks} 
            icon={<BookOpen size={24} />}
          />
          <StatsCard 
            title="Currently Reading" 
            value={stats.currentlyReading} 
            icon={<Clock size={24} />}
          />
          <StatsCard 
            title="Want to Read" 
            value={stats.wantToRead} 
            icon={<Target size={24} />}
          />
          <StatsCard 
            title="Average Rating" 
            value={`${stats.averageRating}⭐`} 
            icon={<Star size={24} />}
          />
          <StatsCard 
            title="Pages Read" 
            value={stats.totalPages.toLocaleString()} 
            icon={<TrendingUp size={24} />}
          />
        </div>

        {/* Favorite Genres */}
        <div className="mb-12">
          <h2 className="text-2xl font-bold mb-6 text-white">🎭 Favorite Genres</h2>
          <div className="flex flex-wrap gap-3">
            {stats.favoriteGenres.map((genre, index) => (
              <span 
                key={index} 
                className="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-full text-sm font-medium hover:scale-105 transition-transform duration-200"
              >
                {genre}
              </span>
            ))}
          </div>
        </div>

        {/* Book Carousels - Vertical Layout */}
        <div className="space-y-16">
          <BookCarousel 
            books={recentBooks} 
            title="📖 Recently Added Books" 
            className="mb-16"
          />
          <BookCarousel 
            books={topRatedBooks} 
            title="⭐ Highest Rated Books" 
            className="mb-16"
          />
        </div>

        {/* Recommendations */}
        <div className="mt-16">
          <h2 className="text-2xl font-bold mb-8 text-white">💡 Personalized Recommendations</h2>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div className="bg-gray-800 p-6 rounded-lg border border-gray-700 hover:border-blue-500 transition-colors">
              <h3 className="text-lg font-semibold text-white mb-3">🔮 Based on Your Reading History</h3>
              <p className="text-gray-400 mb-4">Since you enjoy Fantasy and Science Fiction, you might love:</p>
              <ul className="space-y-2 text-sm">
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• The Fifth Season by N.K. Jemisin</li>
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• Neuromancer by William Gibson</li>
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• The Priory of the Orange Tree by Samantha Shannon</li>
              </ul>
            </div>
            
            <div className="bg-gray-800 p-6 rounded-lg border border-gray-700 hover:border-blue-500 transition-colors">
              <h3 className="text-lg font-semibold text-white mb-3">🔥 Trending Now</h3>
              <p className="text-gray-400 mb-4">Popular books other readers are loving:</p>
              <ul className="space-y-2 text-sm">
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• Fourth Wing by Rebecca Yarros</li>
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• Tomorrow, and Tomorrow, and Tomorrow by Gabrielle Zevin</li>
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• The Atlas Six by Olivie Blake</li>
              </ul>
            </div>
            
            <div className="bg-gray-800 p-6 rounded-lg border border-gray-700 hover:border-blue-500 transition-colors">
              <h3 className="text-lg font-semibold text-white mb-3">🎯 Perfect for Your Goals</h3>
              <p className="text-gray-400 mb-4">Quick reads to help you reach your reading goal:</p>
              <ul className="space-y-2 text-sm">
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• The Alchemist by Paulo Coelho</li>
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• Of Mice and Men by John Steinbeck</li>
                <li className="text-blue-400 hover:text-blue-300 cursor-pointer">• The Great Gatsby by F. Scott Fitzgerald</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;