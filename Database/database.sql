CREATE DATABASE IF NOT EXISTS healthfirst_pims;

USE healthfirst_pims;

-- ==========================================
-- USERS TABLE
-- ==========================================

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

-- ==========================================
-- SUPPLIERS TABLE
-- ==========================================

CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

-- ==========================================
-- MEDICINES TABLE
-- ==========================================

CREATE TABLE medicines (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100) NOT NULL,
    medicine_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL,
    reorder_level INT NOT NULL,
    expiry_date DATE NOT NULL,
    supplier_id INT,

    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(supplier_id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
);

-- ==========================================
-- SALES TABLE
-- ==========================================

CREATE TABLE sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT NOT NULL,

    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

-- ==========================================
-- SALE ITEMS TABLE
-- ==========================================

CREATE TABLE sale_items (
    sale_item_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (sale_id)
    REFERENCES sales(sale_id)
    ON DELETE CASCADE,

    FOREIGN KEY (medicine_id)
    REFERENCES medicines(medicine_id)
    ON DELETE RESTRICT
);

-- ==========================================
-- SAMPLE USERS
-- ==========================================

INSERT INTO users
(username, password, role, full_name)
VALUES
('admin', 'admin123', 'Admin', 'System Administrator'),
('cashier', 'cash123', 'Cashier', 'John Cashier');

-- ==========================================
-- SAMPLE SUPPLIERS
-- ==========================================

INSERT INTO suppliers
(name, contact_person, phone, email, address)
VALUES
('PharmaCare Distributors', 'James Smith',
 '0215551234', 'info@pharmacare.co.za',
 'Cape Town, South Africa'),

('MedSupply SA', 'Sarah Williams',
 '0215555678', 'sales@medsupply.co.za',
 'Bellville, Cape Town'),

('HealthMed Suppliers', 'Michael Brown',
 '0215559999', 'info@healthmed.co.za',
 'Goodwood, Cape Town');

-- ==========================================
-- SAMPLE MEDICINES
-- ==========================================

INSERT INTO medicines
(name, company, medicine_type, price,
 quantity_in_stock, reorder_level, expiry_date, supplier_id)
VALUES
('Panado', 'Adcock Ingram', 'Tablet',
 35.00, 50, 10, '2027-06-30', 1),

('Amoxil', 'GSK', 'Capsule',
 45.00, 30, 8, '2027-04-15', 2),

('Allergex', 'Aspen', 'Tablet',
 40.00, 25, 5, '2027-02-20', 3),

('Benylin', 'Johnson & Johnson', 'Syrup',
 65.00, 15, 5, '2026-11-10', 1),

('Voltaren', 'Novartis', 'Cream',
 85.00, 8, 10, '2027-01-15', 2),

('Insulin', 'Novo Nordisk', 'Injection',
 150.00, 4, 5, '2026-10-10', 3);