import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import PriceChart from '../components/PriceChart';


const getStoreMeta = (shopName) => {
    const name = shopName?.toLowerCase() || '';
    if (name.includes('zoommer') || name.includes('ზუმერი')) {
        return {
            logo: 'https://zoommer.ge/icons/favicon-32x32.png',
            brandColor: '#ef4444',
            bg: '#fef2f2'
        };
    }
    if (name.includes('elite') || name.includes('ელექტრონიქსი') || name.includes('ee')) {
        return {
            logo: 'https://ee.ge/favicon.ico',
            brandColor: '#1d4ed8',
            bg: '#eff6ff'
        };
    }

    return {
        logo: '🏪',
        brandColor: '#475569',
        bg: '#f8fafc'
    };
};

function ProductDetailsPage() {
    const { id } = useParams();
    const [product, setProduct] = useState(null);
    const [offers, setOffers] = useState([]);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [watchlistMsg, setWatchlistMsg] = useState('');

    useEffect(() => {
        const fetchProductDetails = async () => {
            try {
                setLoading(true);

                const productRes = await fetch(`http://localhost:8080/api/products/${id}`);
                if (!productRes.ok) throw new Error('Product not found');
                const productData = await productRes.json();
                setProduct(productData);

                const offersRes = await fetch(`http://localhost:8080/api/products/${id}/listings`);
                if (offersRes.ok) {
                    const offersData = await offersRes.json();
                    setOffers(offersData);
                }

                const historyRes = await fetch(`http://localhost:8080/api/products/${id}/history`);
                if (historyRes.ok) {
                    const historyData = await historyRes.json();
                    const formattedHistory = historyData.map(item => {
                        const date = new Date(item.recordedAt);
                        return {
                            month: date.toLocaleString('en-US', { month: 'short' }),
                            price: item.price
                        };
                    });
                    setHistory(formattedHistory);
                }

            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchProductDetails();
    }, [id]);

    const handleAddToWatchlist = async () => {
        const userStr = localStorage.getItem('user');
        if (!userStr) {
            alert('Please login first to add to your watchlist!');
            return;
        }
        const user = JSON.parse(userStr);
        const currentMinPrice = offers.length > 0 ? Math.min(...offers.map(o => o.price)) : 0;

        try {
            const response = await fetch(`http://localhost:8080/api/watchlist`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${user.token}`
                },
                body: JSON.stringify({
                    productID: parseInt(id),
                    targetPrice: currentMinPrice
                })
            });

            if (response.ok) {
                setWatchlistMsg('✅ Added to Watchlist!');
                setTimeout(() => setWatchlistMsg(''), 3000);
            } else {
                const errData = await response.text();
                setWatchlistMsg(` ${errData || 'Failed to add'}`);
            }
        } catch (err) {
            setWatchlistMsg('Error connecting to server.');
        }
    };

    if (loading) return <div style={styles.center}>Loading product details...</div>;
    if (error) return <div style={styles.centerError}>{error}</div>;
    if (!product) return null;

    return (
        <div style={styles.container}>
            {/* ზედა ქარდი: პროდუქტის ინფო */}
            <div style={styles.headerCard}>
                <div style={styles.imageContainer}>
                    <img src={product.imageUrl} alt={product.name} style={styles.image} />
                </div>
                <div style={styles.infoContainer}>
                    <p style={styles.category}>{product.category?.name || 'Category'}</p>
                    <h1 style={styles.title}>{product.name}</h1>
                    <p style={styles.description}>{product.description}</p>

                    <div style={styles.actionContainer}>
                        <button onClick={handleAddToWatchlist} style={styles.watchlistBtn}>
                            ⭐ Add to Watchlist
                        </button>
                        {watchlistMsg && <span style={styles.watchlistMsg}>{watchlistMsg}</span>}
                    </div>
                </div>
            </div>

            {/* ფასების ისტორიის გრაფიკი */}
            <div style={styles.chartSection}>
                <h2 style={styles.sectionTitle}>Price History</h2>
                {history.length > 0 ? (
                    <PriceChart data={history} />
                ) : (
                    <p style={{ color: '#64748b' }}>No price history available yet.</p>
                )}
            </div>

            {/* 🏪 მაღაზიების განახლებული და გალამაზებული სექცია */}
            <div style={styles.offersSection}>
                <h2 style={styles.sectionTitle}>Available in Stores</h2>
                <div style={styles.offersList}>
                    {offers.length === 0 ? (
                        <p style={{ color: '#64748b', textAlign: 'center', padding: '20px' }}>
                            Currently not available in any tracked stores.
                        </p>
                    ) : (
                        offers.map((offer, index) => {
                            const store = getStoreMeta(offer.shopName);
                            return (
                                <div key={index} style={styles.offerRow}>
                                    {/* მარცხენა მხარე: ლოგო + სახელი */}
                                    <div style={styles.storeBrandBlock}>
                                        {store.logo.startsWith('http') ? (
                                            <img src={store.logo} alt={offer.shopName} style={styles.storeLogo} />
                                        ) : (
                                            <span style={{ fontSize: '20px' }}>{store.logo}</span>
                                        )}
                                        <span style={styles.storeName}>{offer.shopName || 'Store'}</span>
                                    </div>

                                    {/* შუა ნაწილი: მარაგის სტატუსი */}
                                    <div>
                                        <span style={offer.stockStatus === 'IN_STOCK' || offer.stockStatus === 'true' || offer.stockStatus === true ? styles.stockBadge : styles.outOfStockBadge}>
                                            {offer.stockStatus === 'IN_STOCK' || offer.stockStatus === 'true' || offer.stockStatus === true ? 'In Stock' : 'Out of Stock'}
                                        </span>
                                    </div>

                                    {/* მარჯვენა მხარე: ფასი + დინამიური ღილაკი */}
                                    <div style={styles.priceActionBlock}>
                                        <span style={styles.offerPrice}>{offer.price} ₾</span>
                                        <a
                                            href={offer.productUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            style={{
                                                ...styles.buyBtn,
                                                backgroundColor: store.brandColor,
                                                boxShadow: `0 4px 12px ${store.brandColor}33` // ბრენდის ფერის ჩრდილი
                                            }}
                                        >
                                            Go to Shop ↗
                                        </a>
                                    </div>
                                </div>
                            );
                        })
                    )}
                </div>
            </div>
        </div>
    );
}

const styles = {
    container: { maxWidth: '1200px', margin: '40px auto', padding: '0 20px', fontFamily: 'system-ui, sans-serif' },
    center: { textAlign: 'center', padding: '100px 20px', fontSize: '18px', color: '#64748b' },
    centerError: { textAlign: 'center', padding: '100px 20px', fontSize: '18px', color: '#ef4444' },
    headerCard: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '40px',
        backgroundColor: '#ffffff',
        padding: '40px',
        borderRadius: '16px',
        border: '1px solid #e2e8f0',
        marginBottom: '40px',
        boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)'
    },
    imageContainer: {
        flex: '1',
        minWidth: '300px',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f8fafc',
        borderRadius: '12px',
        padding: '20px',
    },
    image: { maxWidth: '100%', maxHeight: '350px', objectFit: 'contain' },
    infoContainer: { flex: '2', minWidth: '300px', display: 'flex', flexDirection: 'column' },
    category: { color: '#2563eb', fontWeight: '700', fontSize: '13px', marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.5px' },
    title: { margin: '0 0 16px 0', fontSize: '32px', color: '#0f172a', fontWeight: '800', lineHeight: '1.2' },
    description: { color: '#475569', fontSize: '16px', lineHeight: '1.6', marginBottom: '30px' },
    actionContainer: { display: 'flex', alignItems: 'center', gap: '15px', marginTop: 'auto' },
    watchlistBtn: {
        backgroundColor: '#f59e0b',
        color: '#ffffff',
        border: 'none',
        padding: '12px 24px',
        borderRadius: '8px',
        fontWeight: '600',
        fontSize: '14px',
        cursor: 'pointer',
        transition: 'background-color 0.2s'
    },
    watchlistMsg: { fontSize: '14px', fontWeight: '500', color: '#10b981' },
    chartSection: { backgroundColor: '#ffffff', padding: '30px', borderRadius: '16px', border: '1px solid #e2e8f0', marginBottom: '40px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' },
    sectionTitle: { fontSize: '20px', fontWeight: '700', color: '#0f172a', margin: '0 0 20px 0' },

    // 🏪 მაღაზიების ახალი სტილები
    offersSection: { backgroundColor: '#ffffff', padding: '30px', borderRadius: '16px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' },
    offersList: { display: 'flex', flexDirection: 'column', gap: '16px' },
    offerRow: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '16px 24px',
        backgroundColor: '#ffffff',
        borderRadius: '12px',
        border: '1px solid #e2e8f0',
        transition: 'transform 0.2s, box-shadow 0.2s',
        flexWrap: 'wrap',
        gap: '16px'
    },
    storeBrandBlock: { display: 'flex', alignItems: 'center', gap: '12px', minWidth: '200px' },
    storeLogo: { width: '28px', height: '28px', borderRadius: '6px', objectFit: 'contain', border: '1px solid #e2e8f0', padding: '2px', backgroundColor: '#fff' },
    storeName: { fontSize: '16px', fontWeight: '600', color: '#1e293b' },
    stockBadge: { backgroundColor: '#dcfce7', color: '#15803d', padding: '6px 12px', borderRadius: '20px', fontSize: '13px', fontWeight: '600' },
    outOfStockBadge: { backgroundColor: '#fee2e2', color: '#b91c1c', padding: '6px 12px', borderRadius: '20px', fontSize: '13px', fontWeight: '600' },
    priceActionBlock: { display: 'flex', alignItems: 'center', gap: '20px' },
    offerPrice: { fontSize: '22px', fontWeight: '850', color: '#0f172a' },
    buyBtn: {
        color: '#ffffff',
        textDecoration: 'none',
        padding: '10px 20px',
        borderRadius: '8px',
        fontWeight: '650',
        fontSize: '14px',
        transition: 'opacity 0.2s ease',
    }
};

export default ProductDetailsPage;