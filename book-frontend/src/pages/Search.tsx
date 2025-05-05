import React, { useState } from 'react';
import api from '../api/axios';
import '../App.css';

type BookDto = {
  externalId: string;
  title: string;
  authors: string[];
  thumbnailUrl: string;
  description: string;
};

enum Shelf {
  WANT_TO_READ = 'WANT_TO_READ',
  CURRENTLY_READING = 'CURRENTLY_READING',
  COMPLETED = 'COMPLETED',
}

const Search: React.FC = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<BookDto[]>([]);
  const [shelfMap, setShelfMap] = useState<Record<string, Shelf>>({});
  const [message, setMessage] = useState('');

  const doSearch = async () => {
    try {
      const res = await api.get<BookDto[]>('/library/search', { params: { q: query } });
      setResults(res.data);
      // default shelf selection
      const map: Record<string, Shelf> = {};
      res.data.forEach(b => { map[b.externalId] = Shelf.WANT_TO_READ; });
      setShelfMap(map);
    } catch {
      setMessage('Search failed');
    }
  };

  const addToShelf = async (book: BookDto) => {
    try {
      await api.post('/library', { externalId: book.externalId, shelf: shelfMap[book.externalId] });
      setMessage(`Added "${book.title}" to ${shelfMap[book.externalId]}`);
    } catch {
      setMessage('Failed to add book');
    }
  };

  return (
    <div className="page">
      <h2>Search Books</h2>
      <div className="search-bar">
        <input
          type="text"
          placeholder="Search books..."
          value={query}
          onChange={e => setQuery(e.target.value)}
        />
        <button onClick={doSearch}>Search</button>
      </div>
      {message && <p className="message">{message}</p>}
      <div className="results">
        {results.map(book => (
          <div key={book.externalId} className="book-card">
            {book.thumbnailUrl && <img src={book.thumbnailUrl} alt={book.title} />}
            <div className="info">
              <h3>{book.title}</h3>
              <p>{book.authors.join(', ')}</p>
              <p>{book.description.substring(0, 200)}...</p>
              <select
                value={shelfMap[book.externalId]}
                onChange={e => setShelfMap(prev => ({ ...prev, [book.externalId]: e.target.value as Shelf }))}
              >
                {Object.values(Shelf).map(s => (
                  <option key={s} value={s}>{s.replace('_', ' ')}</option>
                ))}
              </select>
              <button onClick={() => addToShelf(book)}>Add</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Search;