package com.dummyjson.models.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartProductResponse {
    private Long id;
    private String title;
    private Double price;
    private Integer quantity;
    private Double total;
    private Double discountPercentage;
    private Double discountedTotal;
}
