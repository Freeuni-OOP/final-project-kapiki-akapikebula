import { useNavigate } from 'react-router-dom';

function ProductCard({ product }) {
    const navigate = useNavigate();

    // ფუნქცია, რომელიც ნებისმიერ კლიკზე გადაგვიყვანს პროდუქტის გვერდზე
    const handleCardClick = () => {
        navigate(`/product/${product.id}`);
    };

    return (
        // 🟢 onClick-ის დახმარებით მთლიანი div გახდა აქტიური ლინკი
        <div onClick={handleCardClick} style={styles.card}>
            <div style={styles.imageContainer}>
                <img src={product.imageUrl} alt={product.name} style={styles.image} />
            </div>

            <div style={styles.info}>
                <h3 style={styles.title}>{product.name}</h3>
                <p style={styles.category}>{product.category || 'Gadget'}</p>

                <div style={styles.priceSection}>
                    <div>
                        <p style={styles.priceLabel}>Price:</p>
                        <p style={styles.price}>
                            {product.minPrice} ₾ - {product.maxPrice} ₾
                        </p>
                    </div>
                    <p style={styles.storeCount}>{product.storesCount} Stores</p>
                </div>

                {/* 🟢 ეს არის ჩვეულებრივი ლამაზი დივ-ღილაკი */}
                <div style={styles.button}>
                    Compare Prices
                </div>
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
        height: '100%',
        boxSizing: 'border-box',
        cursor: 'pointer', // 👈 მაუსის მიტანისას მთლიან ქარდზე გამოჩნდება ხელის სიმბოლო (Pointer)
    },
    imageContainer: {
        height: '200px',
        padding: '20px',
        backgroundColor: '#f8fafc',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
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
        fontSize: '13px',
        color: '#64748b',
        fontWeight: '500',
    },
    button: {
        display: 'block',
        width: '100%',
        padding: '10px 0',
        backgroundColor: '#f1f5f9',
        color: '#0f172a',
        textAlign: 'center',
        borderRadius: '8px',
        fontWeight: '600',
        fontSize: '14px',
        transition: 'background-color 0.2s ease, color 0.2s ease',
        boxSizing: 'border-box',
    },
};

export default ProductCard;