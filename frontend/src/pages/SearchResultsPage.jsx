import { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { searchProducts } from '../api/ProductApi.js';

const PAGE_SIZE = 20;

function SearchResultsPage() {
    const [searchParams] = useSearchParams();
    const query = searchParams.get('query') || '';

    const [results, setResults] = useState([]);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [appliedMinPrice, setAppliedMinPrice] = useState(undefined);
    const [appliedMaxPrice, setAppliedMaxPrice] = useState(undefined);
    const [sortOption, setSortOption] = useState('name_asc');

    const [minInput, setMinInput] = useState('');
    const [maxInput, setMaxInput] = useState('');

    const trimmedQuery = query.trim();

    const [prevQuery, setPrevQuery] = useState(query);
    if (query !== prevQuery) {
        setPrevQuery(query);
        setPage(0);
        setAppliedMinPrice(undefined);
        setAppliedMaxPrice(undefined);
        setMinInput('');
        setMaxInput('');
        setSortOption('name_asc');
    }

    useEffect(() => {
        if (!trimmedQuery) return;

        let cancelled = false;

        const fetchData = async () => {
            setLoading(true);
            setError(null);

            let sortBy = 'name';
            let sortDir = 'asc';

            if (sortOption === 'price_asc') {
                sortBy = 'price';
                sortDir = 'asc';
            } else if (sortOption === 'price_desc') {
                sortBy = 'price';
                sortDir = 'desc';
            }

            try {
                const data = await searchProducts(
                    trimmedQuery,
                    appliedMinPrice,
                    appliedMaxPrice,
                    sortBy,
                    sortDir,
                    page,
                    PAGE_SIZE
                );

                if (cancelled) return;
                setResults(data?.content ?? []);
                setTotalElements(data?.totalElements ?? 0);
                setTotalPages(data?.totalPages ?? 0);
            } catch (err) {
                if (cancelled) return;
                console.error('Search results error:', err);
                setError('There as a problem with loading products.');
                setResults([]);
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        fetchData();

        return () => {
            cancelled = true;
        };
    }, [trimmedQuery, page, appliedMinPrice, appliedMaxPrice, sortOption]);

    const handleFilterSubmit = (e) => {
        e.preventDefault();
        setAppliedMinPrice(minInput ? Number(minInput) : undefined);
        setAppliedMaxPrice(maxInput ? Number(maxInput) : undefined);
        setPage(0);
    };

    const handleSortChange = (e) => {
        setSortOption(e.target.value);
        setPage(0);
    };

    const goToPage = (p) => {
        if (p < 0 || p >= totalPages) return;
        setPage(p);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        <div style={styles.container}>
            <h1 style={styles.heading}>
                Search Results: „{query}“
                {trimmedQuery && !loading && !error && <span style={styles.count}> ({totalElements})</span>}
            </h1>

            {!trimmedQuery && <div style={styles.info}>No Query Mentioned</div>}

            {trimmedQuery && (
                <div style={styles.mainLayout}>
                    <aside style={styles.sidebar}>
                        <div style={styles.filterGroup}>
                            <label style={styles.filterLabel}>Sort</label>
                            <select value={sortOption} onChange={handleSortChange} style={styles.select}>
                                <option value="name_asc">By Price</option>
                                <option value="price_asc">Price: Ascending ⬆</option>
                                <option value="price_desc">Price: Descending ⬇</option>
                            </select>
                        </div>

                        <form onSubmit={handleFilterSubmit} style={styles.filterGroup}>
                            <label style={styles.filterLabel}>Price Range</label>
                            <div style={styles.priceInputs}>
                                <input
                                    type="number"
                                    placeholder="From"
                                    value={minInput}
                                    onChange={(e) => setMinInput(e.target.value)}
                                    style={styles.priceInput}
                                />
                                <span style={{ color: '#64748b' }}>-</span>
                                <input
                                    type="number"
                                    placeholder="To"
                                    value={maxInput}
                                    onChange={(e) => setMaxInput(e.target.value)}
                                    style={styles.priceInput}
                                />
                            </div>
                            <button type="submit" style={styles.filterBtn}>Filter</button>
                        </form>
                    </aside>

                    <div style={styles.contentArea}>
                        {loading && <div style={styles.info}>loading...</div>}

                        {!loading && error && <div style={styles.errorInfo}>{error}</div>}

                        {!loading && !error && results.length === 0 && (
                            <div style={styles.info}>No Products Found.</div>
                        )}

                        {!loading && !error && results.length > 0 && (
                            <>
                                <div style={styles.grid}>
                                    {results.map((p) => (
                                        <Link key={p.productId} to={`/product/${p.productId}`} style={styles.card}>
                                            <img src={p.imageUrl} alt={p.name} style={styles.cardImg} />
                                            <div style={styles.cardBody}>
                                                <div style={styles.cardName}>{p.name}</div>
                                                <div style={styles.cardBrand}>{p.brand}</div>
                                                {p.lowestPrice != null && (
                                                    <div style={styles.cardPrice}>{p.lowestPrice} ₾</div>
                                                )}
                                            </div>
                                        </Link>
                                    ))}
                                </div>

                                {totalPages > 1 && (
                                    <div style={styles.pagination}>
                                        <button
                                            onClick={() => goToPage(page - 1)}
                                            disabled={page === 0}
                                            style={{ ...styles.pageBtn, ...(page === 0 ? styles.pageBtnDisabled : {}) }}
                                        >
                                            ← previous
                                        </button>
                                        <span style={styles.pageInfo}>{page + 1} / {totalPages}</span>
                                        <button
                                            onClick={() => goToPage(page + 1)}
                                            disabled={page >= totalPages - 1}
                                            style={{ ...styles.pageBtn, ...(page >= totalPages - 1 ? styles.pageBtnDisabled : {}) }}
                                        >
                                            next →
                                        </button>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

const styles = {
    container: {
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '32px 20px 60px',
    },
    heading: {
        fontSize: '20px',
        fontWeight: '700',
        color: '#1e293b',
        marginBottom: '24px',
    },
    count: {
        fontWeight: '400',
        color: '#64748b',
        fontSize: '16px',
    },
    mainLayout: {
        display: 'flex',
        gap: '24px',
        alignItems: 'flex-start',
    },
    sidebar: {
        width: '260px',
        backgroundColor: '#ffffff',
        border: '1px solid #e2e8f0',
        borderRadius: '12px',
        padding: '20px',
        position: 'sticky',
        top: '90px',
        boxSizing: 'border-box',
    },
    contentArea: {
        flex: 1,
    },
    filterGroup: {
        marginBottom: '24px',
    },
    filterLabel: {
        display: 'block',
        fontSize: '13px',
        fontWeight: '600',
        color: '#475569',
        marginBottom: '8px',
        textTransform: 'uppercase',
        letterSpacing: '0.5px',
    },
    select: {
        width: '100%',
        padding: '10px 12px',
        borderRadius: '8px',
        border: '1px solid #cbd5e1',
        backgroundColor: '#ffffff',
        fontSize: '14px',
        outline: 'none',
        color: '#1e293b',
        cursor: 'pointer',
    },
    priceInputs: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
    },
    priceInput: {
        width: '100%',
        padding: '10px 12px',
        borderRadius: '8px',
        border: '1px solid #cbd5e1',
        fontSize: '14px',
        outline: 'none',
        color: '#1e293b',
    },
    filterBtn: {
        width: '100%',
        backgroundColor: '#2563eb',
        color: '#ffffff',
        border: 'none',
        padding: '10px 0',
        borderRadius: '8px',
        fontWeight: '600',
        fontSize: '14px',
        cursor: 'pointer',
        marginTop: '14px',
        transition: 'background-color 0.15s',
    },
    info: {
        fontSize: '14px',
        color: '#64748b',
        padding: '20px 0',
    },
    errorInfo: {
        fontSize: '14px',
        color: '#dc2626',
        padding: '20px 0',
    },
    grid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
        gap: '16px',
    },
    card: {
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: '#ffffff',
        border: '1px solid #e2e8f0',
        borderRadius: '10px',
        overflow: 'hidden',
        textDecoration: 'none',
        transition: 'transform 0.15s ease, box-shadow 0.15s ease',
    },
    cardImg: {
        width: '100%',
        height: '160px',
        objectFit: 'contain',
        backgroundColor: '#f8fafc',
        padding: '10px',
        boxSizing: 'border-box',
    },
    cardBody: {
        padding: '12px 14px',
    },
    cardName: {
        fontSize: '14px',
        fontWeight: '600',
        color: '#1e293b',
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
    },
    cardBrand: {
        fontSize: '12px',
        color: '#64748b',
        marginTop: '2px',
    },
    cardPrice: {
        fontSize: '15px',
        fontWeight: '700',
        color: '#2563eb',
        marginTop: '8px',
    },
    pagination: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '16px',
        marginTop: '32px',
    },
    pageBtn: {
        backgroundColor: '#f1f5f9',
        border: 'none',
        padding: '8px 16px',
        borderRadius: '8px',
        fontSize: '14px',
        fontWeight: '600',
        color: '#1e293b',
        cursor: 'pointer',
    },
    pageBtnDisabled: {
        opacity: 0.4,
        cursor: 'default',
    },
    pageInfo: {
        fontSize: '14px',
        color: '#64748b',
        fontWeight: '600',
    },
};

export default SearchResultsPage;
