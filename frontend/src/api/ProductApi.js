import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/products';

// 1. პროდუქტების სიის წამოღება (იძახებს /api/products/search-ს)
export const getProducts = async (params = {}) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/search`, { params });
        // Spring Data Page აბრუნებს ობიექტს { content: [...], totalPages: ... }
        // თუ კომპონენტში პირდაპირ მასივი გჭირდება, შეგიძლია დააბრუნო response.data.content
        return response.data;
    } catch (error) {
        console.error('Error fetching products:', error);
        throw error;
    }
};

// 2. ფასების ისტორიის/ლისტინგების წამოღება (იძახებს /api/products/{id}/listings-ს)
export const getProductPriceHistory = async (id) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/${id}/listings`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching listings for product ${id}:`, error);
        throw error;
    }
};

// 3. ძებნის ფუნქცია (სურვილისამებრ)
export const searchProducts = async (query, minPrice, maxPrice, sortBy = 'name', sortDir = 'asc', page = 0, size = 20) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/search`, {
            params: { query, minPrice, maxPrice, sortBy, sortDir, page, size }
        });
        return response.data;
    } catch (error) {
        console.error('Error searching products:', error);
        throw error;
    }
};