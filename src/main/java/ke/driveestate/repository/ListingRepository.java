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

    // Client search — pass "" for empty strings, -1 for unused prices
    @Query("SELECT l FROM Listing l WHERE l.status = 'ACTIVE' AND " +
           "(:type IS NULL OR l.listingType = :type) AND " +
           "(:county = '' OR l.county = :county) AND " +
           "(:minPrice = -1 OR l.price >= :minPrice) AND " +
           "(:maxPrice = -1 OR l.price <= :maxPrice) AND " +
           "(:condition = '' OR l.condition = :condition) AND " +
           "(:fuel = '' OR l.fuelType = :fuel) AND " +
           "(:transmission = '' OR l.transmission = :transmission) AND " +
           "(:zoning = '' OR l.zoning = :zoning) AND " +
           "(:q = '' OR l.title LIKE CONCAT('%',:q,'%') OR " +
           "           l.location LIKE CONCAT('%',:q,'%') OR " +
           "           l.make LIKE CONCAT('%',:q,'%'))")
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

    // Quick search — pass non-null q only
    @Query("SELECT l FROM Listing l WHERE l.status = 'ACTIVE' AND " +
           "(l.title LIKE CONCAT('%',:q,'%') OR l.location LIKE CONCAT('%',:q,'%') OR l.make LIKE CONCAT('%',:q,'%'))")
    List<Listing> quickSearch(@Param("q") String q, Pageable pageable);

    // Admin search — pass "" for unused strings
    @Query("SELECT l FROM Listing l WHERE " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:type IS NULL OR l.listingType = :type) AND " +
           "(:q = '' OR l.title LIKE CONCAT('%',:q,'%') OR l.location LIKE CONCAT('%',:q,'%'))")
    Page<Listing> adminSearch(
        @Param("status") ListingStatus status,
        @Param("type") ListingType type,
        @Param("q") String q,
        Pageable pageable
    );

    @Query("SELECT l.county, COUNT(l) FROM Listing l WHERE l.status = 'ACTIVE' GROUP BY l.county ORDER BY COUNT(l) DESC")
    List<Object[]> getCountyStats();

    @Query("SELECT l.fuelType, COUNT(l) FROM Listing l WHERE l.listingType = 'CAR' AND l.fuelType IS NOT NULL GROUP BY l.fuelType")
    List<Object[]> getFuelStats();

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.listingType = 'CAR' AND l.status = 'ACTIVE' AND l.price < :max AND l.price >= :min")
    long countByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
