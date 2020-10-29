package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Client implements Runnable{

    private Socket socket;
    private UUID ID;
    private String username;
    private PrintWriter writer;
    private BufferedReader in;
    public ArrayList<Stock> ownedStock;


    //Client object
    public Client(Socket socket, String name) {
        try {
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            System.out.println("Error creating I/O");
        }
        this.username = setUsername();
        this.ownedStock = new ArrayList<Stock>();
        this.socket = socket;
        this.ID = UUID.randomUUID();
    }

    public String setUsername(){
        String setName = "noName";
        try {
            setName = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return setName;
    }

    public String getUsername(){
        return this.username;
    }

    public UUID getID(){
        return this.ID;
    }

    private void buyStock(){
        //TODO Buy stock
        // TODO might need to reorganise project so Stock market variable is accessable, as Main.market has not been initialised
        System.out.println("buy stock");
        //testing id = 1
        Stock stockToBuy = StockMarket.getStock(1);
        if (stockToBuy != null){
            if (stockToBuy.hasOwner()){
                Client owner = stockToBuy.getOwner();
                System.out.println("trade");
                System.out.println("owner: " + owner.getUsername());
                System.out.println("buyer: " + this.getUsername());
                System.out.println("Stock: " + stockToBuy.name);
                System.out.println("Market: " + Main.market);

                if(Main.market.trade(owner, this, stockToBuy)){
                    System.out.println("Trade successful");
                }else{
                    System.out.println("Trade unsuccessful");
                }
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

    public void sendMessage(String msg){
        writer.println(msg);
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

    public void quit(){
        System.out.println("User: " + this.username + " quitting");
        Main.broadcast("User: " + this.username + " left");
        try {
            socket.close();
            Main.clients.remove(this);
        } catch (IOException e) {
            System.out.println("Error closing socket");
        }
    }

    @Override
    public void run() {
        boolean running = true;
        try {
            //Scanner scanner = new Scanner(socket.getInputStream());

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
                                    System.out.println("fff");
                                    break;
                                case "balance":
                                    balance();
                                    break;
                                case "quit":
                                    quit();
                                    break;
                            }
                        }
                        running = false;

                    } catch (IOException e) {
                        System.out.println("User disconnected");
                        running = false;
                    }

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error");
            System.exit(1);
        }
    }
}
