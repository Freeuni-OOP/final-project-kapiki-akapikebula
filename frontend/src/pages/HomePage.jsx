import { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard';
import { getHomeProducts } from '../api/productApi';

function HomePage() {
    const [products, setProducts] = useState([]);
    const [recommendations, setRecommendations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const isUserLoggedIn = !!localStorage.getItem('user');

    useEffect(() => {
        const fetchMainProducts = async () => {
            try {
                setLoading(true);
                setError(null);

                const productsData = await getHomeProducts();
                const productList = Array.isArray(productsData)
                    ? productsData
                    : (productsData?.content || []);


                const normalizedList = productList.map(p => ({
                    ...p,
                    id: p.id || p.productId,
                    productId: p.productId || p.id,
                    lowestPrice: p.lowestPrice || p.minPrice || p.price,
                    minPrice: p.minPrice || p.lowestPrice || p.price
                }));

                setProducts(normalizedList);
            } catch (err) {
                console.error("Error loading home page main data:", err);
                setError("Error loading data. Please try again.");
            } finally {
                setLoading(false);
            }
        };

        const fetchRecommendations = async () => {
            if (!isUserLoggedIn) return;

            try {
                const searchHistory = JSON.parse(localStorage.getItem('searchHistory') || '[]');
                if (searchHistory.length > 0) {
                    const lastSearch = searchHistory[0];

                    const recRes = await fetch(`http://localhost:8080/api/products/search?query=${lastSearch}&size=4`);
                    if (recRes.ok) {
                        const recData = await recRes.json();
                        const recList = recData.content || [];

                        // 🔥 აქაც ნორმალიზაცია, რომ რეკომენდაციებზე დაჭერისას ერორი არ ამოაგდოს
                        const normalizedRecs = recList.map(p => ({
                            ...p,
                            id: p.productId || p.id,
                            productId: p.productId || p.id,
                            lowestPrice: p.lowestPrice || p.minPrice || p.price,
                            minPrice: p.minPrice || p.lowestPrice || p.price
                        }));

                        setRecommendations(normalizedRecs);
                    }
                }
            } catch (err) {
                console.error("Error loading recommendations:", err);
            }
        };

        fetchMainProducts().then(() => {
            fetchRecommendations();
        });

    }, [isUserLoggedIn]);

    if (loading) return <div style={{ textAlign: 'center', marginTop: '50px', color: '#1e293b' }}>Loading products...</div>;
    if (error) return <div style={{ textAlign: 'center', marginTop: '50px', color: '#ef4444' }}>{error}</div>;

    return (
        <div style={styles.container}>

            {/* ✨ AI Recommendations Section */}
            {isUserLoggedIn && recommendations.length > 0 && (
                <div style={{ marginBottom: '50px' }}>
                    <div style={styles.sectionHeader}>
                        <h2 style={styles.sectionTitle}>✨ Recommended for You</h2>
                        <p style={styles.sectionSubtitle}>Based on your recent searches</p>
                    </div>
                    <div style={styles.grid}>
                        {recommendations.map((product) => (
                            <ProductCard key={`rec-${product.id}`} product={product} />
                        ))}
                    </div>
                </div>
            )}

            {/* 🔥 Popular Electronics Section */}
            <div style={styles.sectionHeader}>
                <h2 style={styles.sectionTitle}>Popular Electronics</h2>
                <p style={styles.sectionSubtitle}>Compare prices across top Georgian retailers</p>
            </div>

            <div style={styles.grid}>
                {products && products.length > 0 ? (
                    products.map((product) => (
                        <ProductCard key={`main-${product.id}`} product={product} />
                    ))
                ) : (
                    <p style={{ color: '#64748b' }}>No products found.</p>
                )}
            </div>
        </div>
    );
}

const styles = {
    container: { maxWidth: '1200px', margin: '30px auto', padding: '0 20px', boxSizing: 'border-box' },
    sectionHeader: { marginBottom: '24px' },
    sectionTitle: { margin: '0 0 6px 0', fontSize: '22px', color: '#0f172a', fontWeight: '700' },
    sectionSubtitle: { margin: 0, fontSize: '14px', color: '#64748b' },
    grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '24px' },
};

export default HomePage;