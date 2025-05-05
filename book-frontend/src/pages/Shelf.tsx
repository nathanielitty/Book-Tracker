import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/axios';
import { UserBook, Shelf as ShelfType, RateRequest } from '../types';
import '../App.css';

const Shelf: React.FC = () => {
  const { shelf } = useParams<{ shelf: string }>();
  const [books, setBooks] = useState<UserBook[]>([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (shelf) fetchShelf();
  }, [shelf]);

  const fetchShelf = async () => {
    if (!shelf) return;
    
    setIsLoading(true);
    try {
      const res = await api.get<UserBook[]>(`/library/${shelf}`);
      setBooks(res.data);
      setError('');
    } catch (err) {
      setError('Failed to load shelf');
      setBooks([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRate = async (bookId: number, rating: number) => {
    try {
      const request: RateRequest = { userBookId: bookId, rating };
      const res = await api.post<UserBook>('/library/rate', request);
      setBooks(prev => prev.map(b => b.id === bookId ? res.data : b));
      setMessage(`Rated "${res.data.title}": ${rating} stars`);
      setTimeout(() => setMessage(''), 3000); // Clear message after 3 seconds
    } catch (err) {
      setError('Failed to rate book');
      setTimeout(() => setError(''), 3000); // Clear error after 3 seconds
    }
  };

  const formatShelfName = (shelfStr: string) => {
    return shelfStr.replace(/_/g, ' ').toLowerCase()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };

  if (isLoading) return <div className="loading">Loading books...</div>;

  return (
    <div className="page shelf-page">
      <h2>{shelf ? formatShelfName(shelf) : 'Shelf'}</h2>
      {error && <p className="error">{error}</p>}
      {message && <p className="message">{message}</p>}
      
      {books.length === 0 && !isLoading ? (
        <p className="empty-shelf">No books on this shelf yet.</p>
      ) : (
        <div className="book-list">
          {books.map(book => (
            <div key={book.id} className="book-card">
              {book.thumbnailUrl && (
                <div className="book-cover">
                  <img src={book.thumbnailUrl} alt={book.title} />
                </div>
              )}
              <div className="book-info">
                <h3 className="book-title">{book.title}</h3>
                <p className="book-description">
                  {book.description?.substring(0, 200)}
                  {book.description?.length > 200 ? '...' : ''}
                </p>
                <div className="book-rating">
                  <label>Rating: </label>
                  <select
                    value={book.rating || 0}
                    onChange={e => handleRate(book.id, Number(e.target.value))}
                    className="rating-select"
                  >
                    <option value={0}>--</option>
                    {[1, 2, 3, 4, 5].map(n => (
                      <option key={n} value={n}>{n} star{n !== 1 ? 's' : ''}</option>
                    ))}
                  </select>
                </div>
                <div className="book-added">
                  Added: {new Date(book.addedAt).toLocaleDateString()}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Shelf;