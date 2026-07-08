package com.kapiki_akapikebula.app.scraper;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;

public interface Scraper {

    List<StoreListing> search(String query) throws Exception;

    StoreListing parseProduct(JsonObject p) throws IOException, InterruptedException;

    String getCookie(String homepageUrl, String cookieName);

    List<StoreListing> fetchAllPages(String query, String token, String SEARCH_URL, String BASE_URL, String cookieName, HttpClient httpClient) throws Exception;
}