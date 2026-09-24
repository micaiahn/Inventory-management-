-- ============================================================
-- HealthFirst Pharmacy Inventory Management System (PIMS)
-- Database creation & seed script
-- Matches schema expected by com.pims.db.DBConnection (database: pims_db)
-- ============================================================

DROP DATABASE IF EXISTS pims_db;
CREATE DATABASE pims_db;
USE pims_db;

-- ------------------------------------------------------------
-- Table: users
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('Admin', 'Cashier') NOT NULL,
    full_name   VARCHAR(100) NOT NULL
);

-- ------------------------------------------------------------
-- Table: suppliers
-- ------------------------------------------------------------
CREATE TABLE suppliers (
    supplier_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT
);

-- ------------------------------------------------------------
-- Table: medicines
-- ------------------------------------------------------------
CREATE TABLE medicines (
    medicine_id        INT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(150) NOT NULL,
    company             VARCHAR(100),
    medicine_type       VARCHAR(50),
    price               DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantity_in_stock   INT NOT NULL DEFAULT 0,
    reorder_level       INT NOT NULL DEFAULT 0,
    expiry_date         DATE,
    supplier_id         INT,
    CONSTRAINT fk_medicines_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- Table: sales
-- ------------------------------------------------------------
CREATE TABLE sales (
    sale_id       INT AUTO_INCREMENT PRIMARY KEY,
    sale_date     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    user_id       INT,
    CONSTRAINT fk_sales_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- Table: sale_items
-- ------------------------------------------------------------
CREATE TABLE sale_items (
    sale_item_id    INT AUTO_INCREMENT PRIMARY KEY,
    sale_id         INT NOT NULL,
    medicine_id     INT NOT NULL,
    quantity_sold   INT NOT NULL,
    price_at_sale   DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_saleitems_sale
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_saleitems_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
);

-- ============================================================
-- Sample data
-- ============================================================

-- Default login accounts (plain-text passwords, per assignment spec)
INSERT INTO users (username, password, role, full_name) VALUES
('admin',    'admin123', 'Admin',   'System Administrator'),
('cashier1', 'cash123',  'Cashier', 'Thabo Nkosi'),
('cashier2', 'cash123',  'Cashier', 'Lindiwe Dube');

-- Suppliers
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('MedSupply SA',        'Johan van der Merwe', '011-555-0192', 'sales@medsupplysa.co.za', '12 Industria Rd, Johannesburg'),
('PharmaCorp Ltd',      'Aisha Patel',         '021-555-0143', 'orders@pharmacorp.co.za', '45 Main St, Cape Town'),
('Southern Cross Meds', 'David Botha',         '031-555-0177', 'info@southerncrossmeds.co.za', '8 Umgeni Rd, Durban');

-- Medicines (mix of well-stocked and low-stock items for demo)
-- Expiry dates for the two "soon-to-expire" rows below are inserted separately
-- as relative dates so the Expiry Report (next 30 days) always has data
-- regardless of when this script is actually run.
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Paracetamol 500mg',      'Adcock Ingram', 'Tablet',    25.50, 200,  50, '2027-06-30', 1),
('Amoxicillin 250mg',      'Aspen Pharma',  'Capsule',   89.99, 120,  40, '2027-03-15', 2),
('Ibuprofen 200mg',        'Cipla',         'Tablet',    32.00, 150,  50, '2027-04-20', 1),
('Vitamin C 1000mg',       'Pharma Dynamics','Tablet',   40.00, 300,  60, '2028-01-10', 1),
('Antacid Suspension',     'Adcock Ingram', 'Syrup',     28.90,  60,  25, '2027-02-14', 2),
('Metformin 500mg',        'Cipla',         'Tablet',    55.20, 100,  30, '2027-08-22', 1);

-- Low-stock demo rows (quantity_in_stock <= reorder_level)
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Cough Syrup 100ml',      'GSK',           'Syrup',     65.00,  8,  20, DATE_ADD(CURDATE(), INTERVAL 6 MONTH), 3),
('Hydrocortisone Cream',   'Aspen Pharma',  'Cream',     45.75,  5,  15, DATE_ADD(CURDATE(), INTERVAL 8 MONTH), 3),
('Salbutamol Inhaler',     'GSK',           'Injection', 120.00, 4,  10, DATE_ADD(CURDATE(), INTERVAL 7 MONTH), 3);

-- Soon-to-expire demo rows (fall within the next 30 days, whenever the script runs)
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Insulin Injection',      'Novo Nordisk',  'Injection', 350.00, 15,  10, DATE_ADD(CURDATE(), INTERVAL 10 DAY), 2),
('Amoxicillin Syrup 125mg','Aspen Pharma',  'Syrup',     42.00,  30,  20, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 2),
('Diclofenac Gel',         'Cipla',         'Cream',     38.50,  25,  15, DATE_ADD(CURDATE(), INTERVAL 3 DAY),  1);

-- Sample sales transactions (processed by cashier1, user_id 2) so the
-- Sales Report and Item-Wise Sales Report have demo data immediately.
INSERT INTO sales (total_amount, user_id) VALUES
(91.00, 2),
(89.99, 2),
(60.90, 3);

-- sale 1: Paracetamol x2 (medicine_id 1) + Vitamin C x1 (medicine_id 4)
INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES
(1, 1, 2, 25.50),
(1, 4, 1, 40.00);

-- sale 2: Amoxicillin x1 (medicine_id 2)
INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES
(2, 2, 1, 89.99);

-- sale 3: Ibuprofen x1 (medicine_id 3) + Antacid Suspension x1 (medicine_id 5)
INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES
(3, 3, 1, 32.00),
(3, 5, 1, 28.90);
