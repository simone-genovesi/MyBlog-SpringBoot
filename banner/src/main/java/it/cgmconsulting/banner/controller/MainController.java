package it.cgmconsulting.banner.controller;

import it.cgmconsulting.banner.entity.Company;
import it.cgmconsulting.banner.service.MainService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
public class MainController {

    private final MainService mainService;

    // API per le chiamate esterne

    // POST/GET per le entità Company Campaign

    /**
     * COMPANY CONTROLLERS
     */

    @PostMapping("/company")
    public ResponseEntity<?> addCompany(@RequestParam @NotBlank @Size(min = 1, max = 255) String companyName){
//        return new ResponseEntity<>(mainService.addCompany(companyName), HttpStatus.CREATED);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mainService.addCompany(companyName));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<?> getCompany(@PathVariable int id){
        Optional<Company> company = mainService.getCompany(id);
        if(company.isPresent())
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(company);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Company not found");
    }

    @GetMapping("/companies")
    public ResponseEntity<?> getCompanies(
            @RequestParam(defaultValue = "0") int pageNumber, // numero di pagina da cui partire
            @RequestParam(defaultValue = "10") int pageSize, // numero di elementi per pagina
            @RequestParam(defaultValue = "companyName") String sortBy, // indica la colonna su cui eseguire l'ordinamento
            @RequestParam(defaultValue = "ASC") String direction // indica se l'ordinamento è ASC o DESC
    ){
        //elenco di company paginato
        List<Company> list = mainService.getCompanies(pageNumber, pageSize, sortBy, direction);
        if(list.isEmpty())
            return new ResponseEntity<>("no companies found", HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * CAMPAIGN CONTROLLERS
     */

    @PostMapping("/campaigns")
    public ResponseEntity<?> addCampaign(
            @RequestParam @NotNull @FutureOrPresent LocalDate startDate,
            @RequestParam @NotNull @FutureOrPresent LocalDate endDate,
            @RequestParam @Min(1) int companyId,
            @RequestParam @NotBlank @Size(max = 255, min=1) String product,
            @RequestParam MultipartFile file
    ){
        Map<Boolean, Object> response = mainService.addCampaign(startDate, endDate, companyId, product, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}