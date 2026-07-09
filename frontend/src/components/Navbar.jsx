import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Navbar({ user, setUser }) {
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const navigate = useNavigate();

    const handleLogout = () => {
        setUser(null);
        localStorage.removeItem('user');
        setDropdownOpen(false);
        navigate('/');
    };

    return (
        <nav style={styles.nav}>
            <div style={styles.container}>
                <Link to="/" style={styles.logo}>
                    🏷️ PriceTracker
                </Link>

                <div style={styles.menu}>
                    {user ? (
                        /* თუ იუზერი ავტორიზებულია -> ჩანს იუზერნეიმი და Dropdown */
                        <div style={styles.userContainer}>
                            <button
                                onClick={() => setDropdownOpen(!dropdownOpen)}
                                style={styles.userBtn}
                            >
                                👤 {user.username} ▾
                            </button>

                            {dropdownOpen && (
                                <div style={styles.dropdown}>
                                    <Link to="/cart" style={styles.dropdownItem} onClick={() => setDropdownOpen(false)}>
                                        Cart
                                    </Link>
                                    <Link to="/watchlist" style={styles.dropdownItem} onClick={() => setDropdownOpen(false)}>
                                        Watchlist
                                    </Link>
                                    <Link to="/settings" style={styles.dropdownItem} onClick={() => setDropdownOpen(false)}>
                                        Settings
                                    </Link>
                                    <hr style={styles.divider} />
                                    <button onClick={handleLogout} style={styles.logoutBtn}>
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    ) : (
                        /* თუ იუზერი არ არის შესული -> ჩანს Login / Register */
                        <div style={styles.authLinks}>
                            <Link to="/login" style={styles.loginLink}>Login</Link>
                            <Link to="/register" style={styles.registerBtn}>Register</Link>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
}

const styles = {
    nav: {
        backgroundColor: '#ffffff',
        borderBottom: '1px solid #e2e8f0',
        padding: '12px 0',
    },
    container: {
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '0 20px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    logo: {
        fontSize: '20px',
        fontWeight: '700',
        color: '#2563eb',
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
        backgroundColor: '#eff6ff',
        color: '#2563eb',
        border: '1px solid #bfdbfe',
        padding: '8px 16px',
        borderRadius: '20px',
        fontSize: '14px',
        fontWeight: '600',
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
        gap: '12px',
        alignItems: 'center',
    },
    loginLink: {
        color: '#475569',
        textDecoration: 'none',
        fontWeight: '600',
        fontSize: '14px',
    },
    registerBtn: {
        backgroundColor: '#2563eb',
        color: '#ffffff',
        textDecoration: 'none',
        padding: '8px 16px',
        borderRadius: '8px',
        fontSize: '14px',
        fontWeight: '600',
    },
};

export default Navbar;