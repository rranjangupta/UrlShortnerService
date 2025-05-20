package com.rajugup.urlshortener.controller;

import com.rajugup.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService service;

    @GetMapping("/short/**")
    public String shorten(HttpServletRequest request) {
        String url = request.getRequestURI().substring("/short/".length());
        String shortCode = service.shortenUrl(url);
        return "shortLink/" + shortCode;
    }


    @GetMapping("/shortLink/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {
        String originalUrl = service.getOriginalUrl(code);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }
}
