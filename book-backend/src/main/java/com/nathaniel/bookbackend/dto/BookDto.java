package com.nathaniel.bookbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BookDto {
    private String externalId;
    private String title;
    private List<String> authors;
    private String thumbnailUrl;
    private String description;

    public BookDto() {}

    public BookDto(String externalId, String title, List<String> authors, String thumbnailUrl, String description) {
        this.externalId = externalId;
        this.title = title;
        this.authors = authors;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }

}
