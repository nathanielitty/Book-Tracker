import React, { useState, useEffect, useContext } from 'react';
import { useParams, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { getUserBooks, addReviewAndRating, updateBookStatus, UserBook, ReadingStatus } from '../api/libraryApi';
import { getBookById, Book as APIBook } from '../api/bookApi';

// Combined library entry with book details
interface EnrichedBook extends UserBook, APIBook {
  libraryEntryId: string;
}

const Shelf: React.FC = () => {
  const { shelf } = useParams<{ shelf: string }>();
  const { userId, username } = useContext(AuthContext);
  const [books, setBooks] = useState<EnrichedBook[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!userId || !shelf) return;
    const loadShelf = async () => {
      setLoading(true);
      try {
        const map: Record<string, ReadingStatus> = {
          'want-to-read': 'WANT_TO_READ',
          reading: 'CURRENTLY_READING',
          read: 'READ',
          dnf: 'DNF',
        };
        const status = map[shelf.toLowerCase()] ?? (shelf.toUpperCase() as ReadingStatus);
        const resp = await getUserBooks(userId, status, 0, 100);
        const enriched = await Promise.all(resp.books.map(async entry => {
          try {
            const details: APIBook = await getBookById(entry.bookId);
            return { libraryEntryId: entry.id, ...entry, ...details } as EnrichedBook;
          } catch {
            return { libraryEntryId: entry.id, ...entry } as EnrichedBook;
          }
        }));
        setBooks(enriched);
      } catch (err) {
        console.error('Shelf load error', err);
        setBooks([]);
      } finally {
        setLoading(false);
      }
    };
    loadShelf();
  }, [userId, shelf]);

  const handleRate = async (entryId: string, bookId: string, rating: number) => {
    try {
      const updated = await addReviewAndRating(userId!, bookId, rating);
      setBooks(prev => prev.map(b => b.libraryEntryId === entryId ? { ...b, rating: updated.rating } : b));
    } catch (err) {
      console.error('Rating error:', err);
    }
  };
  
  // Change reading status and refresh shelf or remove moved books
  const handleStatusChange = async (entryId: string, bookId: string, newStatus: ReadingStatus) => {
    try {
      const updated = await updateBookStatus(userId!, bookId, newStatus);
      // If status matches current shelf, update in place, else remove
      const currentShelfKey = shelf?.toLowerCase();
      const statusKeyMap: Record<string, string> = {
        'want-to-read': 'WANT_TO_READ',
        reading: 'CURRENTLY_READING',
        read: 'READ',
        dnf: 'DNF',
      };
      const currentStatus = statusKeyMap[currentShelfKey!] ?? currentShelfKey?.toUpperCase();
      if (updated.status === currentStatus) {
        setBooks(prev => prev.map(b => b.libraryEntryId === entryId ? { ...b, status: updated.status } as EnrichedBook : b));
      } else {
        setBooks(prev => prev.filter(b => b.libraryEntryId !== entryId));
      }
    } catch (err) {
      console.error('Status update error:', err);
    }
  };

  if (loading) return <div>Loading shelf...</div>;
  if (!loading && books.length === 0) return (
    <div className="p-4">
      <p className="text-gray-400">No books in this shelf yet.</p>
    </div>
  );

  return (
    <div className="p-4">
      <h2 className="text-2xl font-semibold">{username}'s Library</h2>
      {/* Shelf Tabs */}
      <div className="flex space-x-4 mt-2 mb-4">
        {['want-to-read', 'reading', 'read', 'dnf'].map(key => (
          <Link
            key={key}
            to={`/shelf/${key}`}
            className={`px-3 py-1 rounded-lg transition-colors ${shelf === key ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-700 hover:text-white'}`}
          >
            {key.replace(/-/g, ' ').replace(/\b\w/g, c => c.toUpperCase())}
          </Link>
        ))}
      </div>
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
          {books.map(b => (
            <tr key={b.libraryEntryId} className="border-b hover:bg-gray-100">
              <td className="px-2 py-1">
                {b.coverImage ? <img src={b.coverImage} alt={b.title} className="w-12 h-16 object-cover"/> : '-'}
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
    </div>
  );
};

export default Shelf;