package com.swaplio.swaplio_backend.service;

import com.swaplio.swaplio_backend.model.Listing;
import com.swaplio.swaplio_backend.model.ListingImage;
import com.swaplio.swaplio_backend.repository.ListingImageRepository;
import com.swaplio.swaplio_backend.repository.ListingRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanupService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ListingImageRepository listingImageRepository;

    @Autowired
    private StorageService storageService;

    // ✅ Runs every 1 min (testing)
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupSoftDeletedListings() {

//        System.out.println("Running cleanup job...");

        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        List<Listing> oldListings = listingRepository.findOldDeletedListings(cutoff);

        for (Listing listing : oldListings) { // ✅ FIXED

//            System.out.println("Processing listing: " + listing.getId());

            // 🔥 Delete images from Supabase
            for (ListingImage img : listing.getImages()) {
                storageService.deleteFile(img.getImageUrl());
            }

            // 🔥 Delete images from DB
            listingImageRepository.deleteAll(listing.getImages());

            // 🔥 Delete listing from DB
            listingRepository.delete(listing);
        }

//        System.out.println("Cleanup completed: " + oldListings.size());
    }
}