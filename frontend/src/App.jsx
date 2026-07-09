import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import ProductCard from './components/ProductCard.jsx';
import Footer from './components/Footer.jsx';
import PriceChart from './components/PriceChart.jsx';

const dummyProducts = [
  {
    id: 1,
    name: "Apple iPhone 15 Pro Max 256GB Black Titanium",
    category: "Smartphones",
    minPrice: 3299,
    maxPrice: 3599,
    storesCount: 5,
    imageUrl: "https://alta.ge/images/thumbnails/900/650/detailed/285/1_1h4w-20.png"
  }
];

const dummyHistory = [
  { month: 'Jan', price: 3599 },
  { month: 'Feb', price: 3550 },
  { month: 'Mar', price: 3499 },
  { month: 'Apr', price: 3450 },
  { month: 'May', price: 3350 },
  { month: 'Jun', price: 3299 }
];

function HomePage() {
  return (
      <div style={{ maxWidth: '1200px', margin: '30px auto', padding: '0 20px', boxSizing: 'border-box' }}>
        <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', marginBottom: '30px' }}>
          <h3 style={{ color: '#1e293b', marginTop: 0, marginBottom: '20px' }}>Product Price History</h3>
          <PriceChart data={dummyHistory} />
        </div>

        <h3 style={{ color: '#1e293b', marginBottom: '20px' }}>Popular Products</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px' }}>
          {dummyProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </div>
  );
}

function LoginPage() {
  return (
      <div style={{ maxWidth: '1200px', margin: '30px auto', padding: '0 20px' }}>
        <h2 style={{ color: '#1e293b' }}>Login Page</h2>
      </div>
  );
}

function App() {
  return (
      <Router>
        <div style={{ display: 'flex', flexDirection: 'column', backgroundColor: '#f8fafc', minHeight: '100vh', fontFamily: 'sans-serif' }}>
          <Navbar />
          <div style={{ flex: 1 }}>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />
            </Routes>
          </div>
          <Footer />
        </div>
      </Router>
  );
}

export default App;