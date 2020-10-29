package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Main {

    private static ServerSocket serverSocket;
    public static ArrayList<Client> clients;

    public static void main(String[] args) {

        clients = new ArrayList<Client>();

	    try{
	        startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public static void startServer() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("Server up and waiting for connections...");

            //Assigns stock market to a thread
            new Thread(new StockMarket()).start();

            while (true) {
                Socket socket = serverSocket.accept();
                Client client = new Client(socket, clients.size()+1);

                clients.add(client);

                //first client to connect gets stock
                if (clients.size() == 1){
                    Stock sampleStock = StockMarket.getStock(1);
                    sampleStock.setOwner(client);
                    client.ownedStock.add(sampleStock);
                }
                new Thread(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
