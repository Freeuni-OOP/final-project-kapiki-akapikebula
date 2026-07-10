package com.kapiki_akapikebula.app.scraper;

import com.google.gson.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.*;

@Component
public class ZoommerApiScraper extends ApiScraper {

    private static final String STORE_NAME = "Zoommer";
    private static final String BASE_URL = "https://zoommer.ge";
    private static final String SEARCH_URL = BASE_URL + "/api/proxy/v1/Products/v3?Name=%s&Page=%d&Limit=20&NotInStock=true";
    private static final String DETAIL_URL = BASE_URL + "/api/proxy/v1/Products/details?productId=%s&url=%s";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String authCookie;

    @Override
    public List<StoreListing> search(String query) throws Exception {
        this.authCookie = getCookie(BASE_URL, "zoommer-access_token");
        System.out.println("start fetching");
        return fetchAllPages(query, authCookie, SEARCH_URL, BASE_URL, "zoommer-access_token=", httpClient);
    }

    @Override
    public StoreListing parseProduct(JsonObject p) {
        String id           = p.get("id").getAsString();
        String name         = p.get("name").getAsString();
        double price        = p.get("price").getAsDouble();
        Double previousPrice = p.get("previousPrice").isJsonNull()
                ? null : p.get("previousPrice").getAsDouble();
        boolean inStock     = p.get("isInStock").getAsBoolean();
        String imageUrl     = p.get("imageUrl").getAsString();
        String route        = BASE_URL + "/" + p.get("route").getAsString();

        return new StoreListing(
                id, STORE_NAME, name, price, previousPrice, inStock, imageUrl, route
        );
    }

    private Map<String, String> fetchAttributes(String id, String route)
            throws IOException, InterruptedException {
        System.out.println("fetching product: " + id);

        String url = String.format(DETAIL_URL, id, route);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Accept-Encoding", "identity")
                .header("User-Agent", "Mozilla/5.0")
                .header("Referer", BASE_URL + "/")
                .header("Cookie", "zoommer-access_token=" + authCookie)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("Detail fetch failed for id=" + id + " status=" + response.statusCode());
            return Collections.emptyMap();
        }

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!root.has("product") || root.get("product").isJsonNull()) return Collections.emptyMap();

        JsonObject product = root.getAsJsonObject("product");
        if (!product.has("specificationGroup") || product.get("specificationGroup").isJsonNull())
            return Collections.emptyMap();

        Map<String, String> attributes = new LinkedHashMap<>();
        for (JsonElement groupEl : product.getAsJsonArray("specificationGroup")) {
            JsonObject group = groupEl.getAsJsonObject();
            if (!group.has("specifications") || group.get("specifications").isJsonNull()) continue;

            for (JsonElement specEl : group.getAsJsonArray("specifications")) {
                JsonObject spec = specEl.getAsJsonObject();
                String key   = spec.get("specificationName").getAsString().trim();
                String value = spec.get("specificationMeaning").getAsString().trim();
                attributes.put(key, value);
            }
        }
        return attributes;
    }

    public static void main(String[] args) throws Exception {
        ZoommerApiScraper scraper = new ZoommerApiScraper();
        List<StoreListing> listings = scraper.search("iphone 17 pro");
        System.out.println("Found " + listings.size() + " listings\n");

        for (StoreListing s : listings) {
            System.out.println(s);
            s.getAttributes().forEach((k, v) -> System.out.println("  " + k + ": " + v));
            System.out.println();
        }
    }
}