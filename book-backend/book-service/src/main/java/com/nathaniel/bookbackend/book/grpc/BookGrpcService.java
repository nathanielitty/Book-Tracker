package com.nathaniel.bookbackend.book.grpc;

import com.nathaniel.bookbackend.book.model.Book;
import com.nathaniel.bookbackend.book.service.BookService;
import com.nathaniel.bookbackend.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@GrpcService
public class BookGrpcService extends BookServiceGrpc.BookServiceImplBase {

    @Autowired
    private BookService bookService;

    @Override
    public void getBook(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        bookService.getBookById(request.getBookId())
                .ifPresentOrElse(
                        book -> {
                            BookResponse response = BookResponse.newBuilder()
                                    .setId(book.getId())
                                    .setTitle(book.getTitle())
                                    .setAuthor(book.getAuthor())
                                    .setIsbn(book.getIsbn() != null ? book.getIsbn() : "")
                                    .setDescription(book.getDescription() != null ? book.getDescription() : "")
                                    .setCoverUrl(book.getCoverUrl() != null ? book.getCoverUrl() : "")
                                    .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        () -> {
                            responseObserver.onError(
                                    new RuntimeException("Book not found with id: " + request.getBookId()));
                        }
                );
    }

    @Override
    public void searchBooks(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
        Page<Book> bookPage = bookService.searchBooks(
                request.getQuery(),
                PageRequest.of(request.getPage(), request.getSize())
        );

        SearchResponse.Builder responseBuilder = SearchResponse.newBuilder()
                .setTotalPages(bookPage.getTotalPages())
                .setTotalElements(bookPage.getTotalElements());

        bookPage.getContent().forEach(book ->
                responseBuilder.addBooks(BookResponse.newBuilder()
                        .setId(book.getId())
                        .setTitle(book.getTitle())
                        .setAuthor(book.getAuthor())
                        .setIsbn(book.getIsbn() != null ? book.getIsbn() : "")
                        .setDescription(book.getDescription() != null ? book.getDescription() : "")
                        .setCoverUrl(book.getCoverUrl() != null ? book.getCoverUrl() : "")
                        .build())
        );

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateBookStatus(UpdateBookStatusRequest request, StreamObserver<UpdateBookStatusResponse> responseObserver) {
        // This would typically be handled by the library service
        // Here we just validate that the book exists
        boolean exists = bookService.getBookById(request.getBookId()).isPresent();
        
        UpdateBookStatusResponse response = UpdateBookStatusResponse.newBuilder()
                .setSuccess(exists)
                .setMessage(exists ? "Book found" : "Book not found")
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
