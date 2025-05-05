package com.nathaniel.bookbackend.services;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.nathaniel.bookbackend.dto.BookDto;
import com.nathaniel.bookbackend.dto.RateRequest;
import com.nathaniel.bookbackend.dto.UserBookDto;
import com.nathaniel.bookbackend.models.*;
import com.nathaniel.bookbackend.repository.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LibraryService {
    private final GoogleBooksService googleBooksService;
    private final BookRepository bookRepository;
    private final AppUserRepository userRepository;
    private final UserBookRepository userBookRepository;

    public List<BookDto> searchBooks(String q) {
        return googleBooksService.search(q);
    }

    public BookDto getBook(String externalId) {
        return googleBooksService.fetchByExternalId(externalId);
    }

    public List<Shelf> getShelves() {
        return List.of(Shelf.values());
    }

    public void addToShelf(String username, String externalId, Shelf shelf) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        Book book = bookRepository.findByExternalId(externalId)
            .orElseGet(() -> {
                BookDto dto = googleBooksService.fetchByExternalId(externalId);
                Book b = new Book();
                b.setExternalId(dto.getExternalId());
                b.setTitle(dto.getTitle());
                b.setAuthors(dto.getAuthors());
                b.setThumbnailUrl(dto.getThumbnailUrl());
                b.setDescription(dto.getDescription());
                return bookRepository.save(b);
            });
        UserBook ub = new UserBook();
        ub.setUser(user);
        ub.setBook(book);
        ub.setShelf(shelf);
        userBookRepository.save(ub);
    }

    public List<UserBook> getShelf(String username, Shelf shelf) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return userBookRepository.findByUserIdAndShelf(user.getId(), shelf);
    }

    public UserBookDto rateBook(String username, RateRequest req) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        UserBook ub = userBookRepository.findById(req.getUserBookId())
            .orElseThrow(() -> new RuntimeException("UserBook not found: " + req.getUserBookId()));
        if (!ub.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot rate other user's book");
        }
        ub.setRating(req.getRating());
        UserBook saved = userBookRepository.save(ub);
        Book b = saved.getBook();
        return new UserBookDto(
            saved.getId(), b.getExternalId(), b.getTitle(), b.getThumbnailUrl(), b.getDescription(),
            saved.getShelf(), saved.getRating(), saved.getAddedAt()
        );
    }
}
