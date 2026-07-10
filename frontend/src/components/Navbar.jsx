import { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import SearchBar from './SearchBar.jsx';

function Navbar({ user, setUser }) {
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const navigate = useNavigate();
    const dropdownRef = useRef(null);

    const handleLogout = () => {
        setUser(null);
        localStorage.removeItem('user');
        setDropdownOpen(false);
        navigate('/');
    };

    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setDropdownOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <header style={styles.header}>
            <div style={styles.container}>
                <Link to="/" style={styles.logo}>
                    🪙 <span style={{ color: '#2563eb' }}>Kapiki</span> Akapikebula
                </Link>

                <SearchBar />

                <div style={styles.menu}>
                    {user ? (
                        <div style={styles.userContainer} ref={dropdownRef}>
                            <button
                                onClick={() => setDropdownOpen(!dropdownOpen)}
                                style={styles.userBtn}
                            >
                                👤 {user.username} ▾
                            </button>

                            {dropdownOpen && (
                                <div style={styles.dropdown}>
                                    <Link to="/watchlist" style={styles.dropdownItem} onClick={() => setDropdownOpen(false)}>
                                        ⭐ Watchlist
                                    </Link>
                                    <Link to="/settings" style={styles.dropdownItem} onClick={() => setDropdownOpen(false)}>
                                        ⚙️ Settings
                                    </Link>
                                    <hr style={styles.divider} />
                                    <button onClick={handleLogout} style={styles.logoutBtn}>
                                        🚪 Sign Out
                                    </button>
                                </div>
                            )}
                        </div>
                    ) : (
                        <div style={styles.authLinks}>
                            <Link to="/login" style={styles.loginBtn}>Login</Link>
                        </div>
                    )}
                </div>
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
    menu: {
        display: 'flex',
        alignItems: 'center',
    },
    userContainer: {
        position: 'relative',
    },
    userBtn: {
        backgroundColor: '#f1f5f9',
        border: 'none',
        padding: '8px 16px',
        borderRadius: '8px',
        fontSize: '14px',
        fontWeight: '600',
        color: '#1e293b',
        cursor: 'pointer',
    },
    dropdown: {
        position: 'absolute',
        right: 0,
        top: '42px',
        backgroundColor: '#ffffff',
        border: '1px solid #e2e8f0',
        borderRadius: '10px',
        boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
        width: '200px',
        display: 'flex',
        flexDirection: 'column',
        padding: '8px 0',
        zIndex: 100,
    },
    dropdownItem: {
        padding: '10px 16px',
        color: '#334155',
        textDecoration: 'none',
        fontSize: '14px',
        fontWeight: '500',
    },
    divider: {
        margin: '6px 0',
        border: 'none',
        borderTop: '1px solid #f1f5f9',
    },
    logoutBtn: {
        backgroundColor: 'transparent',
        border: 'none',
        color: '#ef4444',
        padding: '10px 16px',
        textAlign: 'left',
        fontSize: '14px',
        fontWeight: '600',
        cursor: 'pointer',
    },
    authLinks: {
        display: 'flex',
        gap: '10px',
    },
    loginBtn: {
        textDecoration: 'none',
        backgroundColor: '#2563eb',
        color: '#ffffff',
        padding: '8px 16px',
        borderRadius: '6px',
        fontWeight: '600',
        fontSize: '14px',
    },
};

export default Navbar;