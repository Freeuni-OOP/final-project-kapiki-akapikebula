import API from './axios';

// წამოღება — მომხმარებლის watchlist
export const getWatchlist = async () => {
    try {
        const response = await API.get('/watchlist');
        return response.data;
    } catch (error) {
        console.error('Error fetching watchlist:', error);
        throw error;
    }
};

// დამატება — ახალი price alert-ის დამატება watchlist-ში
export const addToWatchlist = async (productId, targetPrice) => {
    try {
        const response = await API.post('/watchlist', {
            productID: productId,
            targetPrice: targetPrice,
        });
        return response.data;
    } catch (error) {
        console.error('Error adding to watchlist:', error);
        throw error;
    }
};

// წაშლა — ტრიგერის გათიშვა
export const removeFromWatchlist = async (alertId) => {
    try {
        const response = await API.delete(`/watchlist/${alertId}`);
        return response.data;
    } catch (error) {
        console.error(`Error deleting alert ${alertId}:`, error);
        throw error;
    }
};