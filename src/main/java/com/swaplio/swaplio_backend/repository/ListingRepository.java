package com.swaplio.swaplio_backend.repository;

import com.swaplio.swaplio_backend.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, UUID> {

    Page<Listing> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT DISTINCT l FROM Listing l LEFT JOIN FETCH l.images WHERE l.isDeleted = true AND l.deletedAt < :cutoff")
    List<Listing> findOldDeletedListings(@Param("cutoff") LocalDateTime cutoff);

}