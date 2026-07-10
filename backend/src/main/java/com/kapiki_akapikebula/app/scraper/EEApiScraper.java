package com.kapiki_akapikebula.app.scraper;

import com.google.gson.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Component
public class EEApiScraper extends ApiScraper {

    private static final String STORE_NAME  = "Elite Electronics";
    private static final String BASE_URL    = "https://ee.ge";
    private static final String SEARCH_URL  = "https://ee-api.ee.ge/v1/Products/v3?Name=%s&Limit=24&Page=%d";
    private static final String DETAIL_URL  = "https://ee-api.ee.ge/v1/Products/details?productId=%d";
    private static final String COOKIE_NAME = "elite-access_token";

    @Override
    public List<StoreListing> search(String query) throws Exception {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new", "--disable-gpu", "--no-sandbox",
                "--disable-dev-shm-usage");
        WebDriver driver = new ChromeDriver(opts);

        driver.manage().timeouts().scriptTimeout(java.time.Duration.ofSeconds(60));

        try {
            driver.get(BASE_URL);
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> d.manage().getCookieNamed(COOKIE_NAME) != null);

            String token = extractToken(driver);
            System.out.println("Token acquired");

            List<StoreListing> results = new ArrayList<>();
            String encoded = query.replace(" ", "+");
            int page = 1;

            while (true) {
                String url  = String.format(SEARCH_URL, encoded, page);
                String body = fetchViaJs(driver, url, token);

                JsonObject root     = JsonParser.parseString(body).getAsJsonObject();
                JsonArray  products = root.getAsJsonArray("products");
                if (products == null || products.isEmpty()) break;

                for (JsonElement el : products) {
                    JsonObject p = el.getAsJsonObject();
                    try {
                        results.add(parseSearchResult(driver, token, p));
                    } catch (Exception e) {
                        System.out.println("Skipped product: " + e.getMessage());
                    }
                    Thread.sleep(150);
                }

                int total = root.has("productsCount")
                        ? root.get("productsCount").getAsInt() : 0;
                if (results.size() >= total || products.size() < 24) break;
                page++;
            }

            return results;

        } finally {
            driver.quit();
        }
    }

    private String fetchViaJs(WebDriver driver, String url, String token) {
        String script = """
        var url   = arguments[0];
        var token = arguments[1];
        var done  = arguments[2];
        fetch(url, {
            headers: {
                'Accept':          'application/json, text/plain, */*',
                'Accept-Language': 'ka',
                'Authorization':   'Bearer ' + token,
                'Os':              'web'
            }
        })
        .then(function(r) { return r.text(); })
        .then(function(t) { done(t); })
        .catch(function(e) { done('ERROR:' + e.toString()); });
        """;

        return (String) ((JavascriptExecutor) driver)
                .executeAsyncScript(script, url, token);
    }

    // ── Extract JWT from cookie ───────────────────────────────────────────────

    private String extractToken(WebDriver driver) {
        Cookie c = driver.manage().getCookieNamed(COOKIE_NAME);
        String decoded = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
        return JsonParser.parseString(decoded).getAsJsonObject()
                .get("token").getAsString();
    }

    // ── Parse search result + fetch specs ────────────────────────────────────

    private StoreListing parseSearchResult(WebDriver driver, String token, JsonObject p) {

        int    id    = p.get("id").getAsInt();
        String name  = p.get("name").getAsString();
        double price = p.get("price").getAsDouble();
        Double prev  = p.get("previousPrice").isJsonNull()
                ? null : p.get("previousPrice").getAsDouble();
        String img   = p.get("imageUrl").getAsString();
        String route = BASE_URL + "/" + p.get("routeEn").getAsString();
        boolean inStock = p.get("storageQuantity").getAsInt() > 0;

        // listing.setAttributes(fetchSpecs(driver, token, id));
        return new StoreListing(
                String.valueOf(id), STORE_NAME, name, price, prev, inStock, img, route);
    }

    private Map<String, String> fetchSpecs(WebDriver driver, String token, int id)
            throws Exception {

        String body = fetchViaJs(driver, String.format(DETAIL_URL, id), token);

        for (int attempt = 1; attempt <= 3; attempt++) {
            Thread.sleep(200L * attempt); // back off each retry
            body = fetchViaJs(driver, String.format(DETAIL_URL, id), token);
            if (!body.startsWith("ERROR:")) break;
            System.out.println("Retry " + attempt + " for id=" + id);
        }

        if (body.startsWith("ERROR:")) {
            System.out.println("Spec fetch error for " + id + ": " + body);
            return Collections.emptyMap();
        }

        JsonObject root = JsonParser.parseString(body).getAsJsonObject();
        if (!root.has("product") || root.get("product").isJsonNull())
            return Collections.emptyMap();

        JsonObject product = root.getAsJsonObject("product");
        if (!product.has("specificationGroup") || product.get("specificationGroup").isJsonNull())
            return Collections.emptyMap();

        Map<String, String> attrs = new LinkedHashMap<>();
        for (JsonElement groupEl : product.getAsJsonArray("specificationGroup")) {
            JsonObject group = groupEl.getAsJsonObject();
            if (!group.has("specifications")) continue;
            for (JsonElement specEl : group.getAsJsonArray("specifications")) {
                JsonObject spec = specEl.getAsJsonObject();
                attrs.put(
                        spec.get("specificationName").getAsString().trim(),
                        spec.get("specificationMeaning").getAsString().trim()
                );
            }
        }
        return attrs;
    }

    // ── Test ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        EEApiScraper scraper = new EEApiScraper();
        List<StoreListing> listings = scraper.search("iphone 17 pro");
        System.out.println("Total: " + listings.size() + "\n");
        for (StoreListing s : listings) {
            System.out.println(s);
            s.getAttributes().forEach((k, v) -> System.out.println("  " + k + ": " + v));
            System.out.println();
        }
    }
}