package com.dummyjson.models.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartResponse {
    private Long id;
    private List<CartProductResponse> products;
    private Double total;
    private Double discountedTotal;
    private Long userId;
    private Integer totalProducts;
    private Integer totalQuantity;
}
