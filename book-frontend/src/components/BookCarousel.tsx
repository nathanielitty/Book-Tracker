"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { ChevronLeft, ChevronRight, Star } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useAuth } from "../hooks/useAuth"
import { useLibrary } from "../hooks/useLibrary"

type BookLike = {
  id: string;
  title: string;
  author: string;
  coverImage?: string;
  coverUrl?: string;
  averageRating?: number;
  rating?: number;
};

interface BookCarouselProps {
  books: BookLike[];
  title: string;
  className?: string;
}

export function BookCarousel({ books, title, className = "" }: BookCarouselProps) {
  const [startIndex, setStartIndex] = useState(0)
  const visibleBooks = 5
  const navigate = useNavigate()
  const { addBookToLibrary, getUserBooks } = useLibrary()
  const { userId } = useAuth()
  const [libraryIds, setLibraryIds] = useState<string[]>([])

  // Load existing library entries
  useEffect(() => {
    if (userId) {
      getUserBooks(undefined, 0, 100)?.then(resp => {
        if (resp) setLibraryIds(resp.books.map(entry => entry.bookId))
      })
    }
  }, [userId, getUserBooks])

  const handlePrevious = () => {
    setStartIndex(Math.max(0, startIndex - 1))
  }

  const handleNext = () => {
    setStartIndex(Math.min(books.length - visibleBooks, startIndex + 1))
  }

  const handleViewDetails = (bookId: string) => {
    navigate(`/book/${bookId}`)
  }

  const handleAddToList = async (bookId: string) => {
    console.log('🔵 [BookCarousel] handleAddToList called with bookId:', bookId)
    console.log('🔵 [BookCarousel] Current userId:', userId)
    console.log('🔵 [BookCarousel] Current libraryIds:', libraryIds)
    
    try {
      console.log('🔵 [BookCarousel] Calling addBookToLibrary...')
      const added = await addBookToLibrary(bookId, 'WANT_TO_READ')
      console.log('🔵 [BookCarousel] addBookToLibrary response:', added)
      
      if (added) {
        console.log('🔵 [BookCarousel] Book added successfully, updating libraryIds')
        setLibraryIds(prev => [...prev, bookId])
        const bookTitle = books.find(b => b.id === bookId)?.title || 'Book'
        console.log('🔵 [BookCarousel] Showing success alert for:', bookTitle)
        alert(`Added "${bookTitle}" to your Want to Read list!`)
      } else {
        console.log('🔵 [BookCarousel] addBookToLibrary returned false/null')
      }
    } catch (error) {
      console.error('🔴 [BookCarousel] Failed to add book to library:', error)
      alert('Failed to add book to your library. Please try again.')
    }
  }

  return (
    <div className={`${className}`}>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-white">{title}</h2>
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="icon"
            onClick={handlePrevious}
            disabled={startIndex === 0}
            className="h-8 w-8 rounded-full bg-gray-800 border-gray-700 text-gray-300 hover:bg-gray-700"
          >
            <ChevronLeft className="h-4 w-4" />
            <span className="sr-only">Previous</span>
          </Button>
          <Button
            variant="outline"
            size="icon"
            onClick={handleNext}
            disabled={startIndex >= books.length - visibleBooks}
            className="h-8 w-8 rounded-full bg-gray-800 border-gray-700 text-gray-300 hover:bg-gray-700"
          >
            <ChevronRight className="h-4 w-4" />
            <span className="sr-only">Next</span>
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {books.slice(startIndex, startIndex + visibleBooks).map((book) => (
          <div key={book.id} className="group relative">
            <div className="relative h-64 w-full overflow-hidden rounded-lg bg-gray-800 transition-all duration-300 group-hover:shadow-xl">
              <img
                src={book.coverUrl || book.coverImage || "/placeholder.svg"}
                alt={book.title}
                className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
              />
              <div className="absolute inset-0 bg-black/60 opacity-0 flex items-center justify-center transition-opacity duration-300 group-hover:opacity-100">
                <div className="flex flex-col gap-2">
                  <Button 
                    onClick={() => handleViewDetails(book.id)}
                    className="bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white"
                  >
                    View Details
                  </Button>
                  {libraryIds.includes(book.id) ? (
                    <Button disabled variant="outline" className="border-gray-500 text-gray-500">
                      In List
                    </Button>
                  ) : (
                    <Button 
                      onClick={() => handleAddToList(book.id)}
                      variant="outline"
                      className="border-white text-white hover:bg-white hover:text-gray-900"
                    >
                      Add to List
                    </Button>
                  )}
                </div>
              </div>
            </div>
            <div className="mt-3">
              <h3 className="text-sm font-medium text-white line-clamp-1">{book.title}</h3>
              <p className="text-xs text-gray-400">{book.author}</p>
              <div className="flex items-center mt-1">
                <Star className="h-3 w-3 text-yellow-400 mr-1" />
                <span className="text-xs text-gray-300">
                  {typeof book.averageRating === 'number' ? book.averageRating.toFixed(1) : (typeof book.rating === 'number' ? book.rating.toFixed(1) : 'N/A')}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}