package com.nathaniel.bookbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.nathaniel.bookbackend.dto.BookDto;
import reactor.core.publisher.Mono;
import java.util.*;

@Service
public class GoogleBooksService {
    private final WebClient client;
    private final String apiKey;

    public GoogleBooksService(@Value("${google.books.api-key}") String apiKey) {
        this.client = WebClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.apiKey = apiKey;
    }

    public List<BookDto> search(String query) {
        JsonNode root = client.get()
            .uri(uriBuilder -> uriBuilder.path("/volumes")
                .queryParam("q", query)
                .queryParam("key", apiKey)
                .build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();
        List<BookDto> results = new ArrayList<>();
        if (root != null && root.has("items")) {
            for (JsonNode item : root.get("items")) {
                JsonNode info = item.get("volumeInfo");
                String externalId = item.path("id").asText();
                String title = info.path("title").asText();
                List<String> authors = new ArrayList<>();
                if (info.has("authors")) {
                    for (JsonNode a : info.get("authors")) authors.add(a.asText());
                }
                String thumbnail = info.path("imageLinks").path("thumbnail").asText(null);
                String description = info.path("description").asText("");
                results.add(new BookDto(externalId, title, authors, thumbnail, description));
            }
        }
        return results;
    }

    public BookDto fetchByExternalId(String externalId) {
        JsonNode root = client.get()
            .uri(uriBuilder -> uriBuilder.path("/volumes/" + externalId)
                .queryParam("key", apiKey)
                .build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();
        if (root == null) return null;
        JsonNode info = root.get("volumeInfo");
        String title = info.path("title").asText();
        List<String> authors = new ArrayList<>();
        if (info.has("authors")) {
            for (JsonNode a : info.get("authors")) authors.add(a.asText());
        }
        String thumbnail = info.path("imageLinks").path("thumbnail").asText(null);
        String description = info.path("description").asText("");
        return new BookDto(externalId, title, authors, thumbnail, description);
    }
}
