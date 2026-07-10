import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import Footer from './components/Footer.jsx';

import HomePage from './pages/HomePage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import ProductDetailsPage from './pages/ProductDetailsPage.jsx';
import SettingsPage from './pages/SettingsPage.jsx';
import WatchlistPage from './pages/WatchlistPage.jsx';
import SearchResultsPage from './pages/SearchResultsPage.jsx';

function App() {
    const [user, setUser] = useState(() => {
        const savedUser = localStorage.getItem('user');
        return savedUser ? JSON.parse(savedUser) : null;
    });

    return (
        <Router>
            <div style={{ display: 'flex', flexDirection: 'column', backgroundColor: '#f8fafc', minHeight: '100vh', fontFamily: 'sans-serif' }}>
                <Navbar user={user} setUser={setUser} />
                <div style={{ flex: 1 }}>
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/login" element={<LoginPage setUser={setUser} />} />
                        <Route path="/register" element={<RegisterPage />} />
                        <Route path="/product/:id" element={<ProductDetailsPage />} />
                        <Route path="/search" element={<SearchResultsPage />} />
                        <Route path="/settings" element={<SettingsPage user={user} setUser={setUser} />} />
                        <Route path="/watchlist" element={<WatchlistPage user={user} />} />
                        <Route path="/cart" element={<div style={{ padding: '40px', textAlign: 'center' }}>🛒 Cart Page</div>} />
                    </Routes>
                </div>
                <Footer />
            </div>
        </Router>
    );
}

export default App;