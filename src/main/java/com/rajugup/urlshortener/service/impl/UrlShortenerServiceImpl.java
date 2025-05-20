package com.rajugup.urlshortener.service.impl;

import com.rajugup.urlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final Logger log = LoggerFactory.getLogger(UrlShortenerServiceImpl.class);

    private final ConcurrentHashMap<String, String> shortToUrl = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> urlToShort = new ConcurrentHashMap<>();
    private final String filePath = "https://github.com/rranjangupta/UrlShortnerService/blob/main/shortened-urls.txt"


    public UrlShortenerServiceImpl() {

        log.info("Initializing UrlShortenerServiceImpl...");
        loadFromFile();
    }

    @Override
    public String shortenUrl(String originalUrl) {
        if (urlToShort.containsKey(originalUrl)) {
            return urlToShort.get(originalUrl);
        }

        String hashString = Integer.toHexString(originalUrl.hashCode());
        shortToUrl.put(hashString, originalUrl);
        urlToShort.put(originalUrl, hashString);

        log.info("Saving shortened URL: {} -> {}", hashString, originalUrl);
        saveToFile();
        return hashString;
    }

    @Override
    public String getOriginalUrl(String hash) {
        return shortToUrl.get(hash);
    }

    private void saveToFile() {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            log.info("Writing to file: {}", file.getAbsolutePath());

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (var entry : shortToUrl.entrySet()) {
                    writer.println(entry.getKey() + "=" + entry.getValue());
                }
            }
        } catch (IOException e) {
            log.error("Error saving to file: {}", e.getMessage(), e);
        }
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    shortToUrl.put(parts[0], parts[1]);
                    urlToShort.put(parts[1], parts[0]);
                }
            }

            log.info("Loaded {} URLs from file.", shortToUrl.size());
        } catch (IOException e) {
            log.error("Error loading from file: {}", e.getMessage(), e);
        }
    }
}
