import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';


function Navbar() {
    const [searchTerm, setSearchTerm] = useState('');
    const navigate = useNavigate();

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchTerm.trim()) {
            navigate(`/?search=${encodeURIComponent(searchTerm)}`);
        }
    };

    return (
        <header style={styles.header}>
            <div style={styles.container}>
                <Link to="/" style={styles.logo}>
                    🪙 <span style={{ color: '#2563eb' }}>Kapiki</span> Akapikebula
                </Link>

                <form onSubmit={handleSearch} style={styles.searchForm}>
                    <input
                        type="text"
                        placeholder="Search electronics, models, shops..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={styles.searchInput}
                    />
                    <button type="submit" style={styles.searchButton}>
                        🔍 Search
                    </button>
                </form>

                <nav style={styles.navLinks}>
                    <Link to="/" style={styles.link}>Main</Link>
                    <Link to="/login" style={styles.loginBtn}>Login</Link>
                </nav>
            </div>
        </header>
    );
}

const styles = {
    header: {
        backgroundColor: '#ffffff',
        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.08)',
        position: 'sticky',
        top: 0,
        zIndex: 1000,
        padding: '12px 0',
    },
    container: {
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '0 20px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '20px',
    },
    logo: {
        fontSize: '22px',
        fontWeight: 'bold',
        color: '#1e293b',
        textDecoration: 'none',
    },
    searchForm: {
        display: 'flex',
        flex: 1,
        maxWidth: '500px',
    },
    searchInput: {
        width: '100%',
        padding: '10px 14px',
        border: '1px solid #cbd5e1',
        borderRadius: '8px 0 0 8px',
        outline: 'none',
        fontSize: '14px',
    },
    searchButton: {
        backgroundColor: '#2563eb',
        color: '#fff',
        border: 'none',
        padding: '10px 18px',
        borderRadius: '0 8px 8px 0',
        cursor: 'pointer',
        fontWeight: '500',
    },
    navLinks: {
        display: 'flex',
        alignItems: 'center',
        gap: '15px',
    },
    link: {
        textDecoration: 'none',
        color: '#475569',
        fontWeight: '500',
    },
    loginBtn: {
        textDecoration: 'none',
        backgroundColor: '#f1f5f9',
        color: '#0f172a',
        padding: '8px 16px',
        borderRadius: '6px',
        fontWeight: '600',
    },
};

export default Navbar;