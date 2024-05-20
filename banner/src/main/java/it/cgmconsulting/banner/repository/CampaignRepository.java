package it.cgmconsulting.banner.repository;

import it.cgmconsulting.banner.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface CampaignRepository extends JpaRepository<Campaign, String> {

    @Query(value = "SELECT c.image " +
            "FROM Campaign c " +
            "WHERE c.id = :id " +
            "AND :now BETWEEN c.startDate AND c.endDate")
    String getBanner(String id, LocalDate now);
}
