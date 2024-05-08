USE customer_database_schema;

-- Create beauty_center_products table
DROP TABLE IF EXISTS beauty_center_products;

CREATE TABLE beauty_center_products (
    product_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    product_name TEXT,
    product_price DECIMAL(10, 2),
    product_description TEXT
);

-- Insert dummy data into beauty_center_products table
INSERT INTO beauty_center_products (product_name, product_price, product_description)
VALUES 
    ('Shampoo', 15.00, 'Moisturizing shampoo for all hair types'),
    ('Conditioner', 12.50, 'Repairing conditioner with natural ingredients'),
    ('Skin Moisturizer', 20.00, 'Hydrating lotion for smooth and soft skin'),
    ('Facial Cleanser', 18.50, 'Gentle cleanser for daily facial care'),
    ('Sunscreen', 25.00, 'Broad-spectrum SPF 50 sunscreen for sun protection'),
    ('Body Scrub', 22.99, 'Exfoliating body scrub for radiant skin'),
    ('Nail Polish Set', 30.00, 'Set of trendy nail polish colors'),
    ('Hair Styling Gel', 14.99, 'Strong-hold styling gel for versatile hairstyles'),
    ('Perfume', 45.00, 'Signature fragrance for a lasting scent'),
    ('Makeup Palette', 39.99, 'Palette with a variety of eyeshadows and blushes');

-- Select all data from the beauty_center_products table
SELECT * FROM beauty_center_products;
