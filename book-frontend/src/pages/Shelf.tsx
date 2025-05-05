import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/axios';
import '../App.css';

type UserBookDto = {
  id: number;
  externalId: string;
  title: string;
  thumbnailUrl: string;
  description: string;
  shelf: string;
  rating: number | null;
  addedAt: string;
};

const Shelf: React.FC = () => {
  const { shelf } = useParams<{ shelf: string }>();
  const [books, setBooks] = useState<UserBookDto[]>([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (shelf) fetchShelf();
  }, [shelf]);

  const fetchShelf = async () => {
    try {
      const res = await api.get<UserBookDto[]>(`/library/${shelf}`);
      setBooks(res.data);
    } catch {
      setError('Failed to load shelf');
    }
  };

  const handleRate = async (bookId: number, rating: number) => {
    try {
      const res = await api.post<UserBookDto>('/library/rate', { userBookId: bookId, rating });
      setBooks(prev => prev.map(b => b.id === bookId ? res.data : b));
      setMessage(`Rated "${res.data.title}": ${res.data.rating}`);
    } catch {
      setError('Failed to rate book');
    }
  };

  return (
    <div className="page">
      <h2>{shelf?.replace('_', ' ')}</h2>
      {error && <p className="error">{error}</p>}
      {message && <p className="message">{message}</p>}
      <div className="results">
        {books.map(book => (
          <div key={book.id} className="book-card">
            {book.thumbnailUrl && <img src={book.thumbnailUrl} alt={book.title} />}
            <div className="info">
              <h3>{book.title}</h3>
              <p>{book.description.substring(0, 200)}...</p>
              <div className="rating">
                <label>Rating: </label>
                <select
                  value={book.rating || 0}
                  onChange={e => handleRate(book.id, Number(e.target.value))}
                >
                  <option value={0}>--</option>
                  {[1,2,3,4,5].map(n => (
                    <option key={n} value={n}>{n}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Shelf;