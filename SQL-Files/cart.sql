USE customer_database_schema;

-- Assuming id column in the customer table is INT
ALTER TABLE user_cart
ADD COLUMN customer_id INT,
ADD FOREIGN KEY (customer_id) REFERENCES customer(id);
