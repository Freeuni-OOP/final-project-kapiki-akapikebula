import { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard';
import PriceChart from '../components/PriceChart';
import { getProducts, getProductPriceHistory } from '../api/productApi';

function HomePage() {
    const [products, setProducts] = useState([]);
    const [priceHistory, setPriceHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);


                const productsData = await getProducts();


                const productList = Array.isArray(productsData)
                    ? productsData
                    : (productsData?.content || []);

                setProducts(productList);


                if (productList.length > 0) {
                    try {
                        const firstProductId = productList[0].productId;
                        const historyData = await getProductPriceHistory(firstProductId);
                        setPriceHistory(historyData || []);
                    } catch (historyErr) {
                        console.warn("Price history error, but products loaded fine:", historyErr);
                        setPriceHistory([]);
                    }
                }

            } catch (err) {
                console.error("Error loading home page data:", err);
                setError("Error loading data.");
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return <div style={{ textAlign: 'center', marginTop: '50px', color: '#1e293b' }}>Loading ...</div>;
    }

    if (error) {
        return <div style={{ textAlign: 'center', color: 'red', marginTop: '50px' }}>{error}</div>;
    }

    return (
        <div style={styles.container}>
            {/* Hero / Price History Card */}
            <div style={styles.chartCard}>
                <div style={styles.chartHeader}>
                    <h2 style={styles.chartTitle}>Market Price Trend (Market Overview)</h2>
                    <span style={styles.badge}>Live Data</span>
                </div>
                <PriceChart data={priceHistory} />
            </div>

            {/* Product List Section */}
            <div style={styles.sectionHeader}>
                <h2 style={styles.sectionTitle}>Popular Electronics</h2>
                <p style={styles.sectionSubtitle}>Compare prices across top Georgian retailers</p>
            </div>

            {/* 🟢 დაზღვევა: .map() გამოიძახება მხოლოდ მაშინ, თუ products მართლაც მასივია */}
            <div style={styles.grid}>
                {Array.isArray(products) && products.length > 0 ? (
                    products.map((product) => (
                        <ProductCard key={product.id} product={product} />
                    ))
                ) : (
                    <p style={{ color: '#64748b' }}>No products found.</p>
                )}
            </div>
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
    chartCard: {
        backgroundColor: '#ffffff',
        padding: '24px',
        borderRadius: '12px',
        border: '1px solid #e2e8f0',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.02)',
        marginBottom: '40px',
    },
    chartHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '20px',
    },
    chartTitle: {
        margin: 0,
        fontSize: '20px',
        color: '#1e293b',
        fontWeight: '600',
    },
    badge: {
        backgroundColor: '#eff6ff',
        color: '#2563eb',
        padding: '4px 10px',
        borderRadius: '12px',
        fontSize: '12px',
        fontWeight: '600',
    },
    sectionHeader: {
        marginBottom: '24px',
    },
    sectionTitle: {
        margin: '0 0 6px 0',
        fontSize: '22px',
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
        gap: '24px',
    },
};

export default HomePage;