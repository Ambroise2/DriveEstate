package ke.driveestate.service;

import ke.driveestate.model.*;
import ke.driveestate.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @Transactional
public class FavoriteService {
    private final FavoriteRepository favRepo;
    public FavoriteService(FavoriteRepository favRepo) { this.favRepo = favRepo; }
    public boolean isFavorited(User user, Listing listing) { return favRepo.existsByUserAndListing(user, listing); }
    public boolean toggle(User user, Listing listing) {
        if (favRepo.existsByUserAndListing(user, listing)) { favRepo.deleteByUserAndListing(user, listing); return false; }
        else { favRepo.save(Favorite.builder().user(user).listing(listing).build()); return true; }
    }
    public List<Favorite> getFavorites(User user) { return favRepo.findByUserOrderBySavedAtDesc(user); }
    public long countFavorites(User user) { return favRepo.findByUserOrderBySavedAtDesc(user).size(); }
}
