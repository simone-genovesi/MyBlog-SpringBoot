package it.cgmconsulting.banner.repository;

import it.cgmconsulting.banner.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
}
