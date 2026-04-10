package com.swaplio.swaplio_backend.repository;

import com.swaplio.swaplio_backend.model.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
}