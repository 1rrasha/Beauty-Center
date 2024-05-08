USE customer_database_schema;

-- Create services table
DROP TABLE IF EXISTS services;

CREATE TABLE services (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name TEXT,
    description TEXT,
    duration INTEGER,
    price DECIMAL(10, 2)
);

-- Insert dummy data into services table
INSERT INTO services (name, description, duration, price)
VALUES 
    ('Haircut', 'Basic haircut service', 30, 25.00),
    ('Manicure', 'Nail grooming service', 45, 35.00),
    ('Massage', 'Relaxing full-body massage', 60, 50.00),
    ('Facial', 'Skin cleansing and treatment', 45, 40.00),
    ('Pedicure', 'Foot care and nail painting', 60, 45.00),
    ('Spa Package', 'Full spa experience', 120, 100.00),
    ('Hair Color', 'Hair coloring service', 90, 75.00),
    ('Waxing', 'Hair removal by waxing', 30, 20.00),
    ('Shave', 'Traditional hot towel shave', 30, 30.00),
    ('Makeup', 'Professional makeup application', 60, 55.00);

-- Select all data from the services table
SELECT * FROM services;
SELECT * FROM customer;





