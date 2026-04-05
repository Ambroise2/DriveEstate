package ke.driveestate.service;

import ke.driveestate.model.*;
import ke.driveestate.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service @Transactional
public class ListingService {

    private final ListingRepository listingRepo;
    private final NotificationRepository notifRepo;

    public ListingService(ListingRepository listingRepo, NotificationRepository notifRepo) {
        this.listingRepo = listingRepo;
        this.notifRepo = notifRepo;
    }

    public Optional<Listing> findById(Long id) { return listingRepo.findById(id); }
    public List<Listing> featuredCars() { return listingRepo.findByStatusAndListingTypeAndFeaturedTrueOrderByCreatedAtDesc(ListingStatus.ACTIVE, ListingType.CAR); }
    public List<Listing> featuredLand() { return listingRepo.findByStatusAndListingTypeAndFeaturedTrueOrderByCreatedAtDesc(ListingStatus.ACTIVE, ListingType.LAND); }
    public List<Listing> newArrivals() { return listingRepo.findTop8ByStatusOrderByCreatedAtDesc(ListingStatus.ACTIVE); }
    public List<Listing> bySellerAll(User seller) { return listingRepo.findBySellerOrderByCreatedAtDesc(seller); }
    public List<Listing> bySellerAndStatus(User seller, ListingStatus status) { return listingRepo.findBySellerAndStatus(seller, status); }

    public Page<Listing> search(ListingType type, String county, BigDecimal minPrice, BigDecimal maxPrice,
            String condition, String fuel, String transmission, String zoning, String q, int page, String sort) {
        Sort s = switch (sort) {
            case "price_asc"  -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "views"      -> Sort.by("views").descending();
            case "oldest"     -> Sort.by("createdAt").ascending();
            default           -> Sort.by("createdAt").descending();
        };
        return listingRepo.searchListings(type, county, minPrice, maxPrice, condition, fuel, transmission, zoning, q, PageRequest.of(page, 12, s));
    }

    public Page<Listing> adminSearch(ListingStatus status, ListingType type, String q, int page) {
        return listingRepo.adminSearch(status, type, q, PageRequest.of(page, 20, Sort.by("createdAt").descending()));
    }

    public List<Listing> quickSearch(String q) { return listingRepo.quickSearch(q, PageRequest.of(0, 8)); }

    public Listing save(Listing listing) { listing.setUpdatedAt(LocalDateTime.now()); return listingRepo.save(listing); }

    public void incrementViews(Long id) {
        listingRepo.findById(id).ifPresent(l -> { l.setViews(l.getViews() + 1); listingRepo.save(l); });
    }

    public void approve(Listing listing, String notes) {
        listing.setStatus(ListingStatus.ACTIVE); listing.setAdminNotes(notes); listingRepo.save(listing);
        notify(listing.getSeller(), "Listing Approved ✅", "Your listing '" + listing.getTitle() + "' is now live!", "success");
    }

    public void reject(Listing listing, String reason) {
        listing.setStatus(ListingStatus.REJECTED); listing.setAdminNotes(reason); listingRepo.save(listing);
        notify(listing.getSeller(), "Listing Not Approved", "Your listing '" + listing.getTitle() + "' was rejected. Reason: " + reason, "danger");
    }

    public void toggleFeatured(Listing listing) { listing.setFeatured(!listing.isFeatured()); listingRepo.save(listing); }
    public void toggleVerified(Listing listing) { listing.setVerified(!listing.isVerified()); listingRepo.save(listing); }

    public void markSold(Listing listing) {
        listing.setStatus(ListingStatus.SOLD); listingRepo.save(listing);
        notify(listing.getSeller(), "Listing Marked as Sold", "Congratulations! '" + listing.getTitle() + "' has been marked as sold.", "success");
    }

    public void delete(Listing listing) { listingRepo.delete(listing); }

    public long countActive()   { return listingRepo.countByStatus(ListingStatus.ACTIVE); }
    public long countPending()  { return listingRepo.countByStatus(ListingStatus.PENDING); }
    public long countCars()     { return listingRepo.countByListingTypeAndStatus(ListingType.CAR, ListingStatus.ACTIVE); }
    public long countLand()     { return listingRepo.countByListingTypeAndStatus(ListingType.LAND, ListingStatus.ACTIVE); }
    public long countFeatured() { return listingRepo.countByFeaturedTrueAndStatus(ListingStatus.ACTIVE); }
    public long countVerified() { return listingRepo.countByVerifiedTrue(); }
    public List<Object[]> countyStats() { return listingRepo.getCountyStats(); }
    public List<Object[]> fuelStats()   { return listingRepo.getFuelStats(); }

    public Map<String, Long> priceRangeStats() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("Under 1M",   listingRepo.countByPriceRange(BigDecimal.ZERO, new BigDecimal("1000000")));
        m.put("1M – 3M",    listingRepo.countByPriceRange(new BigDecimal("1000000"), new BigDecimal("3000000")));
        m.put("3M – 6M",    listingRepo.countByPriceRange(new BigDecimal("3000000"), new BigDecimal("6000000")));
        m.put("6M – 10M",   listingRepo.countByPriceRange(new BigDecimal("6000000"), new BigDecimal("10000000")));
        m.put("Over 10M",   listingRepo.countByPriceRange(new BigDecimal("10000000"), new BigDecimal("999999999")));
        return m;
    }

    private void notify(User user, String title, String msg, String type) {
        notifRepo.save(Notification.builder().user(user).title(title).message(msg).type(type).build());
    }
}
