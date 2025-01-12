package com.bolt.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorObject {
    private int statusCode;
    private LocalDateTime date;
    private String description;
    private String path;
}
