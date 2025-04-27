import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FarmerMarketApp extends JFrame {
    private Connection conn;
    private JTabbedPane tabs;
    private JTable productTable;
    private int nextPaymentId = 800;

    public FarmerMarketApp() {
        setTitle("Farmer Market Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectDatabase();

        tabs = new JTabbedPane();
        tabs.add("Farmers", farmerPanel());
        tabs.add("Products", productPanel());
        tabs.add("Markets", marketPanel());
        tabs.add("Payments", paymentPanel());
        tabs.add("Customers", customerPanel());

        add(tabs);
        setVisible(true);
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/adiproject", "root", "srikar2811");
            System.out.println("Database connected successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private JPanel farmerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2));

        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField marketId = new JTextField();
        JTextField productId = new JTextField();
        JTextField qty = new JTextField();

        form.add(new JLabel("Farmer ID")); form.add(id);
        form.add(new JLabel("Name")); form.add(name);
        form.add(new JLabel("Market ID")); form.add(marketId);
        form.add(new JLabel("Product ID")); form.add(productId);
        form.add(new JLabel("Quantity")); form.add(qty);

        JButton addBtn = new JButton("Add Farmer");
        addBtn.addActionListener(e -> {
            try {
                int mId = Integer.parseInt(marketId.getText());
                int pId = Integer.parseInt(productId.getText());

                PreparedStatement checkMarket = conn.prepareStatement("SELECT COUNT(*) FROM Market WHERE MarketID = ?");
                checkMarket.setInt(1, mId);
                ResultSet rsMarket = checkMarket.executeQuery();
                rsMarket.next();
                if (rsMarket.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Market ID does not exist.");
                    return;
                }

                PreparedStatement checkProduct = conn.prepareStatement("SELECT COUNT(*) FROM Product WHERE ProductID = ?");
                checkProduct.setInt(1, pId);
                ResultSet rsProduct = checkProduct.executeQuery();
                rsProduct.next();
                if (rsProduct.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Product ID does not exist.");
                    return;
                }

                PreparedStatement pst = conn.prepareStatement("INSERT INTO Farmer (Name, MarketID, ProductID, Quantity) VALUES (?, ?, ?, ?)");
                pst.setString(1, name.getText());
                pst.setInt(2, mId);
                pst.setInt(3, pId);
                pst.setInt(4, Integer.parseInt(qty.getText()));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Farmer added successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JTable table = new JTable();
        JButton refresh = new JButton("Refresh List");
        refresh.addActionListener(e -> loadTable("SELECT * FROM Farmer", table));

        panel.add(form, BorderLayout.NORTH);
        panel.add(addBtn, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
        panel.add(refresh, BorderLayout.EAST);
        return panel;
    }

    private JPanel productPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2));

        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JButton addBtn = new JButton("Add Product");

        addBtn.addActionListener(e -> {
            try {
                PreparedStatement pst = conn.prepareStatement("INSERT INTO Product VALUES (?, ?, ?)");
                pst.setInt(1, Integer.parseInt(id.getText()));
                pst.setString(2, name.getText());
                pst.setDouble(3, Double.parseDouble(price.getText()));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product added successfully.");
                loadTable("SELECT * FROM Product", productTable);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        form.add(new JLabel("Product ID")); form.add(id);
        form.add(new JLabel("Name")); form.add(name);
        form.add(new JLabel("Price")); form.add(price);
        form.add(addBtn);

        productTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(productTable);

        JButton refresh = new JButton("Refresh List");
        refresh.addActionListener(e -> loadTable("SELECT * FROM Product", productTable));

        panel.add(form, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);

        loadTable("SELECT * FROM Product", productTable);

        return panel;
    }

    private JPanel marketPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2));

        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField location = new JTextField();

        form.add(new JLabel("Market ID")); form.add(id);
        form.add(new JLabel("Market Name")); form.add(name);
        form.add(new JLabel("Location")); form.add(location);

        JButton addBtn = new JButton("Add Market");
        addBtn.addActionListener(e -> {
            try {
                PreparedStatement pst = conn.prepareStatement("INSERT INTO Market VALUES (?, ?, ?)");
                pst.setInt(1, Integer.parseInt(id.getText()));
                pst.setString(2, name.getText());
                pst.setString(3, location.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Market added successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JTable table = new JTable();
        JButton refresh = new JButton("Refresh List");
        refresh.addActionListener(e -> loadTable("SELECT * FROM Market", table));

        panel.add(form, BorderLayout.NORTH);
        panel.add(addBtn, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
        panel.add(refresh, BorderLayout.EAST);
        return panel;
    }

    private JPanel paymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2));

        JTextField customerId = new JTextField();
        JTextField marketId = new JTextField();
        JTextField productId = new JTextField();
        JTextField qty = new JTextField();

        form.add(new JLabel("Customer ID")); form.add(customerId);
        form.add(new JLabel("Market ID")); form.add(marketId);
        form.add(new JLabel("Product ID")); form.add(productId);
        form.add(new JLabel("Quantity")); form.add(qty);

        JButton addBtn = new JButton("Add Payment");
        addBtn.addActionListener(e -> {
            try {
                int cId = Integer.parseInt(customerId.getText());
                int mId = Integer.parseInt(marketId.getText());
                int pId = Integer.parseInt(productId.getText());
                int quantity = Integer.parseInt(qty.getText());

                PreparedStatement pstProduct = conn.prepareStatement("SELECT Price FROM Product WHERE ProductID = ?");
                pstProduct.setInt(1, pId);
                ResultSet rsProduct = pstProduct.executeQuery();

                if (rsProduct.next()) {
                    double price = rsProduct.getDouble("Price");
                    double totalAmount = price * quantity;

                    // Check available quantity
                    PreparedStatement checkQty = conn.prepareStatement("SELECT Quantity FROM Farmer WHERE MarketID = ? AND ProductID = ?");
                    checkQty.setInt(1, mId);
                    checkQty.setInt(2, pId);
                    ResultSet rsQty = checkQty.executeQuery();

                    if (rsQty.next()) {
                        int currentQty = rsQty.getInt("Quantity");
                        if (currentQty < quantity) {
                            JOptionPane.showMessageDialog(this, "Insufficient quantity available.");
                            return;
                        }

                        // Insert payment
                        PreparedStatement pstPayment = conn.prepareStatement("INSERT INTO Payment (PaymentID, CustomerID, MarketID, ProductID, Quantity, Amount) VALUES (?, ?, ?, ?, ?, ?)");
                        pstPayment.setInt(1, nextPaymentId++);
                        pstPayment.setInt(2, cId);
                        pstPayment.setInt(3, mId);
                        pstPayment.setInt(4, pId);
                        pstPayment.setInt(5, quantity);
                        pstPayment.setDouble(6, totalAmount);
                        pstPayment.executeUpdate();

                        // Decrement quantity in Farmer table
                        PreparedStatement updateQty = conn.prepareStatement("UPDATE Farmer SET Quantity = Quantity - ? WHERE MarketID = ? AND ProductID = ?");
                        updateQty.setInt(1, quantity);
                        updateQty.setInt(2, mId);
                        updateQty.setInt(3, pId);
                        updateQty.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Payment added successfully. Total Amount: " + totalAmount);
                    } else {
                        JOptionPane.showMessageDialog(this, "No farmer found with this Market and Product.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JTable table = new JTable();
        JButton refresh = new JButton("Refresh List");
        refresh.addActionListener(e -> loadTable("SELECT * FROM Payment", table));

        panel.add(form, BorderLayout.NORTH);
        panel.add(addBtn, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
        panel.add(refresh, BorderLayout.EAST);
        return panel;
    }

    private JPanel customerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2));

        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField address = new JTextField();

        form.add(new JLabel("Customer ID")); form.add(id);
        form.add(new JLabel("Name")); form.add(name);
        form.add(new JLabel("Address")); form.add(address);

        JButton addBtn = new JButton("Add Customer");
        addBtn.addActionListener(e -> {
            try {
                PreparedStatement pst = conn.prepareStatement("INSERT INTO Customer (Name, Address) VALUES (?, ?)");
                pst.setString(1, name.getText());
                pst.setString(2, address.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Customer added successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JTable table = new JTable();
        JButton refresh = new JButton("Refresh List");
        refresh.addActionListener(e -> loadTable("SELECT * FROM Customer", table));

        panel.add(form, BorderLayout.NORTH);
        panel.add(addBtn, BorderLayout.CENTER);
        panel.add(new JScrollPane(table), BorderLayout.SOUTH);
        panel.add(refresh, BorderLayout.EAST);
        return panel;
    }

    private void loadTable(String query, JTable table) {
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FarmerMarketApp::new);
    }
}