import { Link } from 'react-router-dom';

function ProductCard({ product }) {
    return (
        <div style={styles.card}>
            <div style={styles.imageContainer}>
                <img src={product.imageUrl} alt={product.name} style={styles.image} />
            </div>

            <div style={styles.info}>
                <h3 style={styles.title}>{product.name}</h3>
                <p style={styles.category}>{product.category}</p>

                <div style={styles.priceSection}>
                    <div>
                        <p style={styles.priceLabel}>Price:</p>
                        <p style={styles.price}>
                            {product.minPrice} ₾ - {product.maxPrice} ₾
                        </p>
                    </div>
                    <p style={styles.storeCount}>{product.storesCount} Stores</p>
                </div>

                <Link to={`/product/${product.id}`} style={styles.button}>
                    Compare Prices
                </Link>
            </div>
        </div>
    );
}

const styles = {
    card: {
        backgroundColor: '#ffffff',
        borderRadius: '12px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.05)',
        overflow: 'hidden',
        transition: 'transform 0.2s ease, box-shadow 0.2s ease',
        display: 'flex',
        flexDirection: 'column',
        cursor: 'pointer',
        border: '1px solid #e2e8f0',
    },
    imageContainer: {
        width: '100%',
        height: '200px',
        backgroundColor: '#f8fafc',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        padding: '20px',
        boxSizing: 'border-box',
    },
    image: {
        maxWidth: '100%',
        maxHeight: '100%',
        objectFit: 'contain',
    },
    info: {
        padding: '16px',
        display: 'flex',
        flexDirection: 'column',
        flex: 1,
    },
    title: {
        margin: '0 0 8px 0',
        fontSize: '16px',
        color: '#0f172a',
        fontWeight: '600',
        lineHeight: '1.4',
    },
    category: {
        margin: '0 0 16px 0',
        fontSize: '13px',
        color: '#64748b',
    },
    priceSection: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-end',
        marginTop: 'auto',
        marginBottom: '16px',
    },
    priceLabel: {
        margin: 0,
        fontSize: '12px',
        color: '#64748b',
    },
    price: {
        margin: 0,
        fontSize: '18px',
        fontWeight: 'bold',
        color: '#2563eb',
    },
    storeCount: {
        margin: 0,
        fontSize: '12px',
        color: '#10b981',
        fontWeight: '600',
        backgroundColor: '#d1fae5',
        padding: '4px 8px',
        borderRadius: '12px',
    },
    button: {
        display: 'block',
        textAlign: 'center',
        backgroundColor: '#2563eb',
        color: '#ffffff',
        textDecoration: 'none',
        padding: '10px 0',
        borderRadius: '8px',
        fontWeight: '600',
        fontSize: '14px',
    },
};

export default ProductCard;