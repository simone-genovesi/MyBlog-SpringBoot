package it.cgmconsulting.banner.service;

import it.cgmconsulting.banner.entity.Company;
import it.cgmconsulting.banner.repository.CampaignRepository;
import it.cgmconsulting.banner.repository.CompanyRepository;
import it.cgmconsulting.banner.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainService {

    private final CampaignRepository campaignRepository;
    private final CompanyRepository companyRepository;
    private final CounterRepository counterRepository;

    public Company addCompany(String companyName) {
        return companyRepository.save(
                Company.builder()
                        .companyName(companyName)
                        .build()
        );
    }

    public Optional<Company> getCompany(int id){
        return companyRepository.findById(id);
    }

    public List<Company> getCompanies(int pageNumber, int pageSize, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<Company> companies = companyRepository.findAll(pageable);
        List<Company> companyResponses = new ArrayList<>();
        if(companies.hasContent())
            companyResponses = companies.getContent();
        return companyResponses;
    }
}
