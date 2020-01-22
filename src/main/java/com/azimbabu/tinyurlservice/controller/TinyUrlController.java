package com.azimbabu.tinyurlservice.controller;

import com.azimbabu.tinyurlservice.dto.TinyUrlRequest;
import com.azimbabu.tinyurlservice.dto.TinyUrlResponse;
import com.azimbabu.tinyurlservice.model.TinyUrl;
import com.azimbabu.tinyurlservice.service.TinyUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/tiny-url")
public class TinyUrlController {

  private TinyUrlService tinyUrlService;

  @Autowired
  public TinyUrlController(TinyUrlService tinyUrlService) {
    this.tinyUrlService = tinyUrlService;
  }

  @PostMapping
  public TinyUrlResponse create(@RequestBody TinyUrlRequest request) {
    TinyUrl tinyUrl =
        tinyUrlService.createTinyUrl(
            request.getOriginalUrl(),
            request.getCustomAlias(),
            request.getUsername(),
            request.getExpirationInSeconds());
    return TinyUrlResponse.builder().data(tinyUrl).success(true).build();
  }

  @GetMapping("/{short-url}")
  public ResponseEntity<TinyUrlResponse> get(@PathVariable("short-url") String shortUrl) {
    Optional<TinyUrl> tinyUrlOptional = tinyUrlService.getTinyUrlByShortUrl(shortUrl);
    if (tinyUrlOptional.isPresent()) {
      TinyUrl tinyUrl = tinyUrlOptional.get();
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.LOCATION, tinyUrl.getOriginalUrl());
      TinyUrlResponse tinyUrlResponse =
          TinyUrlResponse.builder().data(tinyUrl).success(true).build();
      return new ResponseEntity<>(tinyUrlResponse, headers, HttpStatus.FOUND);
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
