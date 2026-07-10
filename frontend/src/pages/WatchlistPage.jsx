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
                setError(null); // ყოველი შემთხვევისთვის ვასუფთავებთ ერორს ჩატვირთვისას

                const data = await getWatchlist();

                // 🟢 მთავარი დაზღვევა: ვამოწმებთ, რომ მონაცემები ნამდვილად მასივია
                if (data && Array.isArray(data)) {
                    setWatchlist(data);
                } else {
                    setWatchlist([]);
                }
            } catch (err) {
                console.error("Error loading watchlist:", err);

                // 🟢 ვამოწმებთ, კონკრეტულად რამ გამოიწვია შეცდომა Axios-ში
                if (err.response && (err.response.status === 401 || err.response.status === 403)) {
                    setError("გთხოვთ გაიაროთ ავტორიზაცია ხელახლა (სესია ამოიწურა).");
                } else {
                    setError("სერვერთან კავშირი ვერ დამყარდა ან მოხდა გაუგებრობა.");
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
            alert("ტრიგერის გათიშვა ვერ მოხერხდა.");
        }
    };

    if (loading) {
        return <div style={{ textAlign: 'center', marginTop: '50px', color: '#1e293b' }}>Loading Watchlist...</div>;
    }

    return (
        <div style={styles.container}>
            <div style={styles.sectionHeader}>
                <h2 style={styles.sectionTitle}>My Price Alerts</h2>
                <p style={styles.sectionSubtitle}>პროდუქტები, რომელთა ფასის ცვლილებასაც ელოდები</p>
            </div>

            {/* 🔴 თუ რეალური ერორია (მაგ. 401), მხოლოდ მაშინ გამოვაჩენთ წითელ შეტყობინებას */}
            {error ? (
                <div style={{ textAlign: 'center', color: 'red', marginTop: '30px', fontSize: '16px' }}>
                    {error}
                </div>
            ) : (
                <div style={styles.grid}>
                    {watchlist.length > 0 ? (
                        watchlist.map((item) => (
                            <div key={item.alertId} style={styles.alertWrapper}>
                                <ProductCard
                                    product={{
                                        id: item.productId,
                                        name: item.productName,
                                        imageUrl: item.productImageUrl,
                                        price: item.currentPrice
                                    }}
                                />

                                <div style={styles.alertControlCard}>
                                    <div style={styles.infoRow}>
                                        <span style={{ color: '#64748b', fontSize: '13px' }}>Target Price:</span>
                                        <span style={styles.targetPriceText}>{item.targetPrice} ₾</span>
                                    </div>

                                    {item.triggered && (
                                        <div style={styles.triggeredBadge}>🔥 ფასი დაეცა!</div>
                                    )}

                                    <button
                                        style={styles.disableBtn}
                                        onClick={() => handleRemoveAlert(item.alertId)}
                                    >
                                        გათიშვა (წაშლა)
                                    </button>
                                </div>
                            </div>
                        ))
                    ) : (
                        /* 🟢 აი ეს ტექსტი გამოჩნდება მაშინ, როცა ბაზა სუფთაა და შეცდომა არ არის! */
                        <p style={{ color: '#64748b', gridColumn: '1/-1', textAlign: 'center', marginTop: '20px', fontSize: '15px' }}>
                            You have not added anything to Watchlist. შენი ვოჩლისტი ცარიელია. აქტიური ტრიგერები არ გაქვს ჩართული.
                        </p>
                    )}
                </div>
            )}
        </div>
    );
}

const styles = {
    container: {
        maxWidth: '1200px',
        margin: '30px auto',
        padding: '0 20px',
        boxSizing: 'border-box',
    },
    sectionHeader: {
        marginBottom: '30px',
        borderBottom: '1px solid #e2e8f0',
        paddingBottom: '16px'
    },
    sectionTitle: {
        margin: '0 0 6px 0',
        fontSize: '24px',
        color: '#0f172a',
        fontWeight: '700',
    },
    sectionSubtitle: {
        margin: 0,
        fontSize: '14px',
        color: '#64748b',
    },
    grid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
        gap: '30px',
    },
    alertWrapper: {
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: '#ffffff',
        borderRadius: '12px',
        border: '1px solid #e2e8f0',
        overflow: 'hidden',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.01)',
    },
    alertControlCard: {
        padding: '16px',
        backgroundColor: '#f8fafc',
        borderTop: '1px solid #e2e8f0',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px'
    },
    infoRow: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
    },
    targetPriceText: {
        fontSize: '16px',
        fontWeight: '700',
        color: '#10b981'
    },
    triggeredBadge: {
        backgroundColor: '#fef2f2',
        color: '#ef4444',
        padding: '6px',
        borderRadius: '6px',
        textAlign: 'center',
        fontSize: '12px',
        fontWeight: '600',
        border: '1px solid #fee2e2'
    },
    disableBtn: {
        backgroundColor: '#ef4444',
        color: '#ffffff',
        border: 'none',
        padding: '8px 12px',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '13px',
        fontWeight: '600',
        transition: 'background-color 0.2s',
    }
};

export default WatchlistPage;
