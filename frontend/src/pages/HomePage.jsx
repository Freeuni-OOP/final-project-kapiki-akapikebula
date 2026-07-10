import { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard';
// 🟢 ამოღებულია PriceChart-ის იმპორტი
import { getHomeProducts } from '../api/productApi'; // 🟢 ამოღებულია getProductPriceHistory

function HomePage() {
    const [products, setProducts] = useState([]);
    // 🟢 ამოღებულია priceHistory სახელმწიფო (state)
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);

                // წამოვიღოთ მხოლოდ დამეჩილი პროდუქტები ჰოუმ ფეიჯისთვის
                const productsData = await getHomeProducts();

                const productList = Array.isArray(productsData) ? productsData : [];
                setProducts(productList);

                // 🟢 ამოღებულია ისტორიის წამოღების ზედმეტი ბლოკი

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
            {/* 🟢 ჩარტის სექცია (chartCard) წარმატებით ამოღებულია აქედან! */}

            {/* Product List Section */}
            <div style={styles.sectionHeader}>
                <h2 style={styles.sectionTitle}>Popular Electronics</h2>
                <p style={styles.sectionSubtitle}>Compare prices across top Georgian retailers</p>
            </div>

            {/* პროდუქტების სექცია */}
            <div style={styles.grid}>
                {products.length > 0 ? (
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
    // 🟢 ჩარტის სტილები ამოღებულია სისუფთავისთვის
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