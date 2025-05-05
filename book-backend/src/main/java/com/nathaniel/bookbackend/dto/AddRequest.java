package com.nathaniel.bookbackend.dto;

import com.nathaniel.bookbackend.models.Shelf;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRequest {
    private String externalId;
    private Shelf shelf;
}