package it.cgmconsulting.banner.service;

import it.cgmconsulting.banner.entity.Campaign;
import it.cgmconsulting.banner.entity.Company;
import it.cgmconsulting.banner.repository.CampaignRepository;
import it.cgmconsulting.banner.repository.CompanyRepository;
import it.cgmconsulting.banner.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MainService {

    @Value("${application.image.banner.path}")
    private String path;
    @Value("${application.image.banner.size}")
    private long size;
    @Value("${application.image.banner.height}")
    private int height;
    @Value("${application.image.banner.width}")
    private int width;
    @Value("${application.image.banner.extensions}")
    private String[] extensions;

    private final CompanyRepository companyRepository;
    private final CampaignRepository campaignRepository;
    private final CounterRepository counterRepository;
    private final ImageService imageService;

    public Company addCompany(String companyName) {
        boolean existsCompany = companyRepository.existsByCompanyName(companyName);
        if(!existsCompany)
            return companyRepository.save(
                    Company.builder()
                            .companyName(companyName)
                            .build()
            );
        return null;
    }

    public Optional<Company> getCompany(int id){
        return companyRepository.findById(id);
    }

    public List<Company> getCompanies(int pageNumber, int pageSize, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<Company> companies = companyRepository.findAll(pageable);
        return companies.getContent();
    }

    public Map<Boolean, Object> addCampaign(LocalDate startDate, LocalDate endDate, int companyId, String product, MultipartFile file) {
        Map<Boolean, Object> response = new HashMap<>();

        Optional<Company> company = getCompany(companyId);
        if(company.isEmpty()) {
            response.put(false, "Company not found");
            return response;
        }

        if(endDate.isBefore(startDate)){
            response.put(false, "Campaign end date is before start date");
            return response;
        }

        if(imageService.checkSize(file, size)){
            response.put(false, "Wrong size image");
            return response;
        }

        if(!imageService.checkDimensions(width, height, file)){
            response.put(false, "Wrong image width or height");
            return response;
        }

        if(!imageService.checkExtensions(extensions, file)){
            response.put(false, "Extension not allowed");
            return response;
        }

        Campaign campaign = Campaign.builder()
                .company(company.get())
                .endDate(endDate)
                .image(file.getOriginalFilename())
                .product(product)
                .startDate(startDate)
                .build();
        campaignRepository.save(campaign);

        if(!imageService.uploadImage(file, campaign.getId(), path)){
            response.put(false, "Something went wrong uploading the image");
            return response;
        }

        campaign.setImage(path+campaign.getImage());

        response.put(true, campaign);

        return response;
    }

    public List<Campaign> getCampaigns(int pageNumber, int pageSize, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<Campaign> campaigns = campaignRepository.findAll(pageable);
        return campaigns.getContent();
    }

    public String getBanner(String id) {
        String banner = campaignRepository.getBanner(id, LocalDate.now());
        if(banner != null)
            return path+banner;
        return null;
    }
}
