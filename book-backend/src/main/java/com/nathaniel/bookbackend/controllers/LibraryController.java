package com.nathaniel.bookbackend.controllers;

import com.nathaniel.bookbackend.dto.BookDto;
import com.nathaniel.bookbackend.models.UserBook;
import com.nathaniel.bookbackend.services.LibraryService;
import com.nathaniel.bookbackend.dto.AddRequest;
import com.nathaniel.bookbackend.models.Shelf;
import com.nathaniel.bookbackend.dto.RateRequest;
import com.nathaniel.bookbackend.dto.UserBookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libSvc;

    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> search(@RequestParam String q) {
        return ResponseEntity.ok(libSvc.searchBooks(q));
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AddRequest req, Principal user) {
        libSvc.addToShelf(user.getName(), req.getExternalId(), req.getShelf());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shelf}")
    public ResponseEntity<List<UserBookDto>> list(@PathVariable Shelf shelf, Principal user) {
        List<UserBook> ubList = libSvc.getShelf(user.getName(), shelf);
        List<UserBookDto> dtos = ubList.stream().map(ub -> new UserBookDto(
            ub.getId(),
            ub.getBook().getExternalId(),
            ub.getBook().getTitle(),
            ub.getBook().getThumbnailUrl(),
            ub.getBook().getDescription(),
            ub.getShelf(),
            ub.getRating(),
            ub.getAddedAt()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/rate")
    public ResponseEntity<UserBookDto> rate(@RequestBody RateRequest req, Principal user) {
        UserBookDto dto = libSvc.rateBook(user.getName(), req);
        return ResponseEntity.ok(dto);
    }
}
