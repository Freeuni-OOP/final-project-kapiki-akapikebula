-- Insert a catch-all category for scraper-ingested products
INSERT INTO categories (id, name, parent_id) VALUES (1, 'Uncategorized', NULL);

-- Add match_key so we can detect the same product across stores
ALTER TABLE products ADD COLUMN match_key VARCHAR(255);
ALTER TABLE products ADD UNIQUE INDEX idx_products_match_key (match_key);

-- Insert the two shops so ingestion can look them up by name
INSERT INTO shops (name, base_url) VALUES ('Zoommer', 'https://zoommer.ge');
INSERT INTO shops (name, base_url) VALUES ('EE', 'https://www.ee.ge');