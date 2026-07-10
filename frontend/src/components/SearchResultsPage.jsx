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

    const trimmedQuery = query.trim();

    const [prevQuery, setPrevQuery] = useState(query);
    if (query !== prevQuery) {
        setPrevQuery(query);
        setPage(0);
    }

    useEffect(() => {
        if (!trimmedQuery) {
            return;
        }

        let cancelled = false;
        const fetchData = async () => {
            setLoading(true);
            setError(null);

            try {
                const data = await searchProducts(trimmedQuery, undefined, undefined, 'name', 'asc', page, PAGE_SIZE);
                if (cancelled) return;

                setResults(data?.content ?? []);
                setTotalElements(data?.totalElements ?? 0);
                setTotalPages(data?.totalPages ?? 0);
            } catch (err) {
                if (cancelled) return;
                console.error('Search results error:', err);
                setError('შედეგების ჩატვირთვა ვერ მოხერხდა — შეამოწმეთ სერვერთან კავშირი');
                setResults([]);
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        fetchData();

        return () => {
            cancelled = true;
        };
    }, [trimmedQuery, page]);

    const goToPage = (p) => {
        if (p < 0 || p >= totalPages) return;
        setPage(p);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        <div style={styles.container}>
            <h1 style={styles.heading}>
                ძიების შედეგები: „{query}“
                {trimmedQuery && !loading && !error && <span style={styles.count}> ({totalElements})</span>}
            </h1>

            {!trimmedQuery && <div style={styles.info}>საძიებო სიტყვა არ არის მითითებული</div>}

            {trimmedQuery && loading && <div style={styles.info}>იტვირთება...</div>}

            {trimmedQuery && !loading && error && <div style={styles.errorInfo}>{error}</div>}

            {trimmedQuery && !loading && !error && results.length === 0 && (
                <div style={styles.info}>ვერაფერი მოიძებნა „{query}“-სთვის</div>
            )}

            {trimmedQuery && !loading && !error && results.length > 0 && (
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
                                ← წინა
                            </button>
                            <span style={styles.pageInfo}>{page + 1} / {totalPages}</span>
                            <button
                                onClick={() => goToPage(page + 1)}
                                disabled={page >= totalPages - 1}
                                style={{ ...styles.pageBtn, ...(page >= totalPages - 1 ? styles.pageBtnDisabled : {}) }}
                            >
                                შემდეგი →
                            </button>
                        </div>
                    )}
                </>
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
        transition: 'box-shadow 0.15s ease',
    },
    cardImg: {
        width: '100%',
        height: '160px',
        objectFit: 'contain',
        backgroundColor: '#f8fafc',
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
