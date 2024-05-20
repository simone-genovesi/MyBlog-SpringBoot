package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/v0/banner/{campaignId}")
    public ResponseEntity<?> getBanner(@PathVariable String campaignId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bannerService.getBanner(campaignId));
    }
}
