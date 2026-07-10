import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { searchProducts, getHomeProducts } from '../api/ProductApi.js';

const DEBOUNCE_MS = 350;

function SearchBar() {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [fallback, setFallback] = useState([]);
    const [loading, setLoading] = useState(false);
    const [open, setOpen] = useState(false);
    const [error, setError] = useState(null);

    const wrapperRef = useRef(null);
    const debounceTimer = useRef(null);
    const navigate = useNavigate();

    // 🤖 AI ფუნქცია: ვინახავთ ძებნის ისტორიას
    const saveSearchHistory = (term) => {
        if (!term.trim()) return;
        const currentHistory = JSON.parse(localStorage.getItem('searchHistory') || '[]');
        // ვამატებთ ახალ სიტყვას თავში, ვშლით დუბლიკატებს და ვტოვებთ ბოლო 3 ძებნას
        const updatedHistory = [term.trim(), ...currentHistory.filter(t => t !== term.trim())].slice(0, 3);
        localStorage.setItem('searchHistory', JSON.stringify(updatedHistory));
    };

    useEffect(() => {
        function handleClickOutside(e) {
            if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
                setOpen(false);
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    useEffect(() => {
        clearTimeout(debounceTimer.current);

        const trimmed = query.trim();
        if (!trimmed) {
            return;
        }

        debounceTimer.current = setTimeout(async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await searchProducts(trimmed, undefined, undefined, 'name', 'asc', 0, 5);
                setResults(data.content || []);

                // 🔥 აი, მთავარი შესწორება: ავტომატურად ვინახავთ ისტორიას როგორც კი რექვესთი გაიგზავნება!
                saveSearchHistory(trimmed);

            } catch (err) {
                console.error("Search error:", err);
                setError("Failed to fetch results");
            } finally {
                setLoading(false);
            }
        }, DEBOUNCE_MS);

    }, [query]);

    const handleFocus = async () => {
        setOpen(true);
        if (!query.trim() && fallback.length === 0) {
            setLoading(true);
            try {
                const homeData = await getHomeProducts();
                setFallback((homeData?.content || homeData || []).slice(0, 5));
            } catch (err) {
                console.error("Fallback error:", err);
            } finally {
                setLoading(false);
            }
        }
    };

    const handleProductClick = (productId) => {
        setOpen(false);
        setQuery('');
        navigate(`/product/${productId}`);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            setOpen(false);
        }
    };

    const displayItems = query.trim() ? results : fallback;

    return (
        <div style={styles.container} ref={wrapperRef}>
            <div style={styles.inputWrapper}>
                <input
                    type="text"
                    placeholder="Search for electronics..."
                    value={query}
                    onChange={(e) => {
                        setQuery(e.target.value);
                        setOpen(true);
                    }}
                    onFocus={handleFocus}
                    onKeyDown={handleKeyDown}
                    style={styles.input}
                />
                <button style={styles.button} onClick={(e) => {
                    e.preventDefault();
                    if (query.trim()) saveSearchHistory(query);
                }}>
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="18"
                        height="18"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    >
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                </button>
            </div>

            {open && (
                <div style={styles.dropdown}>
                    {!query.trim() && (
                        <div style={styles.sectionTitle}>Suggested Products</div>
                    )}

                    {loading ? (
                        <div style={styles.message}>Searching...</div>
                    ) : error ? (
                        <div style={styles.message}>{error}</div>
                    ) : displayItems.length > 0 ? (
                        displayItems.map((item) => {
                            const pId = item.productId || item.id;
                            const priceToDisplay = item.lowestPrice || item.price;

                            return (
                                <div
                                    key={pId}
                                    style={styles.item}
                                    onClick={() => handleProductClick(pId)}
                                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f8fafc'}
                                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                                >
                                    <img
                                        src={item.imageUrl}
                                        alt={item.name}
                                        style={styles.itemImg}
                                        onError={(e) => {
                                            e.target.onerror = null;
                                            e.target.src = 'https://via.placeholder.com/32?text=No+Img';
                                        }}
                                    />
                                    <div style={styles.itemText}>
                                        <div style={styles.itemName} title={item.name}>
                                            {item.name}
                                        </div>
                                        <div style={styles.itemBrand}>{item.brand || 'Electronics'}</div>
                                    </div>
                                    <div style={styles.itemPrice}>
                                        {priceToDisplay ? `${priceToDisplay} ₾` : ''}
                                    </div>
                                </div>
                            );
                        })
                    ) : query.trim() ? (
                        <div style={styles.message}>No products found.</div>
                    ) : null}
                </div>
            )}
        </div>
    );
}

const styles = {
    container: { position: 'relative', width: '400px', maxWidth: '100%' },
    inputWrapper: { display: 'flex', alignItems: 'center', backgroundColor: '#f1f5f9', borderRadius: '10px', border: '1px solid #e2e8f0', padding: '4px 12px', transition: 'all 0.2s ease' },
    input: { flex: 1, border: 'none', backgroundColor: 'transparent', padding: '8px 4px', fontSize: '14px', color: '#0f172a', outline: 'none' },
    button: { background: 'none', border: 'none', color: '#64748b', cursor: 'pointer', padding: '4px', display: 'flex', alignItems: 'center', justifyContent: 'center' },
    dropdown: { position: 'absolute', top: '48px', left: 0, right: 0, backgroundColor: '#ffffff', border: '1px solid #e2e8f0', borderRadius: '10px', boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)', maxHeight: '360px', overflowY: 'auto', zIndex: 200, padding: '6px 0' },
    item: { display: 'flex', alignItems: 'center', gap: '10px', padding: '8px 14px', cursor: 'pointer' },
    itemImg: { width: '32px', height: '32px', objectFit: 'contain', borderRadius: '4px', flexShrink: 0 },
    itemText: { flex: 1, minWidth: 0 },
    itemName: { fontSize: '13px', fontWeight: '600', color: '#1e293b', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' },
    itemBrand: { fontSize: '12px', color: '#64748b' },
    itemPrice: { fontSize: '13px', fontWeight: 'bold', color: '#2563eb', whiteSpace: 'nowrap' },
    sectionTitle: { padding: '8px 14px', fontSize: '11px', fontWeight: '700', textTransform: 'uppercase', color: '#94a3b8', letterSpacing: '0.5px' },
    message: { padding: '16px 14px', fontSize: '13px', color: '#64748b', textAlign: 'center' },
};

export default SearchBar;