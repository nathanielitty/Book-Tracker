import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/index';
import { UserBook, RateRequest } from '../types';
import { AuthContext } from '../context/AuthContext';
import { Book, Star, Calendar, Loader2 } from 'lucide-react';

const Shelf: React.FC = () => {
  const { shelf } = useParams<{ shelf: string }>();
  const { userId } = useContext(AuthContext);
  const [books, setBooks] = useState<UserBook[]>([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const fetchShelf = async () => {
    if (!shelf || !userId) return;
    
    setIsLoading(true);
    try {
      // Map frontend shelf names to backend ReadingStatus enum
      const statusMap: { [key: string]: string } = {
        'reading': 'CURRENTLY_READING',
        'read': 'READ',
        'want-to-read': 'WANT_TO_READ',
        'dnf': 'DNF'
      };
      
      const status = statusMap[shelf.toLowerCase()] || shelf.toUpperCase();
      const res = await api.get<{content: UserBook[]}>(`/api/v1/library/users/${userId}/books?status=${status}`);
      setBooks(res.data.content || []);
      setError('');
    } catch (err) {
      console.error('Failed to load shelf:', err);
      setError('Failed to load shelf');
      setBooks([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (shelf && userId) fetchShelf();
  }, [shelf, userId]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleRate = async (bookId: string, rating: number) => {
    try {
      const request: RateRequest = { userBookId: bookId, rating };
      const res = await api.post<UserBook>('/library/rate', request);
      setBooks(prev => prev.map(b => b.id === bookId ? res.data : b));
      setMessage(`Rated "${res.data.title}": ${rating} stars`);
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      console.error('Failed to rate book:', err);
      setError('Failed to rate book');
      setTimeout(() => setError(''), 3000);
    }
  };

  const formatShelfName = (shelfStr: string) => {
    return shelfStr.replace(/_/g, ' ').toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-900 flex items-center justify-center">
        <div className="flex items-center space-x-3 text-white">
          <Loader2 className="animate-spin" size={24} />
          <span className="text-lg">Loading books...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold mb-2 bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent">
            {shelf ? formatShelfName(shelf) : 'My Shelf'}
          </h1>
          <p className="text-gray-400">
            {books.length > 0 ? `${books.length} book${books.length !== 1 ? 's' : ''}` : 'No books yet'}
          </p>
        </div>

        {/* Messages */}
        {error && (
          <div className="bg-red-900 border border-red-700 text-red-200 px-4 py-3 rounded mb-6">
            {error}
          </div>
        )}
        {message && (
          <div className="bg-green-900 border border-green-700 text-green-200 px-4 py-3 rounded mb-6">
            {message}
          </div>
        )}
        
        {/* Books Grid */}
        {books.length === 0 && !isLoading ? (
          <div className="text-center py-16">
            <Book className="mx-auto text-gray-600 mb-4" size={64} />
            <p className="text-gray-400 text-lg">No books on this shelf yet.</p>
            <p className="text-gray-500 mt-2">Start adding books to build your library!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {books.map(book => (
              <div key={book.id} className="bg-gray-800 rounded-lg overflow-hidden border border-gray-700 hover:border-blue-500 transition-colors group">
                {book.thumbnailUrl && (
                  <div className="aspect-[3/4] overflow-hidden">
                    <img 
                      src={book.thumbnailUrl} 
                      alt={book.title}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                    />
                  </div>
                )}
                
                <div className="p-4 space-y-3">
                  <div>
                    <h3 className="font-semibold text-white text-lg line-clamp-2">
                      {book.title}
                    </h3>
                    {book.author && (
                      <p className="text-gray-400 text-sm mt-1">{book.author}</p>
                    )}
                  </div>
                  
                  {book.description && (
                    <p className="text-gray-300 text-sm line-clamp-3">
                      {book.description}
                    </p>
                  )}
                  
                  {/* Rating */}
                  <div className="flex items-center space-x-2">
                    <Star className="text-yellow-400" size={16} />
                    <select
                      value={book.rating || 0}
                      onChange={e => handleRate(book.id, Number(e.target.value))}
                      className="bg-gray-700 text-white text-sm rounded px-2 py-1 border border-gray-600 focus:border-blue-500 focus:outline-none"
                    >
                      <option value={0}>Rate this book</option>
                      {[1, 2, 3, 4, 5].map(n => (
                        <option key={n} value={n}>
                          {n} star{n !== 1 ? 's' : ''}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  {/* Date Added */}
                  {book.addedAt && (
                    <div className="flex items-center space-x-2 text-gray-400 text-sm">
                      <Calendar size={14} />
                      <span>Added: {new Date(book.addedAt).toLocaleDateString()}</span>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Shelf;