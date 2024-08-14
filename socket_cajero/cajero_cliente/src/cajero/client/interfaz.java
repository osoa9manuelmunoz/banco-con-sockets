package cajero.client;

import cajero.java_client_socket.JavaClientSocket;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class interfaz {
    private JFrame frame;
    private JPanel cardEntryPanel, menuPanel, passwordEntryPanel, transactionPanel, balancePanel, withdrawalMessagePanel, receiptPanel;
    private JTextField cardNumberField, transactionAmountField;
    private JPasswordField passwordField;
    private JLabel transactionTitleLabel, balanceAmountLabel, initialAmountLabel, withdrawnAmountLabel, finalAmountLabel;

    private String currentCardNumber;
    private String transactionType;
    private double initialAmount;
    private double withdrawnAmount;
    private Client client;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                interfaz window = new interfaz();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public interfaz() {
        // Iniciar el cliente de sockets
        JavaClientSocket javaSocket = new JavaClientSocket("localhost", 1802);
        client = javaSocket.getClient();

        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new CardLayout());

        // Panel de ingreso de tarjeta
        cardEntryPanel = new JPanel();
        frame.getContentPane().add(cardEntryPanel, "cardEntryPanel");
        cardEntryPanel.setLayout(new GridLayout(3, 1));
        JLabel cardEntryLabel = new JLabel("Ingrese su número de tarjeta", SwingConstants.CENTER);
        cardEntryPanel.add(cardEntryLabel);
        cardNumberField = new JTextField();
        cardEntryPanel.add(cardNumberField);
        JButton enterCardButton = new JButton("Ingresar");
        enterCardButton.addActionListener(e -> enterCard(cardNumberField.getText()));
        cardEntryPanel.add(enterCardButton);

        // Panel del menú
        menuPanel = new JPanel();
        frame.getContentPane().add(menuPanel, "menuPanel");
        menuPanel.setLayout(new GridLayout(5, 1));
        JLabel menuLabel = new JLabel("Menú", SwingConstants.CENTER);
        menuPanel.add(menuLabel);
        JButton depositButton = new JButton("Consignación");
        depositButton.addActionListener(e -> showTransaction("deposit"));
        menuPanel.add(depositButton);
        JButton balanceButton = new JButton("Consulta de saldo");
        balanceButton.addActionListener(e -> showTransaction("balance"));
        menuPanel.add(balanceButton);
        JButton creditButton = new JButton("Retiro con tarjeta de crédito");
        creditButton.addActionListener(e -> showTransaction("credit"));
        menuPanel.add(creditButton);
        JButton debitButton = new JButton("Retiro con tarjeta de débito");
        debitButton.addActionListener(e -> showTransaction("debit"));
        menuPanel.add(debitButton);

        // Panel de ingreso de contraseña
        passwordEntryPanel = new JPanel();
        frame.getContentPane().add(passwordEntryPanel, "passwordEntryPanel");
        passwordEntryPanel.setLayout(new GridLayout(3, 1));
        JLabel passwordEntryLabel = new JLabel("Ingrese su contraseña", SwingConstants.CENTER);
        passwordEntryPanel.add(passwordEntryLabel);
        passwordField = new JPasswordField();
        passwordEntryPanel.add(passwordField);
        JButton verifyPasswordButton = new JButton("Verificar");
        verifyPasswordButton.addActionListener(e -> verifyPassword(new String(passwordField.getPassword())));
        passwordEntryPanel.add(verifyPasswordButton);

        // Panel de transacción
        transactionPanel = new JPanel();
        frame.getContentPane().add(transactionPanel, "transactionPanel");
        transactionPanel.setLayout(new GridLayout(3, 1));
        transactionTitleLabel = new JLabel("", SwingConstants.CENTER);
        transactionPanel.add(transactionTitleLabel);
        transactionAmountField = new JTextField();
        transactionPanel.add(transactionAmountField);
        JButton processTransactionButton = new JButton("Confirmar");
        processTransactionButton.addActionListener(e -> processTransaction(transactionAmountField.getText()));
        transactionPanel.add(processTransactionButton);

        // Panel de saldo
        balancePanel = new JPanel();
        frame.getContentPane().add(balancePanel, "balancePanel");
        balancePanel.setLayout(new GridLayout(2, 1));
        JLabel balanceLabel = new JLabel("Su saldo es:", SwingConstants.CENTER);
        balancePanel.add(balanceLabel);
        balanceAmountLabel = new JLabel("", SwingConstants.CENTER);
        balancePanel.add(balanceAmountLabel);
        JButton backToMenuButton = new JButton("Volver al menú");
        backToMenuButton.addActionListener(e -> goBackToMenu());
        balancePanel.add(backToMenuButton);

        // Panel de mensaje de retiro
        withdrawalMessagePanel = new JPanel();
        frame.getContentPane().add(withdrawalMessagePanel, "withdrawalMessagePanel");
        withdrawalMessagePanel.setLayout(new GridLayout(4, 1));
        JLabel withdrawalMessageLabel = new JLabel("Retire su dinero", SwingConstants.CENTER);
        withdrawalMessagePanel.add(withdrawalMessageLabel);
        JButton viewReceiptButton = new JButton("Ver Factura");
        viewReceiptButton.addActionListener(e -> showReceipt());
        withdrawalMessagePanel.add(viewReceiptButton);
        JButton backToMenuButton2 = new JButton("Volver al menú");
        backToMenuButton2.addActionListener(e -> goBackToMenu());
        withdrawalMessagePanel.add(backToMenuButton2);
        JButton restartButton = new JButton("Finalizar");
        restartButton.addActionListener(e -> restart());
        withdrawalMessagePanel.add(restartButton);

        // Panel de recibo
        receiptPanel = new JPanel();
        frame.getContentPane().add(receiptPanel, "receiptPanel");
        receiptPanel.setLayout(new GridLayout(4, 1));
        JLabel receiptLabel = new JLabel("Factura", SwingConstants.CENTER);
        receiptPanel.add(receiptLabel);
        initialAmountLabel = new JLabel("Monto inicial: ", SwingConstants.CENTER);
        receiptPanel.add(initialAmountLabel);
        withdrawnAmountLabel = new JLabel("Monto retirado: ", SwingConstants.CENTER);
        receiptPanel.add(withdrawnAmountLabel);
        finalAmountLabel = new JLabel("Saldo final: ", SwingConstants.CENTER);
        receiptPanel.add(finalAmountLabel);
        JButton backToMenuButton3 = new JButton("Volver al menú");
        backToMenuButton3.addActionListener(e -> goBackToMenu());
        receiptPanel.add(backToMenuButton3);
    }

    public void enterCard(String cardNumber) {
        currentCardNumber = cardNumber;
        List<Object> requestData = new ArrayList<>();
        requestData.add(cardNumber);
        requestData.add(""); // Contraseña vacía
        requestData.add("validateCard");
        requestData.add(0.0); // Monto irrelevante
    
        if (client.response(requestData)) {
            List<Object> response = client.listen();
            if (response != null && !response.isEmpty()) {
                if (response.get(0).equals("Card valid")) {
                    showPanel("menuPanel");
                } else {
                    JOptionPane.showMessageDialog(frame, "Número de tarjeta no válido");
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Error al comunicarse con el servidor.");
        }
    }

    public void showTransaction(String type) {
        transactionType = type;
        String title = "";
        switch (type) {
            case "deposit":
                title = "Consignación";
                break;
            case "credit":
            case "debit":
                title = "Retiro";
                break;
            case "balance":
                title = "Consulta de saldo";
                break;
        }
        setTransactionTitle(title);
        showPanel("passwordEntryPanel");
    }

    public void verifyPassword(String password) {
        // Solicitar verificación de contraseña al servidor
        List<Object> requestData = new ArrayList<>();
        requestData.add(currentCardNumber);
        requestData.add(password);
        requestData.add("verifyPassword");
        requestData.add(0.0); // Monto irrelevante para la verificación de la contraseña

        client.response(requestData);
        List<Object> response = client.listen();

        if (response.get(0).equals("Password valid")) {
            if (transactionType.equals("balance")) {
                showBalance();
            } else {
                showPanel("transactionPanel");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Contraseña incorrecta");
        }
    }

    public void processTransaction(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                throw new NumberFormatException();
            }

            List<Object> requestData = new ArrayList<>();
            requestData.add(currentCardNumber);
            requestData.add(""); // Contraseña ya ha sido validada
            requestData.add(transactionType);
            requestData.add(amount);

            client.response(requestData);
            List<Object> response = client.listen();

            if (response.get(0).equals("Transaction successful")) {
                if (transactionType.equals("withdraw")) {
                    withdrawnAmount = amount;
                }
                showPanel("withdrawalMessagePanel");
            } else {
                JOptionPane.showMessageDialog(frame, response.get(0).toString());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Por favor, ingrese un monto válido.");
        }
    }

    private void showBalance() {
        // Solicitar saldo al servidor
        List<Object> requestData = new ArrayList<>();
        requestData.add(currentCardNumber);
        requestData.add(""); // Contraseña ya ha sido validada
        requestData.add("balance");
        requestData.add(0.0); // Monto irrelevante para la consulta de saldo

        client.response(requestData);
        List<Object> response = client.listen();

        setBalanceAmount(response.get(0).toString());
        showPanel("balancePanel");
    }

    public void showReceipt() {
        setInitialAmount(String.valueOf(initialAmount));
        setWithdrawnAmount(String.valueOf(withdrawnAmount));
        // La cuenta actual se debería actualizar después de la transacción
        // Asegúrate de obtener el saldo actualizado desde el servidor
        showPanel("receiptPanel");
    }

    public void goBackToMenu() {
        showPanel("menuPanel");
    }

    public void restart() {
        transactionType = "";
        showPanel("cardEntryPanel");
    }

    public void showPanel(String panelName) {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), panelName);
    }

    public void setTransactionTitle(String title) {
        transactionTitleLabel.setText(title);
    }

    public void setBalanceAmount(String amount) {
        balanceAmountLabel.setText(amount);
    }

    public void setInitialAmount(String amount) {
        initialAmountLabel.setText("Monto inicial: " + amount);
    }

    public void setWithdrawnAmount(String amount) {
        withdrawnAmountLabel.setText("Monto retirado: " + amount);
    }

    public void setFinalAmount(String amount) {
        finalAmountLabel.setText("Saldo final: " + amount);
    }
}