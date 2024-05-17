package it.cgmconsulting.banner.repository;

import it.cgmconsulting.banner.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
