import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';


function HomePage() {
  return <h2>home page </h2>;
}

function LoginPage() {
  return <h2> authorization (Login)</h2>;
}

function App() {
  return (
      <Router>
        <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
          {}
          <nav style={{ display: 'flex', gap: '15px', marginBottom: '20px' }}>
            <Link to="/">main</Link>
            <Link to="/login">enter</Link>
          </nav>

          <hr />

          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
          </Routes>
        </div>
      </Router>
  );
}

export default App;
