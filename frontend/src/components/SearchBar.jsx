import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { searchProducts, getHomeProducts } from '../api/ProductApi.js';

const DEBOUNCE_MS = 350;

function SearchBar() {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [fallback, setFallback] = useState([]); // როცა ვერაფერი მოიძებნა
    const [loading, setLoading] = useState(false);
    const [open, setOpen] = useState(false);
    const [error, setError] = useState(null);

    const wrapperRef = useRef(null);
    const debounceTimer = useRef(null);
    const navigate = useNavigate();

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
                const data = await searchProducts(trimmed, undefined, undefined, 'name', 'asc', 0, 6);
                const items = data?.content ?? [];
                setResults(items);
                setOpen(true);

                if (items.length === 0) {
                    try {
                        const home = await getHomeProducts();
                        setFallback((home?.content ?? home ?? []).slice(0, 4));
                    } catch {
                        setFallback([]);
                    }
                } else {
                    setFallback([]);
                }
            } catch (err) {
                console.error('Search error:', err);
                setResults([]);
                setFallback([]);
                setOpen(true);
                setError('ძებნა ვერ შესრულდა — შეამოწმეთ სერვერთან კავშირი');
            } finally {
                setLoading(false);
            }
        }, DEBOUNCE_MS);

        return () => clearTimeout(debounceTimer.current);
    }, [query]);

    const handleQueryChange = (e) => {
        const value = e.target.value;
        setQuery(value);

        if (!value.trim()) {
            setResults([]);
            setFallback([]);
            setOpen(false);
            setError(null);
        }
    };

    const goToProduct = (id) => {
        setOpen(false);
        setQuery('');
        navigate(`/product/${id}`);
    };

    const goToSearchPage = () => {
        const trimmed = query.trim();
        if (!trimmed) return;
        setOpen(false);
        navigate(`/search?query=${encodeURIComponent(trimmed)}`);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            goToSearchPage();
        }
    };

    const showNoResults = open && !loading && !error && query.trim() && results.length === 0;

    return (
        <div style={styles.wrapper} ref={wrapperRef}>
            <input
                type="text"
                value={query}
                onChange={handleQueryChange}
                onKeyDown={handleKeyDown}
                onFocus={() => query.trim() && setOpen(true)}
                placeholder="მოძებნე პროდუქტი..."
                style={styles.input}
            />

            {open && (
                <div style={styles.dropdown}>
                    {loading && <div style={styles.info}>იძებნება...</div>}

                    {!loading && error && <div style={styles.errorInfo}>{error}</div>}

                    {!loading && !error && results.map((p) => (
                        <div key={p.productId} style={styles.item} onClick={() => goToProduct(p.productId)}>
                            <img src={p.imageUrl} alt={p.name} style={styles.itemImg} />
                            <div style={styles.itemText}>
                                <div style={styles.itemName}>{p.name}</div>
                                <div style={styles.itemBrand}>{p.brand}</div>
                            </div>
                            {p.lowestPrice != null && (
                                <div style={styles.itemPrice}>{p.lowestPrice} ₾</div>
                            )}
                        </div>
                    ))}

                    {showNoResults && (
                        <>
                            <div style={styles.info}>ვერაფერი მოიძებნა „{query}“-სთვის</div>
                            {fallback.length > 0 && (
                                <>
                                    <div style={styles.fallbackLabel}>იქნებ დაგაინტერესოთ:</div>
                                    {fallback.map((p) => (
                                        <div key={p.productId ?? p.id} style={styles.item} onClick={() => goToProduct(p.productId ?? p.id)}>
                                            <img src={p.imageUrl} alt={p.name} style={styles.itemImg} />
                                            <div style={styles.itemText}>
                                                <div style={styles.itemName}>{p.name}</div>
                                                <div style={styles.itemBrand}>{p.brand}</div>
                                            </div>
                                        </div>
                                    ))}
                                </>
                            )}
                        </>
                    )}
                </div>
            )}
        </div>
    );
}

const styles = {
    wrapper: {
        position: 'relative',
        flex: 1,
        maxWidth: '420px',
        margin: '0 20px',
    },
    input: {
        width: '100%',
        boxSizing: 'border-box',
        padding: '9px 14px',
        borderRadius: '8px',
        border: '1px solid #e2e8f0',
        backgroundColor: '#f1f5f9',
        color: '#1e293b',
        colorScheme: 'light',
        fontSize: '14px',
        outline: 'none',
    },
    dropdown: {
        position: 'absolute',
        top: '46px',
        left: 0,
        right: 0,
        backgroundColor: '#ffffff',
        border: '1px solid #e2e8f0',
        borderRadius: '10px',
        boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
        maxHeight: '360px',
        overflowY: 'auto',
        zIndex: 200,
        padding: '6px 0',
    },
    item: {
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        padding: '8px 14px',
        cursor: 'pointer',
    },
    itemImg: {
        width: '32px',
        height: '32px',
        objectFit: 'contain',
        borderRadius: '4px',
        flexShrink: 0,
    },
    itemText: {
        flex: 1,
        minWidth: 0,
    },
    itemName: {
        fontSize: '13px',
        fontWeight: '600',
        color: '#1e293b',
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
    },
    itemBrand: {
        fontSize: '12px',
        color: '#64748b',
    },
    itemPrice: {
        fontSize: '13px',
        fontWeight: '700',
        color: '#2563eb',
        flexShrink: 0,
    },
    info: {
        padding: '10px 14px',
        fontSize: '13px',
        color: '#64748b',
    },
    errorInfo: {
        padding: '10px 14px',
        fontSize: '13px',
        color: '#dc2626',
    },
    fallbackLabel: {
        padding: '4px 14px',
        fontSize: '11px',
        fontWeight: '600',
        color: '#94a3b8',
        textTransform: 'uppercase',
    },
};

export default SearchBar;