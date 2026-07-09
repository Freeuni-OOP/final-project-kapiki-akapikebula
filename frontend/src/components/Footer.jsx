import { Link } from 'react-router-dom';

function Footer() {
    return (
        <footer style={styles.footer}>
            <div style={styles.container}>
                <p style={styles.text}>&copy; 2026 Kapiki Akapikebula. All rights reserved.</p>
                <div style={styles.links}>
                    <Link to="/" style={styles.link}>Privacy Policy</Link>
                    <Link to="/" style={styles.link}>Terms of Service</Link>
                    <Link to="/" style={styles.link}>Contact</Link>
                </div>
            </div>
        </footer>
    );
}

const styles = {
    footer: {
        backgroundColor: '#ffffff',
        borderTop: '1px solid #e2e8f0',
        padding: '20px 0',
        marginTop: 'auto',
    },
    container: {
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '0 20px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        flexWrap: 'wrap',
        gap: '10px',
    },
    text: {
        margin: 0,
        fontSize: '14px',
        color: '#64748b',
    },
    links: {
        display: 'flex',
        gap: '20px',
    },
    link: {
        textDecoration: 'none',
        color: '#64748b',
        fontSize: '14px',
    },
};

export default Footer;