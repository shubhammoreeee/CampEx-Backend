package com.swaplio.swaplio_backend.dto.listing;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateListingRequest {

    private String title;
    private String description;
    private BigDecimal price;
    private String condition;
    private Long categoryId;

}