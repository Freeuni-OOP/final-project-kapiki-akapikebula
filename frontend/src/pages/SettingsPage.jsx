import { useState } from 'react';

function SettingsPage({ user, setUser }) {
    const [username, setUsername] = useState(user?.username || '');
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');

    const [statusMessage, setStatusMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const getAuthHeaders = () => {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        };
    };

    // Handle Username Update
    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setStatusMessage('');
        setErrorMessage('');

        try {
            const response = await fetch('http://localhost:8080/api/users/update-username', {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify({ username })
            });

            const data = await response.json();

            if (response.ok) {
                const updatedUser = { ...user, username };
                setUser(updatedUser);
                localStorage.setItem('user', JSON.stringify(updatedUser));
                setStatusMessage('✅ Username updated successfully!');
            } else {
                setErrorMessage(data.error || 'Error updating profile settings.');
            }
        } catch (err) {
            setErrorMessage('Could not connect to the server.');
        }
    };

    // Handle Password Change
    const handleChangePassword = async (e) => {
        e.preventDefault();
        setStatusMessage('');
        setErrorMessage('');

        try {
            const response = await fetch('http://localhost:8080/api/auth/change-password', {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify({ oldPassword, newPassword })
            });

            const data = await response.json();

            if (response.ok) {
                setStatusMessage('✅ Password changed successfully!');
                setOldPassword('');
                setNewPassword('');
            } else {
                setErrorMessage(data.error || 'Error changing password.');
            }
        } catch (err) {
            setErrorMessage('Could not connect to the server.');
        }
    };

    // Handle Account Deactivation / Deletion
    const handleDeleteAccount = async () => {
        const confirmDelete = window.confirm('⚠️ Are you sure you want to delete your account? This action cannot be undone!');
        if (!confirmDelete) return;

        try {
            const response = await fetch('http://localhost:8080/api/users/delete', {
                method: 'DELETE',
                headers: getAuthHeaders()
            });

            if (response.ok) {
                alert('Your account has been deleted.');
                setUser(null);
                localStorage.removeItem('user');
                localStorage.removeItem('token');
            } else {
                const data = await response.json();
                setErrorMessage(data.error || 'Error deleting your account.');
            }
        } catch (err) {
            setErrorMessage('Could not connect to the server.');
        }
    };

    if (!user) {
        return <div style={{ textAlign: 'center', padding: '40px' }}>Please log in to view this page.</div>;
    }

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>⚙️ Profile Settings</h2>
                <p style={styles.subtitle}>Manage your account details and security settings</p>

                {statusMessage && <div style={styles.successAlert}>{statusMessage}</div>}
                {errorMessage && <div style={styles.errorAlert}>{errorMessage}</div>}

                {/* Section 1: Profile Info */}
                <form onSubmit={handleUpdateProfile} style={styles.form}>
                    <h3 style={styles.sectionTitle}>👤 Edit Profile</h3>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Email Address - <span style={{color: '#64748b'}}>Cannot be changed</span></label>
                        <input
                            type="email"
                            value={user.email}
                            disabled
                            style={{ ...styles.input, backgroundColor: '#f1f5f9', color: '#64748b', cursor: 'not-allowed' }}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Username</label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    <button type="submit" style={styles.saveBtn}>
                        Update Username
                    </button>
                </form>

                <hr style={styles.divider} />

                {/* Section 2: Security */}
                <form onSubmit={handleChangePassword} style={styles.form}>
                    <h3 style={styles.sectionTitle}>🔒 Security</h3>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Current Password</label>
                        <input
                            type="password"
                            placeholder="••••••••"
                            value={oldPassword}
                            onChange={(e) => setOldPassword(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    <div style={styles.inputGroup}>
                        <label style={styles.label}>New Password</label>
                        <input
                            type="password"
                            placeholder="••••••••"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                            style={styles.input}
                        />
                    </div>

                    <button type="submit" style={styles.passwordBtn}>
                        Change Password
                    </button>
                </form>

                <hr style={styles.divider} />

                {/* Section 3: Danger Zone */}
                <div style={styles.dangerZone}>
                    <h3 style={styles.dangerTitle}>Delete Account</h3>
                    <p style={styles.dangerSubtitle}>Permanently delete your profile and all associated data.</p>
                    <button onClick={handleDeleteAccount} style={styles.deleteBtn}>
                        Delete Account
                    </button>
                </div>
            </div>
        </div>
    );
}

const styles = {
    container: { maxWidth: '600px', margin: '40px auto', padding: '0 20px' },
    card: { backgroundColor: '#ffffff', padding: '32px', borderRadius: '16px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px rgba(0, 0, 0, 0.02)' },
    title: { margin: '0 0 6px 0', fontSize: '22px', color: '#0f172a' },
    subtitle: { margin: '0 0 24px 0', fontSize: '14px', color: '#64748b' },
    sectionTitle: { margin: '0 0 10px 0', fontSize: '16px', color: '#334155', fontWeight: '600' },
    divider: { border: '0', height: '1px', backgroundColor: '#e2e8f0', margin: '28px 0' },
    successAlert: { backgroundColor: '#d1fae5', color: '#047857', padding: '12px', borderRadius: '8px', marginBottom: '20px', fontSize: '14px', fontWeight: '600' },
    errorAlert: { backgroundColor: '#fee2e2', color: '#b91c1c', padding: '12px', borderRadius: '8px', marginBottom: '20px', fontSize: '14px', fontWeight: '600' },
    form: { display: 'flex', flexDirection: 'column', gap: '16px' },
    inputGroup: { display: 'flex', flexDirection: 'column', gap: '6px' },
    label: { fontSize: '13px', fontWeight: '600', color: '#475569' },
    input: { padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px', outline: 'none' },
    saveBtn: { backgroundColor: '#2563eb', color: '#ffffff', border: 'none', padding: '10px 16px', borderRadius: '8px', fontSize: '14px', fontWeight: '600', cursor: 'pointer', alignSelf: 'flex-start' },
    passwordBtn: { backgroundColor: '#0f172a', color: '#ffffff', border: 'none', padding: '10px 16px', borderRadius: '8px', fontSize: '14px', fontWeight: '600', cursor: 'pointer', alignSelf: 'flex-start' },
    dangerZone: { border: '1px solid #fca5a5', backgroundColor: '#fff5f5', padding: '20px', borderRadius: '12px', display: 'flex', flexDirection: 'column', gap: '10px' },
    dangerTitle: { margin: '0', fontSize: '16px', color: '#991b1b', fontWeight: '600' },
    dangerSubtitle: { margin: '0', fontSize: '13px', color: '#7f1d1d' },
    deleteBtn: { backgroundColor: '#dc2626', color: '#ffffff', border: 'none', padding: '10px 16px', borderRadius: '8px', fontSize: '14px', fontWeight: '600', cursor: 'pointer', alignSelf: 'flex-start', marginTop: '6px' }
};

export default SettingsPage;