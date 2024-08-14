package cajero.server;

import cajero_in.Account;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements SocketProcess {
    private ServerSocket serverSocket;
    private Session session;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public boolean bind() {
        try {
            Socket socket = this.serverSocket.accept();
            this.session = new Session(socket);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Object> listen() {
        List<Object> dataList = new ArrayList<>();
        boolean next = true;
        while (next) {
            Object data = this.session.read();
            if (data != null) {
                dataList.add(data);
                processRequest(dataList);
                next = false; 
            }
        }
        return dataList;
    }

    @Override
    public boolean response(List<Object> data) {
        for (Object d : data) {
            System.out.println("[Client] Enviando: " + d);
            if (!this.session.write(d)) {
                System.err.println("[Client]: Error al enviar datos.");
                return false;
            }
        }
        // No se espera respuesta en el servidor ya que se procesa y envía directamente.
        return true;
    }

    @Override
    public boolean close() {
        return this.session.close();
    }

    private Account findAccount(String cardNumber) {
        if (cardNumber.equals(Account.getTestAccount1().getCardNumber())) {
            return Account.getTestAccount1();
        } else if (cardNumber.equals(Account.getTestAccount2().getCardNumber())) {
            return Account.getTestAccount2();
        }
        return null;
    }

    @Override
    public void processRequest(List<Object> requestData) {
        if (requestData.size() != 4) {
            this.session.write("Error: Datos incompletos. Se esperaban 4 elementos.");
            return;
        }
        
        String cardNumber = (String) requestData.get(0);
        String password = (String) requestData.get(1);
        String transactionType = (String) requestData.get(2);
        double amount = (double) requestData.get(3);
    
        Account account = findAccount(cardNumber);
        if (account != null && account.getPassword().equals(password)) {
            try {
                switch (transactionType) {
                    case "deposit":
                        account.deposit(amount);
                        session.write("Transaction successful");
                        break;
                    case "withdraw":
                        account.withdraw(amount);
                        session.write("Transaction successful");
                        break;
                    case "balance":
                        session.write(account.getBalance());
                        break;
                    default:
                        session.write("Invalid transaction type");
                }
            } catch (Exception e) {
                session.write("Error in transaction: " + e.getMessage());
            }
        } else {
            session.write("Invalid card number or password");
        }
    }
}