function WatchlistPage({ user }) {
    if (!user) {
        return (
            <div style={{ textAlign: 'center', padding: '80px 20px', color: '#64748b' }}>
                <h2 style={{ color: '#0f172a', marginBottom: '8px' }}>🔒 Protected Page</h2>
                <p>Please log in to view your watchlist.</p>
            </div>
        );
    }

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>⭐ My Watchlist</h2>
                <p style={styles.subtitle}>Your saved products and their price history will appear here.</p>

                <div style={styles.placeholderBox}>
                    <p style={styles.devTitle}> Greetings to Mariam Murghulia from Jaba!</p>
                    <p style={styles.devText}>
                        mzadaa gaakete
                    </p>
                </div>
            </div>
        </div>
    );
}

const styles = {
    container: {
        maxWidth: '1200px',
        margin: '40px auto',
        padding: '0 20px',
    },
    card: {
        backgroundColor: '#ffffff',
        padding: '32px',
        borderRadius: '16px',
        border: '1px solid #e2e8f0',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.02)',
    },
    title: {
        margin: '0 0 6px 0',
        fontSize: '22px',
        color: '#0f172a',
        fontWeight: '700',
    },
    subtitle: {
        margin: '0 0 24px 0',
        fontSize: '14px',
        color: '#64748b',
    },
    placeholderBox: {
        backgroundColor: '#eff6ff',
        border: '1px dashed #bfdbfe',
        borderRadius: '12px',
        padding: '24px',
        textAlign: 'center',
        marginTop: '20px',
    },
    devTitle: {
        color: '#2563eb',
        fontWeight: '700',
        fontSize: '15px',
        margin: '0 0 8px 0',
    },
    devText: {
        color: '#475569',
        fontSize: '14px',
        lineHeight: '1.6',
        margin: 0,
        maxWidth: '600px',
        marginInline: 'auto',
    },
};

export default WatchlistPage;