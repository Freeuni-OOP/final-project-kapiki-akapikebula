import { useState } from 'react';

function SettingsPage({ user, setUser }) {
    const [username, setUsername] = useState(user?.username || '');
    const [email, setEmail] = useState(user?.email || '');
    const [password, setPassword] = useState('');
    const [statusMessage, setStatusMessage] = useState('');

    const handleSave = (e) => {
        e.preventDefault();
        const updatedUser = { ...user, username, email };

        // განაახლე State და LocalStorage
        setUser(updatedUser);
        localStorage.setItem('user', JSON.stringify(updatedUser));

        setStatusMessage('✅ პროფილის მონაცემები წარმატებით განახლდა!');
        setPassword('');
    };

    if (!user) {
        return <div style={{ textAlign: 'center', padding: '40px' }}>გთხოვთ გაიაროთ ავტორიზაცია.</div>;
    }

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>⚙️ პროფილის პარამეტრები</h2>
                <p style={styles.subtitle}>ნახე და განაახლე შენი ანგარიშის ინფორმაცია</p>

                {statusMessage && <div style={styles.successAlert}>{statusMessage}</div>}

                <form onSubmit={handleSave} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>იუზერნეიმი (Username)</label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>ელ. ფოსტა (Email)</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>ახალი პაროლი (დატოვე ცარიელი თუ არ ცვლი)</label>
                        <input
                            type="password"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            style={styles.input}
                        />
                    </div>

                    <button type="submit" style={styles.saveBtn}>
                        ცვლილებების შენახვა
                    </button>
                </form>
            </div>
        </div>
    );
}

const styles = {
    container: {
        maxWidth: '600px',
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
    },
    subtitle: {
        margin: '0 0 24px 0',
        fontSize: '14px',
        color: '#64748b',
    },
    successAlert: {
        backgroundColor: '#d1fae5',
        color: '#047857',
        padding: '12px',
        borderRadius: '8px',
        marginBottom: '20px',
        fontSize: '14px',
        fontWeight: '600',
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
    saveBtn: {
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
};

export default SettingsPage;