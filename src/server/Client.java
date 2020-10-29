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

                if(Main.market.trade(owner, this, stockToBuy)){
                    System.out.println("Trade successful");
                    Main.broadcast("Trade successful, owner of stock is: " + this.getUsername());
                }else{
                    sendMessage("Trade unsuccessful");
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

    public boolean isConnected(){
        if (socket.isClosed()){
            return false;
        }else{
            return true;
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

    //Could be modified to use a stock ID in a multi-stock market by taking stock id through arguement
    public void status(){
        Client stockOwner = Main.market.getStock(1).getOwner();
        if (stockOwner != null){
            writer.println("Stock owned by: " + stockOwner.getUsername());
        }else{
            writer.println("Stock not owned.");
        }


    }

    public void quit(){
        System.out.println("User: " + this.username + " quitting");
        Main.broadcast("User: " + this.username + " disconnected");
        Main.clients.remove(this);
        try {
            System.out.println("reseeting stock in quit function");
            for (int i = 0; i < ownedStock.size(); i++){
                Main.resetStock(this);
            }


            socket.close();
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
                                    buyStock();
                                    break;
                                case "balance":
                                    balance();
                                    break;
                                case "status":
                                    status();
                                    break;
                                case "quit":
                                    quit();
                                    break;
                            }
                        }
                        running = false;

                    } catch (IOException e) {
                        quit();
                        running = false;
                    }

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("Error");

            Main.resetStock(this);

            this.ownedStock.clear();
            System.exit(1);
        }
    }


    /*
    public void resetStock(){
        if (ownedStock.size() > 0){
            for(int i = 0; i < ownedStock.size(); i++){
                if (Main.clients.size() > 0){
                    System.out.println("setting owner");
                    Client newOwner = Main.clients.get(0);
                    Stock stock = ownedStock.get(i);
                    if(stock.setOwner(newOwner)){//As user disconnects, if they are the owner of the stock it will set to the first client in the connection array
                        System.out.println("New owner success");
                        System.out.println(stock.getOwner().username);
                    }else{
                        System.out.println("new owner failure.");
                    }

                    System.out.println("here");
                }else{
                    ownedStock.get(i).setOwner(null); //if there are no users connected, resets the stock ownership to null
                }

            }
        }
    }*/


}
