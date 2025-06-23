import React, { useState, useEffect } from 'react';
import { completeBookList, SampleBook } from '../data/sampleBooks';
import { Shelf } from '../types';

const Search: React.FC = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SampleBook[]>([]);
  const [shelfMap, setShelfMap] = useState<Record<string, Shelf>>({});
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [selectedGenre, setSelectedGenre] = useState<string>('all');
  const [sortBy, setSortBy] = useState<string>('relevance');

  // Get all unique genres from the complete book list
  const getAllGenres = () => {
    const genreSet = new Set<string>();
    completeBookList.forEach(book => {
      book.genre.forEach(g => genreSet.add(g));
    });
    return Array.from(genreSet).sort();
  };

  const allGenres = getAllGenres();

  useEffect(() => {
    // Show popular books initially
    const popularBooks = completeBookList
      .sort((a, b) => b.ratingsCount - a.ratingsCount)
      .slice(0, 12);
    setResults(popularBooks);
    const map: Record<string, Shelf> = {};
    popularBooks.forEach(b => { map[b.id] = Shelf.WANT_TO_READ; });
    setShelfMap(map);
  }, []);

  const doSearch = () => {
    if (!query.trim() && selectedGenre === 'all') {
      // Show popular books when no search criteria
      const popularBooks = completeBookList
        .sort((a, b) => b.ratingsCount - a.ratingsCount)
        .slice(0, 20);
      setResults(popularBooks);
      return;
    }
    
    setIsLoading(true);
    
    // Simulate API delay
    setTimeout(() => {
      let filteredBooks = completeBookList;

      // Filter by search query
      if (query.trim()) {
        const searchTerm = query.toLowerCase();
        filteredBooks = filteredBooks.filter(book =>
          book.title.toLowerCase().includes(searchTerm) ||
          book.author.toLowerCase().includes(searchTerm) ||
          book.description.toLowerCase().includes(searchTerm) ||
          book.genre.some(g => g.toLowerCase().includes(searchTerm))
        );
      }

      // Filter by genre
      if (selectedGenre !== 'all') {
        filteredBooks = filteredBooks.filter(book =>
          book.genre.includes(selectedGenre)
        );
      }

      // Sort results
      switch (sortBy) {
        case 'rating':
          filteredBooks.sort((a, b) => b.averageRating - a.averageRating);
          break;
        case 'popularity':
          filteredBooks.sort((a, b) => b.ratingsCount - a.ratingsCount);
          break;
        case 'newest':
          filteredBooks.sort((a, b) => b.publishedYear - a.publishedYear);
          break;
        case 'title':
          filteredBooks.sort((a, b) => a.title.localeCompare(b.title));
          break;
        default:
          // Keep current order for relevance
          break;
      }

      setResults(filteredBooks.slice(0, 50)); // Limit to 50 results
      
      // Default shelf selection
      const map: Record<string, Shelf> = {};
      filteredBooks.forEach(b => { map[b.id] = Shelf.WANT_TO_READ; });
      setShelfMap(map);
      setMessage('');
      setIsLoading(false);
    }, 500);
  };

  const addToShelf = (book: SampleBook) => {
    const shelfName = shelfMap[book.id].replace('_', ' ').toLowerCase();
    setMessage(`Added "${book.title}" to ${shelfName}`);
    // In a real app, this would make an API call
    setTimeout(() => setMessage(''), 3000);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    doSearch();
  };

  const clearSearch = () => {
    setQuery('');
    setSelectedGenre('all');
    setSortBy('relevance');
    // Show popular books
    const popularBooks = completeBookList
      .sort((a, b) => b.ratingsCount - a.ratingsCount)
      .slice(0, 12);
    setResults(popularBooks);
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1>🔍 Discover Books</h1>
        <p>Search through our collection of {completeBookList.length}+ amazing books</p>
      </div>

      <div className="search-section">
        <form onSubmit={handleSubmit} className="search-form">
          <div className="search-row">
            <input
              type="text"
              value={query}
              onChange={e => setQuery(e.target.value)}
              placeholder="Search by title, author, or genre..."
              className="search-input"
            />
            <button type="submit" className="search-button" disabled={isLoading}>
              {isLoading ? '🔄' : '🔍'} Search
            </button>
          </div>

          <div className="filter-row">
            <div className="filter-group">
              <label>Genre:</label>
              <select 
                value={selectedGenre} 
                onChange={e => setSelectedGenre(e.target.value)}
                className="filter-select"
              >
                <option value="all">All Genres</option>
                {allGenres.map(genre => (
                  <option key={genre} value={genre}>{genre}</option>
                ))}
              </select>
            </div>

            <div className="filter-group">
              <label>Sort by:</label>
              <select 
                value={sortBy} 
                onChange={e => setSortBy(e.target.value)}
                className="filter-select"
              >
                <option value="relevance">Relevance</option>
                <option value="rating">Highest Rated</option>
                <option value="popularity">Most Popular</option>
                <option value="newest">Newest</option>
                <option value="title">Title A-Z</option>
              </select>
            </div>

            <button type="button" onClick={clearSearch} className="clear-button">
              Clear Filters
            </button>
          </div>
        </form>

        {message && <div className="message">{message}</div>}
      </div>

      <div className="results-section">
        {isLoading && <div className="loading">Searching books...</div>}
        
        {!isLoading && results.length > 0 && (
          <div className="results-header">
            <h3>Found {results.length} books</h3>
          </div>
        )}

        <div className="results">
          {results.map(book => (
            <div key={book.id} className="book-card">
              <div className="book-cover">
                <img src={book.coverUrl} alt={book.title} />
              </div>
              <div className="book-info">
                <h3 className="book-title">{book.title}</h3>
                <p className="authors">{book.author}</p>
                <p className="book-description">{book.description.substring(0, 150)}...</p>
                
                <div className="book-metadata">
                  <div className="book-rating">
                    <span>⭐ {book.averageRating}</span>
                    <span className="rating-count">({book.ratingsCount.toLocaleString()} ratings)</span>
                  </div>
                  <div className="book-details">
                    <span>📅 {book.publishedYear}</span>
                    <span>📄 {book.pages} pages</span>
                  </div>
                  <div className="book-genres">
                    {book.genre.slice(0, 3).map((genre, index) => (
                      <span key={index} className="genre-badge">{genre}</span>
                    ))}
                  </div>
                </div>

                <div className="book-actions">
                  <select
                    value={shelfMap[book.id] || Shelf.WANT_TO_READ}
                    onChange={e => setShelfMap(prev => ({ ...prev, [book.id]: e.target.value as Shelf }))}
                    className="shelf-select"
                  >
                    <option value={Shelf.WANT_TO_READ}>Want to Read</option>
                    <option value={Shelf.CURRENTLY_READING}>Currently Reading</option>
                    <option value={Shelf.COMPLETED}>Read</option>
                  </select>
                  <button onClick={() => addToShelf(book)} className="add-button">
                    Add to Shelf
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        {!isLoading && results.length === 0 && query && (
          <div className="no-results">
            <h3>No books found</h3>
            <p>Try adjusting your search terms or filters</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Search;
