import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

function LoginPage({ setUser }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Invalid credentials');
            }

            const payloadBase64 = data.token.split('.')[1];
            const decodedPayload = JSON.parse(atob(payloadBase64))
            const usernameFromJwt = decodedPayload.username;

            const loggedInUser = {
                email: email,
                username: usernameFromJwt,
                token: data.token
            };

            setUser(loggedInUser);
            localStorage.setItem('user', JSON.stringify(loggedInUser));

            navigate('/');
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={styles.wrapper}>
            <div style={styles.card}>
                <h2 style={styles.title}>Welcome Back</h2>
                <p style={styles.subtitle}>Sign in to track price changes and save products</p>

                {error && <p style={styles.errorText}>{error}</p>}

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

                    <button type="submit" style={styles.button}>Sign In</button>
                </form>

                <p style={styles.footerText}>
                    Don't have an account? <Link to="/register" style={styles.link}>Register here</Link>
                </p>
            </div>
        </div>
    );
}

const styles = {
    wrapper: { display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '60px 20px', minHeight: '60vh' },
    card: { backgroundColor: '#ffffff', padding: '40px 30px', borderRadius: '16px', border: '1px solid #e2e8f0', boxShadow: '0 10px 25px rgba(0, 0, 0, 0.05)', width: '100%', maxWidth: '400px' },
    title: { margin: '0 0 8px 0', fontSize: '24px', fontWeight: '700', color: '#0f172a', textAlign: 'center' },
    subtitle: { margin: '0 0 28px 0', fontSize: '14px', color: '#64748b', textAlign: 'center' },
    errorText: { color: '#ef4444', fontSize: '14px', textAlign: 'center', marginBottom: '10px' },
    form: { display: 'flex', flexDirection: 'column', gap: '18px' },
    inputGroup: { display: 'flex', flexDirection: 'column', gap: '6px' },
    label: { fontSize: '13px', fontWeight: '600', color: '#334155' },
    input: { padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px', outline: 'none' },
    button: { backgroundColor: '#2563eb', color: '#ffffff', border: 'none', padding: '14px', borderRadius: '8px', fontWeight: '600', fontSize: '16px', cursor: 'pointer', marginTop: '10px' },
    footerText: { marginTop: '24px', textAlign: 'center', fontSize: '14px', color: '#64748b' },
    link: { color: '#2563eb', textDecoration: 'none', fontWeight: '600' }
};

export default LoginPage;