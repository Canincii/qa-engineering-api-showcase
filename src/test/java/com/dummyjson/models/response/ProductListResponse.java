package com.dummyjson.models.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListResponse {
    private List<ProductResponse> products;
    private Integer total;
    private Integer skip;
    private Integer limit;
}
