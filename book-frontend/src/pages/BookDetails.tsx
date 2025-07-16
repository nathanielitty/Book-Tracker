import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Star, Calendar, BookOpen, User, Globe, Building } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { completeBookList } from '@/data/sampleBooks';
import type { Book } from '@/types';

const BookDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      const foundBook = completeBookList.find(b => b.id === id);
      setBook(foundBook || null);
      setLoading(false);
    }
  }, [id]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-900 text-white flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto mb-4"></div>
          <p className="text-gray-400">Loading book details...</p>
        </div>
      </div>
    );
  }

  if (!book) {
    return (
      <div className="min-h-screen bg-gray-900 text-white flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">Book Not Found</h1>
          <p className="text-gray-400 mb-6">The book you're looking for doesn't exist.</p>
          <Button onClick={() => navigate('/dashboard')} className="bg-blue-600 hover:bg-blue-700">
            Back to Dashboard
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <div className="container mx-auto px-4 py-8">
        {/* Back Button */}
        <Button
          onClick={() => navigate(-1)}
          variant="ghost"
          className="mb-6 text-gray-400 hover:text-white"
        >
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back
        </Button>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Book Cover */}
          <div className="lg:col-span-1">
            <div className="sticky top-8">
              <div className="relative">
                <img
                  src={book.coverUrl}
                  alt={book.title}
                  className="w-full rounded-lg shadow-2xl"
                />
                <div className="absolute top-4 right-4 bg-black/70 backdrop-blur-sm rounded-full px-3 py-1">
                  <div className="flex items-center">
                    <Star className="h-4 w-4 text-yellow-400 mr-1" />
                    <span className="text-sm font-medium">{book.averageRating.toFixed(1)}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Book Information */}
          <div className="lg:col-span-2">
            <div className="space-y-6">
              {/* Title and Author */}
              <div>
                <h1 className="text-4xl font-bold mb-2 bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent">
                  {book.title}
                </h1>
                <p className="text-xl text-gray-300 mb-4">by {book.author}</p>
              </div>

              {/* Rating and Stats */}
              <div className="flex items-center space-x-6 text-sm text-gray-400">
                <div className="flex items-center">
                  <Star className="h-4 w-4 text-yellow-400 mr-1" />
                  <span>{book.averageRating.toFixed(1)} ({book.ratingsCount.toLocaleString()} ratings)</span>
                </div>
                <div className="flex items-center">
                  <BookOpen className="h-4 w-4 mr-1" />
                  <span>{book.pages} pages</span>
                </div>
                <div className="flex items-center">
                  <Calendar className="h-4 w-4 mr-1" />
                  <span>{book.publishedYear}</span>
                </div>
              </div>

              {/* Genres */}
              <div>
                <h3 className="text-lg font-semibold mb-3">Genres</h3>
                <div className="flex flex-wrap gap-2">
                  {book.genre.map((genre, index) => (
                    <span
                      key={index}
                      className="px-3 py-1 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-full text-sm"
                    >
                      {genre}
                    </span>
                  ))}
                </div>
              </div>

              {/* Description */}
              <div>
                <h3 className="text-lg font-semibold mb-3">Description</h3>
                <p className="text-gray-300 leading-relaxed">{book.description}</p>
              </div>

              {/* Additional Details */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div className="flex items-center">
                    <Building className="h-4 w-4 text-gray-400 mr-3" />
                    <div>
                      <p className="text-sm text-gray-400">Publisher</p>
                      <p className="font-medium">{book.publisher}</p>
                    </div>
                  </div>
                  <div className="flex items-center">
                    <Globe className="h-4 w-4 text-gray-400 mr-3" />
                    <div>
                      <p className="text-sm text-gray-400">Language</p>
                      <p className="font-medium">{book.language}</p>
                    </div>
                  </div>
                </div>
                <div className="space-y-4">
                  <div className="flex items-center">
                    <BookOpen className="h-4 w-4 text-gray-400 mr-3" />
                    <div>
                      <p className="text-sm text-gray-400">ISBN</p>
                      <p className="font-medium">{book.isbn}</p>
                    </div>
                  </div>
                  <div className="flex items-center">
                    <User className="h-4 w-4 text-gray-400 mr-3" />
                    <div>
                      <p className="text-sm text-gray-400">Author</p>
                      <p className="font-medium">{book.author}</p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex flex-wrap gap-4 pt-6">
                <Button className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700">
                  Add to My List
                </Button>
                <Button variant="outline" className="border-gray-600 text-gray-300 hover:bg-gray-800">
                  Mark as Read
                </Button>
                <Button variant="outline" className="border-gray-600 text-gray-300 hover:bg-gray-800">
                  Currently Reading
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookDetails; 