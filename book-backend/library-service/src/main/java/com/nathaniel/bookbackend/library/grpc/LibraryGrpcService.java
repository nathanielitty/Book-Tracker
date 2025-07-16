package com.nathaniel.bookbackend.library.grpc;

import com.nathaniel.bookbackend.grpc.*;
import com.nathaniel.bookbackend.library.model.ReadingStatus;
import com.nathaniel.bookbackend.library.model.UserBook;
import com.nathaniel.bookbackend.library.service.LibraryService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@GrpcService
public class LibraryGrpcService extends LibraryServiceGrpc.LibraryServiceImplBase {

    @Autowired
    private LibraryService libraryService;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Override
    public void getUserBooks(UserBooksRequest request, StreamObserver<UserBooksResponse> responseObserver) {
        try {
            Page<UserBook> userBooks;
            PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

            if (request.getStatus().isEmpty()) {
                userBooks = libraryService.getUserBooks(request.getUserId(), pageRequest);
            } else {
                userBooks = libraryService.getUserBooksByStatus(
                    request.getUserId(),
                    ReadingStatus.valueOf(request.getStatus()),
                    pageRequest
                );
            }

            UserBooksResponse response = UserBooksResponse.newBuilder()
                .addAllBooks(userBooks.getContent().stream()
                    .map(this::mapToGrpcUserBook)
                    .toList())
                .setTotalElements((int) userBooks.getTotalElements())
                .setTotalPages(userBooks.getTotalPages())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateBookStatus(BookStatusRequest request, StreamObserver<BookStatusResponse> responseObserver) {
        try {
            Optional<UserBook> updatedBook = libraryService.updateReadingStatus(
                request.getUserId(),
                request.getBookId(),
                ReadingStatus.valueOf(request.getStatus())
            );

            if (updatedBook.isPresent()) {
                BookStatusResponse response = BookStatusResponse.newBuilder()
                    .setBook(mapToGrpcUserBook(updatedBook.get()))
                    .setSuccess(true)
                    .setMessage("Status updated successfully")
                    .build();
                responseObserver.onNext(response);
            } else {
                BookStatusResponse response = BookStatusResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Book not found")
                    .build();
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateReadingProgress(ProgressRequest request, StreamObserver<ProgressResponse> responseObserver) {
        try {
            Optional<UserBook> updated = libraryService.updateReadingProgress(
                request.getUserId(),
                request.getBookId(),
                request.getCurrentPage(),
                request.getTotalPages()
            );
            if (updated.isPresent()) {
                ProgressResponse resp = ProgressResponse.newBuilder()
                    .setBook(mapToGrpcUserBook(updated.get()))
                    .setSuccess(true)
                    .setMessage("Progress updated successfully")
                    .build();
                responseObserver.onNext(resp);
            } else {
                ProgressResponse resp = ProgressResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Book not found")
                    .build();
                responseObserver.onNext(resp);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addReview(ReviewRequest request, StreamObserver<ReviewResponse> responseObserver) {
        try {
            Optional<UserBook> updated = libraryService.addReviewAndRating(
                request.getUserId(),
                request.getBookId(),
                request.getReview(),
                request.getRating()
            );
            if (updated.isPresent()) {
                ReviewResponse resp = ReviewResponse.newBuilder()
                    .setBook(mapToGrpcUserBook(updated.get()))
                    .setSuccess(true)
                    .setMessage("Review added successfully")
                    .build();
                responseObserver.onNext(resp);
            } else {
                ReviewResponse resp = ReviewResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Book not found")
                    .build();
                responseObserver.onNext(resp);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private com.nathaniel.bookbackend.grpc.UserBook mapToGrpcUserBook(UserBook userBook) {
        com.nathaniel.bookbackend.grpc.UserBook.Builder builder = com.nathaniel.bookbackend.grpc.UserBook.newBuilder()
            .setUserId(userBook.getUserId())
            .setBookId(userBook.getBookId())
            .setStatus(userBook.getStatus().name());

        if (userBook.getStartedAt() != null) {
            builder.setStartedAt(ISO_FORMATTER.format(userBook.getStartedAt()));
        }
        if (userBook.getFinishedAt() != null) {
            builder.setFinishedAt(ISO_FORMATTER.format(userBook.getFinishedAt()));
        }
        if (userBook.getCurrentPage() != null) {
            builder.setCurrentPage(userBook.getCurrentPage());
        }
        if (userBook.getTotalPages() != null) {
            builder.setTotalPages(userBook.getTotalPages());
        }
        if (userBook.getRating() != null) {
            builder.setRating(userBook.getRating());
        }
        if (userBook.getReview() != null) {
            builder.setReview(userBook.getReview());
        }

        return builder.build();
    }
}
