-- Create payments table
USE customer_database_schema;
DROP TABLE IF EXISTS payments;
CREATE TABLE payments (
    payment_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    customer_id int,
    service_id INTEGER,
    payment_date DATE,
    amount_paid DECIMAL(10, 2),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);

-- Insert dummy data into payments table
INSERT INTO payments (customer_id, service_id, payment_date, amount_paid)
VALUES 
    (1, 1, '2024-01-25', 25.00),
    (2, 3, '2024-01-26', 50.00),
    (3, 7, '2024-01-27', 75.00);

-- Select all data from the payments table
SELECT * FROM payments;
DESCRIBE user_cart;
ALTER TABLE user_cart ADD COLUMN is_paid BOOLEAN DEFAULT 0;
