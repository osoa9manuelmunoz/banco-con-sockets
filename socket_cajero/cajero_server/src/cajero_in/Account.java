package cajero_in;

public class Account {
    private String cardNumber;
    private double balance;
    private String password;  // Campo para la contraseña

    // Constructor que acepta número de tarjeta, saldo inicial y contraseña
    public Account(String cardNumber, double initialBalance, String password) {
        this.cardNumber = cardNumber;
        this.balance = initialBalance;
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPassword() {
        return password;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public void withdraw(double amount) throws Exception {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        } else {
            throw new Exception("Fondos insuficientes o cantidad inválida");
        }
    }

    // Método estático para obtener cuentas de prueba
    public static Account getTestAccount1() {
        return new Account("123456789", 1000.00, "1234");
    }

    public static Account getTestAccount2() {
        return new Account("987654321", 500.00, "5678");
    }
}