package com.rajugup.urlshortener.service;

public interface UrlShortenerService {
    String shortenUrl(String originalUrl);
    String getOriginalUrl(String hash);
}
