package com.swaplio.swaplio_backend.controller;

import com.swaplio.swaplio_backend.dto.listing.CreateListingRequest;
import com.swaplio.swaplio_backend.dto.listing.ListingResponseDTO;
import com.swaplio.swaplio_backend.service.ListingService;
import com.swaplio.swaplio_backend.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired private ListingService listingService;
    @Autowired private JwtUtil jwtUtil;

    // ✅ CREATE LISTING (RETURN DTO)
    @PostMapping
    public ListingResponseDTO createListing(
            @RequestHeader("Authorization") String token,
            @RequestPart("data") CreateListingRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        String email = jwtUtil.extractEmail(token.substring(7));
        return listingService.createListing(email, request, images);
    }

    // ✅ GET ALL LISTINGS (PAGINATION DTO)
    @GetMapping
    public Page<ListingResponseDTO> getListings(Pageable pageable) {
        return listingService.getAllListings(pageable);
    }

    // ✅ GET SINGLE LISTING (DTO)
    @GetMapping("/{id}")
    public ListingResponseDTO getListing(@PathVariable UUID id) {
        return listingService.getListingById(id);
    }

    // ✅ DELETE LISTING
    @DeleteMapping("/{id}")
    public String deleteListing(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtUtil.extractEmail(token.substring(7));
        listingService.deleteListing(id, email);
        return "Listing deleted successfully";
    }
}