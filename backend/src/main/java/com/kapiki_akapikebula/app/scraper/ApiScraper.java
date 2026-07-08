package com.kapiki_akapikebula.app.scraper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiScraper implements Scraper{
    @Override
    public List<StoreListing> search(String query) throws Exception {
        return List.of();
    }

    @Override
    public StoreListing parseProduct(JsonObject p) throws IOException, InterruptedException {
        return null;
    }

    @Override
    public String getCookie(String homepageUrl, String cookieName) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(homepageUrl);

            // DEBUG: Print all cookies to see what the server actually names it
            System.out.println("--- AVAILABLE COOKIES ---");
            for (Cookie c : driver.manage().getCookies()) {
                System.out.println(c.getName() + " = " + c.getValue());
            }
            System.out.println("-------------------------");

            Cookie cookie = driver.manage().getCookieNamed(cookieName);
            return cookie != null ? cookie.getValue() : "";
        } finally {
            driver.quit();
        }
    }

    @Override
    public List<StoreListing> fetchAllPages(String query, String token, String SEARCH_URL, String BASE_URL, String cookieName, HttpClient httpClient) throws Exception {
        List<StoreListing> results = new ArrayList<>();
        String encodedQuery = query.replace(" ", "+");
        int page = 1;
        int totalCount = Integer.MAX_VALUE;

        while (results.size() < totalCount) {
            String url = String.format(SEARCH_URL, encodedQuery, page);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Accept-Encoding", "identity")
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Referer", BASE_URL + "/")
                    .header("Cookie", cookieName + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Request failed — status " + response.statusCode());
                break;
            }

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            if (page == 1) {
                totalCount = root.get("productsCount").getAsInt();
            }

            JsonArray products = root.getAsJsonArray("products");
            if (products == null || products.isEmpty()) break;

            for (JsonElement el : products) {
                JsonObject p = el.getAsJsonObject();
                StoreListing listing = parseProduct(p);
                if (listing != null) results.add(listing);
            }

            page++;
        }

        return results;
    }
}
