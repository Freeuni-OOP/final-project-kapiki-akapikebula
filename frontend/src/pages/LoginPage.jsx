import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();

        // სატესტოდ შევქმნათ იუზერის ობიექტი
        const loggedInUser = { username: email.split('@')[0], email: email };

        setUser(loggedInUser);
        localStorage.setItem('user', JSON.stringify(loggedInUser));

        navigate('/');
    };

    return (
        <div style={styles.wrapper}>
            <div style={styles.card}>
                <h2 style={styles.title}>Welcome Back</h2>
                <p style={styles.subtitle}>Sign in to track price changes and save products</p>

                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Email Address</label>
                        <input
                            type="email"
                            required
                            placeholder="name@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Password</label>
                        <input
                            type="password"
                            required
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            style={styles.input}
                        />
                    </div>

                    <button type="submit" style={styles.button}>
                        Sign In
                    </button>
                </form>

                <p style={styles.footerText}>
                    Don't have an account?{' '}
                    <Link to="/register" style={styles.link}>
                        Register here
                    </Link>
                </p>
            </div>
        </div>
    );
}

const styles = {
    wrapper: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: 'calc(100vh - 200px)',
        padding: '20px',
    },
    card: {
        backgroundColor: '#ffffff',
        padding: '36px',
        borderRadius: '16px',
        border: '1px solid #e2e8f0',
        boxShadow: '0 10px 25px rgba(0, 0, 0, 0.05)',
        width: '100%',
        maxWidth: '400px',
    },
    title: {
        margin: '0 0 8px 0',
        fontSize: '24px',
        fontWeight: '700',
        color: '#0f172a',
        textAlign: 'center',
    },
    subtitle: {
        margin: '0 0 28px 0',
        fontSize: '14px',
        color: '#64748b',
        textAlign: 'center',
    },
    form: {
        display: 'flex',
        flexDirection: 'column',
        gap: '18px',
    },
    inputGroup: {
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
    },
    label: {
        fontSize: '13px',
        fontWeight: '600',
        color: '#334155',
    },
    input: {
        padding: '12px',
        borderRadius: '8px',
        border: '1px solid #cbd5e1',
        fontSize: '14px',
        outline: 'none',
    },
    button: {
        backgroundColor: '#2563eb',
        color: '#ffffff',
        border: 'none',
        padding: '12px',
        borderRadius: '8px',
        fontSize: '15px',
        fontWeight: '600',
        cursor: 'pointer',
        marginTop: '10px',
    },
    footerText: {
        marginTop: '24px',
        fontSize: '14px',
        color: '#64748b',
        textAlign: 'center',
    },
    link: {
        color: '#2563eb',
        textDecoration: 'none',
        fontWeight: '600',
    },
};

export default LoginPage;