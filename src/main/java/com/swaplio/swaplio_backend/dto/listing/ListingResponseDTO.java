package com.swaplio.swaplio_backend.dto.listing;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ListingResponseDTO {

    private UUID id;
    private String title;
    private String description;   // ✅ ADD
    private BigDecimal price;
    private String condition;     // ✅ ADD

    private String categoryName;  // ✅ ADD

    private String sellerName;    // ✅ ADD
    private String sellerImage;   // ✅ ADD

    private List<String> imageUrls;
}