package com.swaplio.swaplio_backend.service;

import com.swaplio.swaplio_backend.dto.listing.*;
import com.swaplio.swaplio_backend.model.*;
import com.swaplio.swaplio_backend.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ListingService {

    @Autowired private ListingRepository listingRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ListingImageRepository listingImageRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StorageService storageService;

    // ✅ FIX 1: RETURN DTO
    public ListingResponseDTO createListing(String email, CreateListingRequest request, List<MultipartFile> images) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Listing listing = Listing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .condition(request.getCondition())
                .seller(user)
                .category(category)
                .build();

        Listing saved = listingRepository.save(listing);

        if (images != null) {
            for (MultipartFile file : images) {
                String url = storageService.uploadFile(file);

                ListingImage img = ListingImage.builder()
                        .imageUrl(url)
                        .listing(saved)
                        .build();

                listingImageRepository.save(img);
            }
        }

        return mapToDTO(saved);
    }

    // ✅ FIX 2: PAGE DTO
    public Page<ListingResponseDTO> getAllListings(Pageable pageable) {
        return listingRepository.findByIsDeletedFalse(pageable)
                .map(this::mapToDTO);
    }

    // ✅ FIX 3: RETURN DTO
    public ListingResponseDTO getListingById(UUID id) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        return mapToDTO(listing);
    }

    // ✅ FIX 4: USE ENTITY (NOT DTO)
    public void deleteListing(UUID id, String email) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        listing.setDeleted(true);
        listing.setDeletedAt(LocalDateTime.now());

        listingRepository.save(listing);
    }

    // ✅ FIX 5: MAPPER METHOD
    private ListingResponseDTO mapToDTO(Listing listing) {

        ListingResponseDTO dto = new ListingResponseDTO();

        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription()); // ✅ NEW
        dto.setPrice(listing.getPrice());
        dto.setCondition(listing.getCondition()); // ✅ ENUM → STRING

        // ✅ Category
        dto.setCategoryName(
                listing.getCategory() != null
                        ? listing.getCategory().getName()
                        : null
        );

        // ✅ Seller
        dto.setSellerName(
                listing.getSeller() != null
                        ? listing.getSeller().getName()
                        : null
        );

        dto.setSellerImage(
                listing.getSeller() != null
                        ? listing.getSeller().getImageUrl()
                        : null
        );

        // ✅ Images
        dto.setImageUrls(
                listing.getImages() != null
                        ? listing.getImages()
                          .stream()
                          .map(ListingImage::getImageUrl)
                          .toList()
                        : List.of()   // empty list instead of crash
        );

        return dto;
    }
}