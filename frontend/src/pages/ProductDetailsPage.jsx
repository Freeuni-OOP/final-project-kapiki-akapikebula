import { useParams, Link } from 'react-router-dom';
import PriceChart from '../components/PriceChart';

const dummyProductDetails = {
    id: 1,
    name: 'Apple iPhone 15 Pro Max 256GB Black Titanium',
    category: 'Smartphones',
    description: 'Super Retina XDR display with ProMotion. Aerospace-grade titanium design. A17 Pro chip for next-level gaming performance.',
    imageUrl: 'https://alta.ge/images/thumbnails/900/650/detailed/285/1_1h4w-20.png',
    history: [
        { month: 'Jan', price: 3599 },
        { month: 'Feb', price: 3550 },
        { month: 'Mar', price: 3499 },
        { month: 'Apr', price: 3450 },
        { month: 'May', price: 3350 },
        { month: 'Jun', price: 3299 }
    ],
    offers: [
        { store: 'Alta', price: 3299, inStock: true, url: 'https://alta.ge' },
        { store: 'Elit Electronics', price: 3349, inStock: true, url: 'https://ee.ge' },
        { store: 'Zoommer', price: 3399, inStock: true, url: 'https://zoommer.ge' },
        { store: 'iSpace', price: 3599, inStock: false, url: 'https://ispace.ge' },
    ]
};

function ProductDetailsPage() {
    const { id } = useParams();
    // In real implementation, fetch product by `id` from API
    const product = dummyProductDetails;

    return (
        <div style={styles.container}>
            <Link to="/" style={styles.backLink}>← Back to Products</Link>

            {/* Main Details Grid */}
            <div style={styles.productCard}>
                <div style={styles.imageSection}>
                    <img src={product.imageUrl} alt={product.name} style={styles.image} />
                </div>

                <div style={styles.infoSection}>
                    <span style={styles.category}>{product.category}</span>
                    <h1 style={styles.title}>{product.name}</h1>
                    <p style={styles.description}>{product.description}</p>

                    <div style={styles.priceContainer}>
                        <div>
                            <span style={styles.priceLabel}>Best Available Price</span>
                            <h2 style={styles.bestPrice}>{product.offers[0].price} ₾</h2>
                        </div>
                        <span style={styles.storeBadge}>{product.offers.length} Stores Available</span>
                    </div>
                </div>
            </div>

            {/* Price History Chart */}
            <div style={styles.chartSection}>
                <h3 style={styles.sectionTitle}>Price History Overview</h3>
                <PriceChart data={product.history} />
            </div>

            {/* Retailers Table */}
            <div style={styles.offersSection}>
                <h3 style={styles.sectionTitle}>Compare Store Offers</h3>
                <div style={styles.offersList}>
                    {product.offers.map((offer, index) => (
                        <div key={index} style={styles.offerRow}>
                            <div style={styles.storeName}>{offer.store}</div>
                            <div style={{ ...styles.stockBadge, backgroundColor: offer.inStock ? '#d1fae5' : '#fee2e2', color: offer.inStock ? '#047857' : '#b91c1c' }}>
                                {offer.inStock ? 'In Stock' : 'Out of Stock'}
                            </div>
                            <div style={styles.offerPrice}>{offer.price} ₾</div>
                            <a href={offer.url} target="_blank" rel="noopener noreferrer" style={styles.visitBtn}>
                                Go to Store ↗
                            </a>
                        </div>
                    ))}
                </div>
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
    backLink: {
        display: 'inline-block',
        marginBottom: '20px',
        color: '#2563eb',
        textDecoration: 'none',
        fontWeight: '600',
        fontSize: '14px',
    },
    productCard: {
        backgroundColor: '#ffffff',
        borderRadius: '16px',
        border: '1px solid #e2e8f0',
        padding: '30px',
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
        gap: '40px',
        marginBottom: '30px',
    },
    imageSection: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f8fafc',
        borderRadius: '12px',
        padding: '20px',
        height: '300px',
    },
    image: {
        maxWidth: '100%',
        maxHeight: '100%',
        objectFit: 'contain',
    },
    infoSection: {
        display: 'flex',
        flexDirection: 'column',
    },
    category: {
        color: '#64748b',
        fontSize: '14px',
        fontWeight: '500',
        marginBottom: '8px',
    },
    title: {
        margin: '0 0 16px 0',
        fontSize: '24px',
        color: '#0f172a',
        fontWeight: '700',
    },
    description: {
        color: '#475569',
        fontSize: '14px',
        lineHeight: '1.6',
        marginBottom: '24px',
    },
    priceContainer: {
        marginTop: 'auto',
        padding: '16px',
        backgroundColor: '#f8fafc',
        borderRadius: '12px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    priceLabel: {
        fontSize: '12px',
        color: '#64748b',
        display: 'block',
    },
    bestPrice: {
        margin: 0,
        fontSize: '26px',
        color: '#2563eb',
        fontWeight: '800',
    },
    storeBadge: {
        backgroundColor: '#d1fae5',
        color: '#047857',
        fontSize: '13px',
        fontWeight: '600',
        padding: '6px 12px',
        borderRadius: '20px',
    },
    chartSection: {
        backgroundColor: '#ffffff',
        padding: '24px',
        borderRadius: '16px',
        border: '1px solid #e2e8f0',
        marginBottom: '30px',
    },
    sectionTitle: {
        margin: '0 0 20px 0',
        fontSize: '18px',
        color: '#0f172a',
        fontWeight: '700',
    },
    offersSection: {
        backgroundColor: '#ffffff',
        padding: '24px',
        borderRadius: '16px',
        border: '1px solid #e2e8f0',
    },
    offersList: {
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
    },
    offerRow: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '14px 18px',
        border: '1px solid #f1f5f9',
        borderRadius: '10px',
        backgroundColor: '#fafafa',
    },
    storeName: {
        fontWeight: '600',
        color: '#1e293b',
        flex: 1,
    },
    stockBadge: {
        padding: '4px 10px',
        borderRadius: '12px',
        fontSize: '12px',
        fontWeight: '600',
        marginRight: '20px',
    },
    offerPrice: {
        fontSize: '18px',
        fontWeight: '700',
        color: '#0f172a',
        marginRight: '20px',
    },
    visitBtn: {
        backgroundColor: '#2563eb',
        color: '#ffffff',
        textDecoration: 'none',
        padding: '8px 14px',
        borderRadius: '6px',
        fontSize: '13px',
        fontWeight: '600',
    },
};

export default ProductDetailsPage;