import React, { useState, useEffect } from 'react';
import { completeBookList, SampleBook } from '../data/sampleBooks';
import { Shelf } from '../types';
import { Search as SearchIcon, Filter, X, Star } from 'lucide-react';

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
      ;
    setResults(popularBooks);
    const map: Record<string, Shelf> = {};
    popularBooks.forEach(b => { map[b.id] = Shelf.WANT_TO_READ; });
    setShelfMap(map);
  }, []);

  const doSearch = () => {
    if (!query.trim() && selectedGenre === 'all') {
      const popularBooks = completeBookList
        .sort((a, b) => b.ratingsCount - a.ratingsCount)
        ;
      setResults(popularBooks);
      return;
    }
    
    setIsLoading(true);
    
    setTimeout(() => {
      let filteredBooks = completeBookList;

      if (query.trim()) {
        const searchTerm = query.toLowerCase();
        filteredBooks = filteredBooks.filter(book =>
          book.title.toLowerCase().includes(searchTerm) ||
          book.author.toLowerCase().includes(searchTerm) ||
          book.description.toLowerCase().includes(searchTerm) ||
          book.genre.some(g => g.toLowerCase().includes(searchTerm))
        );
      }

      if (selectedGenre !== 'all') {
        filteredBooks = filteredBooks.filter(book =>
          book.genre.includes(selectedGenre)
        );
      }

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
      }

      setResults(filteredBooks);
      
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
    const popularBooks = completeBookList
      .sort((a, b) => b.ratingsCount - a.ratingsCount)
      ;
    setResults(popularBooks);
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <div className="container mx-auto px-4 py-8">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold mb-4 bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent">
            🔍 Discover Books
          </h1>
          <p className="text-gray-400 text-lg">
            Search through our collection of {completeBookList.length}+ amazing books
          </p>
        </div>

        <form onSubmit={handleSubmit} className="bg-gray-800 rounded-lg p-6 mb-8 space-y-4">
          <div className="flex gap-4">
            <div className="flex-1 relative">
              <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
              <input
                type="text"
                value={query}
                onChange={e => setQuery(e.target.value)}
                placeholder="Search by title, author, or genre..."
                className="w-full pl-10 pr-4 py-3 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:border-blue-500"
              />
            </div>
            <button 
              type="submit" 
              disabled={isLoading}
              className="px-6 py-3 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white rounded-lg font-medium transition-colors flex items-center gap-2"
            >
              {isLoading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                  Searching...
                </>
              ) : (
                <>
                  <SearchIcon size={16} />
                  Search
                </>
              )}
            </button>
          </div>

          <div className="flex flex-wrap gap-4">
            <div className="flex items-center gap-2">
              <Filter size={16} className="text-gray-400" />
              <select 
                value={selectedGenre} 
                onChange={e => setSelectedGenre(e.target.value)}
                className="px-3 py-2 bg-gray-700 border border-gray-600 rounded text-white focus:outline-none focus:border-blue-500"
              >
                <option value="all">All Genres</option>
                {allGenres.map(genre => (
                  <option key={genre} value={genre}>{genre}</option>
                ))}
              </select>
            </div>

            <select 
              value={sortBy} 
              onChange={e => setSortBy(e.target.value)}
              className="px-3 py-2 bg-gray-700 border border-gray-600 rounded text-white focus:outline-none focus:border-blue-500"
            >
              <option value="relevance">Sort by Relevance</option>
              <option value="rating">Highest Rated</option>
              <option value="popularity">Most Popular</option>
              <option value="newest">Newest First</option>
              <option value="title">Title A-Z</option>
            </select>

            <button 
              type="button" 
              onClick={clearSearch}
              className="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded transition-colors flex items-center gap-2"
            >
              <X size={16} />
              Clear
            </button>
          </div>
        </form>

        {message && (
          <div className="bg-green-900 border border-green-700 text-green-200 px-4 py-3 rounded mb-6">
            {message}
          </div>
        )}

        <div className="mb-4">
          <h3 className="text-xl font-semibold text-white">
            Found {results.length} books
          </h3>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5 gap-6">
          {results.map(book => (
            <div key={book.id} className="bg-gray-800 rounded-lg overflow-hidden border border-gray-700 hover:border-blue-500 transition-colors group">
              <div className="aspect-[3/4] overflow-hidden">
                <img 
                  src={book.coverUrl} 
                  alt={book.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
              </div>
              
              <div className="p-4 space-y-3">
                <div>
                  <h4 className="font-semibold text-white line-clamp-2 group-hover:text-blue-400 transition-colors">
                    {book.title}
                  </h4>
                  <p className="text-gray-400 text-sm mt-1">{book.author}</p>
                </div>
                
                <div className="flex items-center gap-1">
                  <Star className="text-yellow-400 fill-current" size={16} />
                  <span className="text-gray-300 text-sm">{book.averageRating}</span>
                  <span className="text-gray-500 text-sm">({book.ratingsCount.toLocaleString()})</span>
                </div>
                
                <div className="flex flex-wrap gap-1">
                  {book.genre.slice(0, 2).map(genre => (
                    <span key={genre} className="px-2 py-1 bg-blue-600 text-white text-xs rounded">
                      {genre}
                    </span>
                  ))}
                  {book.genre.length > 2 && (
                    <span className="px-2 py-1 bg-gray-600 text-gray-300 text-xs rounded">
                      +{book.genre.length - 2}
                    </span>
                  )}
                </div>
                
                <div className="flex gap-2">
                  <select
                    value={shelfMap[book.id] || Shelf.WANT_TO_READ}
                    onChange={e => setShelfMap(prev => ({ ...prev, [book.id]: e.target.value as Shelf }))}
                    className="flex-1 px-3 py-2 bg-gray-700 border border-gray-600 rounded text-white text-sm focus:outline-none focus:border-blue-500"
                  >
                    <option value={Shelf.WANT_TO_READ}>Want to Read</option>
                    <option value={Shelf.CURRENTLY_READING}>Currently Reading</option>
                    <option value={Shelf.COMPLETED}>Completed</option>
                  </select>
                  <button
                    onClick={() => addToShelf(book)}
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm rounded transition-colors"
                  >
                    Add
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Search;
