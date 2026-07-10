import { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard';
import { getWatchlist, removeFromWatchlist } from '../api/WatchlistApi.js';

function WatchlistPage() {
    const [watchlist, setWatchlist] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchWatchlistData = async () => {
            try {
                setLoading(true);
                setError(null);

                const data = await getWatchlist();

                if (data && Array.isArray(data)) {
                    const normalizedData = data.map(item => ({
                        ...item,
                        id: item.productId,
                        name: item.productName,
                        imageUrl: item.productImageUrl,
                        lowestPrice: item.currentPrice,
                        minPrice: item.currentPrice,
                        maxPrice: item.currentPrice
                    }));
                    setWatchlist(normalizedData);
                } else {
                    setWatchlist([]);
                }
            } catch (err) {
                if (err.response && (err.response.status === 401 || err.response.status === 403)) {
                    setError("Please log in again.");
                } else {
                    setError("Could not connect to the server.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchWatchlistData();
    }, []);

    const handleRemoveAlert = async (alertId) => {
        try {
            await removeFromWatchlist(alertId);
            setWatchlist(prev => prev.filter(item => item.alertId !== alertId));
        } catch (err) {
            alert("Failed to remove item.");
        }
    };

    if (loading) return <div style={styles.center}>Loading Watchlist...</div>;
    if (error) return <div style={styles.centerError}>{error}</div>;

    return (
        <div style={styles.container}>
            <h1 style={styles.title}>My Watchlist</h1>

            {watchlist.length === 0 ? (
                <p style={styles.empty}>Your watchlist is empty.</p>
            ) : (
                <div style={styles.grid}>
                    {watchlist.map((item) => (
                        <div key={item.alertId} style={styles.alertWrapper}>
                            <ProductCard product={item} />

                            <div style={styles.alertControlCard}>
                                <div style={styles.infoRow}>
                                    <span style={styles.label}>Target Price:</span>
                                    <span style={styles.targetPriceText}>{item.targetPrice} ₾</span>
                                </div>

                                {item.triggered && (
                                    <div style={styles.triggeredBadge}>
                                        Price dropped below target!
                                    </div>
                                )}

                                <button
                                    onClick={() => handleRemoveAlert(item.alertId)}
                                    style={styles.removeBtn}
                                >
                                    Remove from Watchlist
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

const styles = {
    container: { maxWidth: '1200px', margin: '40px auto', padding: '0 20px', fontFamily: 'system-ui, sans-serif' },
    title: { fontSize: '28px', color: '#0f172a', marginBottom: '24px', fontWeight: '800' },
    center: { textAlign: 'center', marginTop: '50px', color: '#64748b', fontSize: '16px' },
    centerError: { textAlign: 'center', marginTop: '50px', color: '#ef4444', fontSize: '16px' },
    empty: { color: '#64748b', fontSize: '16px' },
    label: { color: '#64748b', fontSize: '14px', fontWeight: '500' },
    grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '30px' },
    alertWrapper: { display: 'flex', flexDirection: 'column', backgroundColor: '#ffffff', borderRadius: '12px', border: '1px solid #e2e8f0', overflow: 'hidden', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)', transition: 'transform 0.2s' },
    alertControlCard: { padding: '16px', backgroundColor: '#f8fafc', borderTop: '1px solid #e2e8f0', display: 'flex', flexDirection: 'column', gap: '12px', marginTop: 'auto' },
    infoRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
    targetPriceText: { fontSize: '16px', fontWeight: '700', color: '#10b981' },
    triggeredBadge: { backgroundColor: '#fef2f2', color: '#ef4444', padding: '8px', borderRadius: '6px', textAlign: 'center', fontWeight: '600', fontSize: '13px' },
    removeBtn: { backgroundColor: '#fee2e2', color: '#ef4444', border: '1px solid #f87171', padding: '10px', borderRadius: '8px', cursor: 'pointer', fontWeight: '600', fontSize: '14px', transition: 'all 0.2s ease', width: '100%' }
};

export default WatchlistPage;