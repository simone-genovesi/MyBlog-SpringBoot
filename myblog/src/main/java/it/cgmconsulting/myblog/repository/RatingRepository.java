package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Rating;
import it.cgmconsulting.myblog.entity.RatingId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM rating " +
            "WHERE post_id = :postId " +
            "AND user_id = :userId", nativeQuery = true)
    void deleteRating(int postId, int userId);

    @Query(value = "SELECT COALESCE(" +
            "(SELECT rate FROM rating " +
            "WHERE post_id = :postId " +
            "AND user_id = :userId), 0)", nativeQuery = true)
    byte getMyRating(int postId, int userId);

/*
    @Query(value = "SELECT rate FROM rating " +
            "WHERE post_id = :postId " +
            "AND user_id = :userId", nativeQuery = true)
    Optional<Byte> getMyRating(int postId, int userId);
*/

}
