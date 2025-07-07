import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { getUserBooks, addReviewAndRating, UserBook, ReadingStatus } from '../api/libraryApi';
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

  if (loading) return <div>Loading shelf...</div>;
  if (!loading && books.length === 0) return (
    <div className="p-4">
      <p className="text-gray-400">No books in this shelf yet.</p>
    </div>
  );

  return (
    <div className="p-4">
      <h2>{username}'s {shelf?.replace(/-/g,' ')?.toUpperCase()} Shelf</h2>
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
              <td className="px-2 py-1">{b.status.replace('_',' ').toLowerCase()}</td>
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