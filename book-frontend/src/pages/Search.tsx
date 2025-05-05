import React, { useState } from 'react';
import api from '../api/axios';
import { Book, Shelf, AddRequest } from '../types';
import '../App.css';

const Search: React.FC = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<Book[]>([]);
  const [shelfMap, setShelfMap] = useState<Record<string, Shelf>>({});
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const doSearch = async () => {
    if (!query.trim()) return;
    
    setIsLoading(true);
    try {
      const res = await api.get<Book[]>('/library/search', { params: { q: query } });
      setResults(res.data);
      // default shelf selection
      const map: Record<string, Shelf> = {};
      res.data.forEach(b => { map[b.externalId] = Shelf.WANT_TO_READ; });
      setShelfMap(map);
      setMessage('');
    } catch (err) {
      setMessage('Search failed');
      setResults([]);
    } finally {
      setIsLoading(false);
    }
  };

  const addToShelf = async (book: Book) => {
    try {
      const request: AddRequest = {
        externalId: book.externalId,
        shelf: shelfMap[book.externalId]
      };
      await api.post('/library', request);
      setMessage(`Added "${book.title}" to ${shelfMap[book.externalId].replace('_', ' ')}`);
    } catch (err) {
      setMessage('Failed to add book');
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    doSearch();
  };

  return (
    <div className="page">
      <h2>Search Books</h2>
      <div className="search-bar">
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Search books..."
            value={query}
            onChange={e => setQuery(e.target.value)}
          />
          <button type="submit" disabled={isLoading}>
            {isLoading ? 'Searching...' : 'Search'}
          </button>
        </form>
      </div>
      {message && <p className="message">{message}</p>}
      <div className="results">
        {results.map(book => (
          <div key={book.externalId} className="book-card">
            {book.thumbnailUrl && <img src={book.thumbnailUrl} alt={book.title} />}
            <div className="info">
              <h3>{book.title}</h3>
              <p className="authors">{book.authors?.join(', ')}</p>
              <p className="description">{book.description?.substring(0, 200)}{book.description?.length > 200 ? '...' : ''}</p>
              <div className="book-actions">
                <select
                  value={shelfMap[book.externalId] || Shelf.WANT_TO_READ}
                  onChange={e => setShelfMap(prev => ({ ...prev, [book.externalId]: e.target.value as Shelf }))}
                >
                  {Object.values(Shelf).map(s => (
                    <option key={s} value={s}>{s.replace('_', ' ')}</option>
                  ))}
                </select>
                <button onClick={() => addToShelf(book)}>Add</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Search;