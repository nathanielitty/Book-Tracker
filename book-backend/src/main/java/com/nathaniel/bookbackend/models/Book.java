package com.nathaniel.bookbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name="book")
public class Book {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique=true, nullable=false)
    private String externalId;
    private String title;
    @ElementCollection
    private List<String> authors = new ArrayList<>();
    private String thumbnailUrl;
    @Column(length=2000)
    private String description;

}
