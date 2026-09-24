# Inventory-management-

 HealthFirst Pharmacy Inventory Management System (PIMS)
 User Manual / README


1. WHAT THIS IS
------------------------------------------------------------------------
A Java Swing desktop application, backed by a MySQL database via JDBC,
that lets a pharmacy manage stock, process sales at the till, and
generate business reports. Two roles are supported: Admin and Cashier.

2. DEFAULT LOGIN CREDENTIALS
------------------------------------------------------------------------
    Admin account:
        Username: admin
        Password: admin123

    Cashier accounts:
        Username: cashier1   Password: cash123
        Username: cashier2   Password: cash123

3. REQUIREMENTS TO RUN
------------------------------------------------------------------------
    - Apache NetBeans (12.x or later) with the Java SE bundle
    - JDK 17 or later
    - MySQL Server (tested with MySQL 8.x, e.g. via XAMPP)
    - MySQL Connector/J (the JDBC driver jar) - see step 2 below

4. PROJECT STRUCTURE (standard NetBeans Ant "Java Application" layout)
------------------------------------------------------------------------
    PIMS/
      build.xml                <- Ant entry point (NetBeans regenerates
                                   nbproject/build-impl.xml the first
                                   time you open the project)
      manifest.mf
      nbproject/
        project.xml
        project.properties     <- main class, source/target level,
                                   library reference to the MySQL jar
      src/
        com/pims/
          Main.java
          db/DBConnection.java
          model/CartItem.java
          util/Session.java, UIStyle.java
          ui/LoginFrame.java, AdminDashboard.java, CashierDashboard.java,
             BillWindow.java
          ui/panels/MedicinePanel.java, SupplierPanel.java, UserPanel.java,
             POSPanel.java, StockCheckPanel.java, ReportsPanel.java
      lib/                     <- put mysql-connector-j.jar here (empty
                                   in this zip - see PUT_MYSQL_CONNECTOR
                                   _JAR_HERE.txt inside it)
      database/database.sql
      screenshots/
      README.txt

5. FIRST-TIME SETUP (NetBeans)
------------------------------------------------------------------------
    Step 1 - Add the MySQL JDBC driver
        Download the "Platform Independent" jar from
        https://dev.mysql.com/downloads/connector/j/ and copy it into
        this project's /lib folder, named exactly mysql-connector-j.jar
        (or update nbproject/project.properties to match your filename
        if you'd rather keep the version number).

    Step 2 - Open the project in NetBeans
        File > Open Project... and select the PIMS folder. NetBeans
        will detect it as a Java Application project and regenerate
        nbproject/build-impl.xml automatically. If it flags a "missing
        reference" for the MySQL jar, right-click it in the error
        dialog and point it at lib/mysql-connector-j.jar (this only
        happens if the jar wasn't already in /lib when you opened the
        project).

    Step 3 - Start MySQL
        Start your MySQL server (e.g. open XAMPP Control Panel and
        click "Start" next to MySQL).

    Step 4 - Create the database
        Open a MySQL client (phpMyAdmin, MySQL Workbench, or the
        command line) and run the script:
            database/database.sql
        This creates the "pims_db" database, all five tables, and
        inserts sample data: the login accounts above, three
        suppliers, thirteen medicines (including some low-stock and
        soon-to-expire items so the reports have data immediately),
        and three sample sales transactions.

    Step 5 - Check the DB connection settings
        Open src/com/pims/db/DBConnection.java and confirm the URL,
        USER and PASS constants match your local MySQL setup:
            URL:  jdbc:mysql://localhost:3306/pims_db
            USER: root
            PASS: root   (change this to your MySQL root password)

    Step 6 - Run
        Right-click the PIMS project in NetBeans' Projects pane and
        choose "Run" (or press F6). This runs com.pims.Main directly -
        no separate compile step needed.

5. USING THE APPLICATION
------------------------------------------------------------------------
    Login Screen
        Enter a username and password from section 2. Incorrect
        credentials show an on-screen error and do not proceed.

    Cashier Dashboard (login as cashier1 / cashier2)
        - Point of Sale (POS): search for a medicine, set a
          quantity, and add it to the cart. The cart shows a running
          total. Click "Checkout" to finalise the sale - stock
          levels are decremented automatically inside a database
          transaction, and a printable/saveable bill window opens.
          "Clear Cart" empties the current cart without selling.
        - Stock Check: look up a medicine's price and available
          quantity without adding it to a sale. Cashiers cannot add,
          edit, or delete medicines.

    Admin Dashboard (login as admin)
        - Manage Medicines: full Create / Read / Update / Delete for
          the medicine catalogue, including choosing a supplier from
          a dropdown.
        - Manage Suppliers: full CRUD for supplier records.
        - Manage Users: create, edit and delete Cashier (and Admin)
          accounts. Leave the password field blank when editing a
          user to keep their existing password unchanged.
        - Reports:
            * Sales Report - every transaction, who processed it,
              and a grand total.
            * Item-Wise Sales Report - units sold and revenue per
              medicine.
            * Low Stock Report - medicines at or below their reorder
              level.
            * Expiry Report - medicines expiring within the next 30
              days.

6. BUILDING A RUNNABLE JAR / .EXE FOR SUBMISSION
------------------------------------------------------------------------
    From NetBeans:
        Run > Clean and Build Project (Shift+F11). This produces
        dist/PIMS.jar plus dist/lib/mysql-connector-j.jar alongside it
        - copy both when you hand off the jar, or double-click PIMS.jar
        with the lib folder sitting next to it.

    To get a Windows .exe (the assignment asks for yourname_pims.exe):
        Open a terminal in the project folder after "Clean and Build"
        and run jpackage against the produced jar, e.g.:
            jpackage --input dist --main-jar PIMS.jar
                     --main-class com.pims.Main
                     --name yourname_pims --type exe
                     --icon icon.ico
        Rename the resulting yourname_pims.exe as required and include
        it in your submission zip alongside the MySQL connector jar
        NetBeans placed in dist/lib.

7. NOTES
------------------------------------------------------------------------
    - Passwords are stored and compared in plain text in this build,
      purely to keep the assignment's scope manageable - a production
      system should hash and salt passwords (e.g. with bcrypt).
    - The POS checkout uses row locking (SELECT ... FOR UPDATE) and a
      database transaction with rollback, so stock cannot go negative
      even if two cashiers try to sell the last unit at once.
    - UI colours and fonts are centralised in util/UIStyle.java so the
      whole application shares one consistent look and feel.
========================================================================
Built using Java
