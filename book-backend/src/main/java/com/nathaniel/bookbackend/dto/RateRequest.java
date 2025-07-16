package com.nathaniel.bookbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateRequest {
    private Long userBookId;
    private Integer rating;
}