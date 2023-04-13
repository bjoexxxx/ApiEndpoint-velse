package com.example.endpointting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class GenderizeResponse {
    private String name;
    private String gender;
    private Double probability;
}
