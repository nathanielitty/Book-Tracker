import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { getUserBooks, addReviewAndRating, updateBookStatus, UserBook, ReadingStatus } from '../api/libraryApi';
import { completeBookList, SampleBook } from '../data/sampleBooks';

// Combined library entry with book details
interface EnrichedBook extends UserBook, SampleBook {
  libraryEntryId: string;
}

const Shelf: React.FC = () => {
  const { shelf } = useParams<{ shelf: string }>();
  const { userId, username } = useContext(AuthContext);
  const [allBooks, setAllBooks] = useState<EnrichedBook[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('all');

  useEffect(() => {
    if (!userId) return;
    const loadAllBooks = async () => {
      setLoading(true);
      try {
        // Load all books regardless of status
        const resp = await getUserBooks(userId, undefined, 0, 1000);
        const enriched = resp.books.map(entry => {
          // Find the book details from sample books
          const bookDetails = completeBookList.find(book => book.id === entry.bookId);
          if (bookDetails) {
            return { 
              libraryEntryId: entry.id, 
              ...entry, 
              ...bookDetails 
            } as EnrichedBook;
          } else {
            // Fallback if book not found in sample data
            return { 
              libraryEntryId: entry.id, 
              ...entry,
              title: `Book ${entry.bookId}`,
              author: 'Unknown Author',
              coverUrl: '/placeholder.svg',
              publishedYear: 0,
              genre: [],
              isbn: '',
              pages: 0,
              averageRating: 0,
              ratingsCount: 0,
              language: 'English',
              publisher: 'Unknown Publisher',
              description: 'Book details not available'
            } as EnrichedBook;
          }
        });
        setAllBooks(enriched);
      } catch (err) {
        console.error('Shelf load error', err);
        setAllBooks([]);
      } finally {
        setLoading(false);
      }
    };
    loadAllBooks();
  }, [userId]);

  // Set active tab based on URL parameter
  useEffect(() => {
    if (shelf) {
      setActiveTab(shelf);
    }
  }, [shelf]);

  const handleRate = async (entryId: string, bookId: string, rating: number) => {
    try {
      const updated = await addReviewAndRating(userId!, bookId, rating);
      setAllBooks(prev => prev.map(b => b.libraryEntryId === entryId ? { ...b, rating: updated.rating } : b));
    } catch (err) {
      console.error('Rating error:', err);
    }
  };
  
  // Change reading status
  const handleStatusChange = async (entryId: string, bookId: string, newStatus: ReadingStatus) => {
    try {
      const updated = await updateBookStatus(userId!, bookId, newStatus);
      setAllBooks(prev => prev.map(b => b.libraryEntryId === entryId ? { ...b, status: updated.status } as EnrichedBook : b));
    } catch (err) {
      console.error('Status update error:', err);
    }
  };

  // Filter books based on active tab
  const getFilteredBooks = () => {
    if (activeTab === 'all') return allBooks;
    
    const statusMap: Record<string, ReadingStatus> = {
      'want-to-read': 'WANT_TO_READ',
      'reading': 'CURRENTLY_READING',
      'read': 'READ',
      'dnf': 'DNF',
    };
    
    const targetStatus = statusMap[activeTab];
    return allBooks.filter(book => book.status === targetStatus);
  };

  const filteredBooks = getFilteredBooks();

  if (loading) return <div>Loading shelf...</div>;
  if (!loading && allBooks.length === 0) return (
    <div className="p-4">
      <p className="text-gray-400">No books in your library yet.</p>
    </div>
  );

  return (
    <div className="p-4">
      <h2 className="text-2xl font-semibold">{username}'s Library</h2>
      {/* Shelf Tabs */}
      <div className="flex space-x-4 mt-2 mb-4">
        {[
          { key: 'all', label: 'All Books' },
          { key: 'want-to-read', label: 'Want to Read' },
          { key: 'reading', label: 'Reading' },
          { key: 'read', label: 'Read' },
          { key: 'dnf', label: 'DNF' }
        ].map(tab => (
          <button
            key={tab.key}
            onClick={() => setActiveTab(tab.key)}
            className={`px-3 py-1 rounded-lg transition-colors ${activeTab === tab.key ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-700 hover:text-white'}`}
          >
            {tab.label}
          </button>
        ))}
      </div>
      
      {filteredBooks.length === 0 ? (
        <div className="p-4">
          <p className="text-gray-400">No books in this category yet.</p>
        </div>
      ) : (
        <table className="min-w-full mt-4 border-collapse">
          <thead>
            <tr className="border-b">
              <th className="px-2 py-1">Cover</th>
              <th className="px-2 py-1">Title</th>
              <th className="px-2 py-1">Author</th>
              <th className="px-2 py-1">Status</th>
              <th className="px-2 py-1">Added</th>
              <th className="px-2 py-1">Rating</th>
            </tr>
          </thead>
          <tbody>
            {filteredBooks.map(b => (
              <tr key={b.libraryEntryId} className="border-b hover:bg-gray-100">
                <td className="px-2 py-1">
                  {b.coverUrl ? <img src={b.coverUrl} alt={b.title} className="w-12 h-16 object-cover"/> : '-'}
                </td>
                <td className="px-2 py-1">{b.title}</td>
                <td className="px-2 py-1">{b.author}</td>
                <td className="px-2 py-1">
                  <select value={b.status} onChange={e => handleStatusChange(b.libraryEntryId, b.bookId, e.target.value as ReadingStatus)}
                    className="bg-gray-700 text-white px-2 py-1 rounded">
                    <option value="WANT_TO_READ">Want to Read</option>
                    <option value="CURRENTLY_READING">Reading</option>
                    <option value="READ">Read</option>
                    <option value="DNF">DNF</option>
                  </select>
                </td>
                <td className="px-2 py-1">{b.createdAt ? new Date(b.createdAt).toLocaleDateString() : '-'}</td>
                <td className="px-2 py-1">
                  <select value={b.rating||0} onChange={e=>handleRate(b.libraryEntryId, b.bookId, +e.target.value)}>
                    <option value={0}>-</option>
                    {[1,2,3,4,5].map(n=><option key={n} value={n}>{n}</option>)}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default Shelf;