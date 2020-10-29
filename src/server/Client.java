package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Runnable {

    private Socket socket;
    private int ID;
    private PrintWriter out;
    private PrintWriter writer;
    private BufferedReader in;
    public ArrayList<Stock> ownedStock;

    public Client(Socket socket, int ID) {
        this.ownedStock = new ArrayList<Stock>();
        this.socket = socket;
        this.ID = ID;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            System.out.println("Error creating I/O");
        }
    }

    private void buyStock(){
        //TODO Buy stock
        System.out.println("buy stock");
        //testing id = 1
        Stock stockToBuy = StockMarket.getStock(1);
        if (stockToBuy != null){
            if (stockToBuy.hasOwner()){
                System.out.println("trade");
                //TODO trade
            }else{
                System.out.println("doesnt have owner"); //if owner has disconnected
                stockToBuy.setOwner(this);
                this.ownedStock.add(stockToBuy);
            }
        }else if (stockToBuy == null){
            System.out.println("Stock doesnt exist");
        }

    }

    private void sellStock(){
        //TODO sell stock
        System.out.println("Sell stock");
    }

    private void balance(){
        //TODO show balance
        writer.println("Trader balance for trader ID: " + ID);
        writer.println("Stock size: " + ownedStock.size());
        //for (Stock Stock : ownedStock){
            //writer.println("Stock: " + Stock.name);
        //}
    }

    @Override
    public void run() {
        boolean running = true;
        try {
            //Scanner scanner = new Scanner(socket.getInputStream());
            this.writer = new PrintWriter(socket.getOutputStream(), true);

            try {
                System.out.println("Client connected, ID: " + this.ID);




                while (running) {

                    String inputLine;
                    try {

                        while ((inputLine = in.readLine()) != null) {
                            System.out.println(inputLine);
                            switch (inputLine){
                                case "sell":
                                    System.out.println("sell found");
                                    break;
                                case "buy":
                                    System.out.println("buy found");
                                    buyStock();
                                    break;
                                case "balance":
                                    balance();
                                    break;
                            }
                        }
                        running = false;

                    } catch (IOException e) {
                        System.out.println("User disconnected");
                        running = false;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                writer.println("poo ERROR " + e.getMessage());
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error");
            System.exit(1);
        }
    }
}
