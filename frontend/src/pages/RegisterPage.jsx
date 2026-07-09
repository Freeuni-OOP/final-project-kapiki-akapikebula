import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

function RegisterPage() {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
    });
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (formData.password !== formData.confirmPassword) {
            alert("Passwords don't match!");
            return;
        }
        // TODO: Connect with Spring Boot Backend API
        console.log('Registering user:', formData);
        navigate('/login');
    };

    return (
        <div style={styles.wrapper}>
            <div style={styles.card}>
                <h2 style={styles.title}>Create Account</h2>
                <p style={styles.subtitle}>Start tracking product prices across Georgia</p>

                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Username</label>
                        <input
                            type="text"
                            name="username"
                            required
                            placeholder="johndoe"
                            value={formData.username}
                            onChange={handleChange}
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Email Address</label>
                        <input
                            type="email"
                            name="email"
                            required
                            placeholder="name@example.com"
                            value={formData.email}
                            onChange={handleChange}
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Password</label>
                        <input
                            type="password"
                            name="password"
                            required
                            placeholder="••••••••"
                            value={formData.password}
                            onChange={handleChange}
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Confirm Password</label>
                        <input
                            type="password"
                            name="confirmPassword"
                            required
                            placeholder="••••••••"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            style={styles.input}
                        />
                    </div>

                    <button type="submit" style={styles.button}>
                        Register
                    </button>
                </form>

                <p style={styles.footerText}>
                    Already have an account?{' '}
                    <Link to="/login" style={styles.link}>
                        Login
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
        padding: '30px 20px',
    },
    card: {
        backgroundColor: '#ffffff',
        padding: '36px',
        borderRadius: '16px',
        border: '1px solid #e2e8f0',
        boxShadow: '0 10px 25px rgba(0, 0, 0, 0.05)',
        width: '100%',
        maxWidth: '420px',
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
        gap: '16px',
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

export default RegisterPage;