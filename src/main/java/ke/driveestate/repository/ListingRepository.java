package ke.driveestate.repository;

import ke.driveestate.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByStatusAndFeaturedTrueOrderByCreatedAtDesc(ListingStatus status);
    List<Listing> findByStatusAndListingTypeAndFeaturedTrueOrderByCreatedAtDesc(ListingStatus status, ListingType type);
    List<Listing> findTop8ByStatusOrderByCreatedAtDesc(ListingStatus status);
    List<Listing> findBySellerOrderByCreatedAtDesc(User seller);
    List<Listing> findBySellerAndStatus(User seller, ListingStatus status);

    long countByStatus(ListingStatus status);
    long countByListingTypeAndStatus(ListingType type, ListingStatus status);
    long countByFeaturedTrueAndStatus(ListingStatus status);
    long countByVerifiedTrue();

    @Query("SELECT l FROM Listing l WHERE l.status = 'ACTIVE' AND " +
           "(:type IS NULL OR l.listingType = :type) AND " +
           "(:county IS NULL OR l.county = :county) AND " +
           "(:minPrice IS NULL OR l.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR l.price <= :maxPrice) AND " +
           "(:condition IS NULL OR l.condition = :condition) AND " +
           "(:fuel IS NULL OR l.fuelType = :fuel) AND " +
           "(:transmission IS NULL OR l.transmission = :transmission) AND " +
           "(:zoning IS NULL OR l.zoning = :zoning) AND " +
           "(:q IS NULL OR l.title LIKE CONCAT('%', :q, '%') OR " +
           " l.location LIKE CONCAT('%', :q, '%') OR " +
           " l.make LIKE CONCAT('%', :q, '%'))")
    Page<Listing> searchListings(
        @Param("type") ListingType type,
        @Param("county") String county,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("condition") String condition,
        @Param("fuel") String fuel,
        @Param("transmission") String transmission,
        @Param("zoning") String zoning,
        @Param("q") String q,
        Pageable pageable
    );

    @Query("SELECT l FROM Listing l WHERE l.title LIKE CONCAT('%', :q, '%') AND l.status = 'ACTIVE' ORDER BY l.views DESC")
    List<Listing> quickSearch(@Param("q") String q, Pageable pageable);

    // Admin search
    @Query(value = "SELECT * FROM listings l WHERE " +
           "(:status IS NULL OR l.status = CAST(:status AS VARCHAR)) AND " +
           "(:type IS NULL OR l.listing_type = CAST(:type AS VARCHAR)) AND " +
           "(:q IS NULL OR l.title ILIKE CONCAT('%', :q, '%'))",
           countQuery = "SELECT COUNT(*) FROM listings l WHERE " +
           "(:status IS NULL OR l.status = CAST(:status AS VARCHAR)) AND " +
           "(:type IS NULL OR l.listing_type = CAST(:type AS VARCHAR)) AND " +
           "(:q IS NULL OR l.title ILIKE CONCAT('%', :q, '%'))",
           nativeQuery = true)
    Page<Listing> adminSearch(
        @Param("status") String status,
        @Param("type") String type,
        @Param("q") String q,
        Pageable pageable
    );

    // County stats
    @Query("SELECT l.county, COUNT(l) FROM Listing l WHERE l.status = 'ACTIVE' GROUP BY l.county ORDER BY COUNT(l) DESC")
    List<Object[]> getCountyStats();

    // Fuel stats
    @Query("SELECT l.fuelType, COUNT(l) FROM Listing l WHERE l.listingType = 'CAR' AND l.fuelType IS NOT NULL GROUP BY l.fuelType")
    List<Object[]> getFuelStats();

    // Price range counts
    @Query("SELECT COUNT(l) FROM Listing l WHERE l.listingType = 'CAR' AND l.status = 'ACTIVE' AND l.price < :max AND l.price >= :min")
    long countByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
