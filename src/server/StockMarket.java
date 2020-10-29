package server;

import java.util.ArrayList;

public class StockMarket implements Runnable{

    //ArrayList storing existing stock
    private static ArrayList<Stock> Stock; //TODO make into hashmap for more efficient searching

    public StockMarket(){
        Stock = new ArrayList<Stock>();
        Stock stock1 = new Stock("Sample stock", Stock.size()+1);
        this.Stock = new ArrayList<Stock>();
        Stock.add(stock1);
    }



    //Trades stock between traders c1 and c2, returns true if trade was successul.
    //C1 = current owner, C2 = buyer
    public boolean trade(Client c1, Client c2, Stock stockTrade){
        System.out.println("in trade ");

        /* UNCOMMENT FOR TESTING IF A TRADER DISCONNECTS MID-TRADE
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
           */

        //Check if the owner is also the buyer
        if (stockTrade.getOwner() == c2){
            System.out.println("Cannot sell to owner");
            return false;
        }else {
            c1.ownedStock.remove(stockTrade);
            c2.ownedStock.add(stockTrade);
            stockTrade.setOwner(c2);
            if (!(c1.ownedStock.contains(stockTrade))) {
                if (c2.ownedStock.contains(stockTrade)) {
                    if ((c1.isConnected()) && (c2.isConnected())){ //Check if traders are still connected before finalisation of trade.
                        return true;
                    }else{
                        //Revert trade to previous owner
                        c2.ownedStock.remove(stockTrade);
                        c1.ownedStock.add(stockTrade);
                        stockTrade.setOwner(c1);
                        return false;
                    }

                }
            }
        }

        return false;
    }


    //Following functions used to search stock and return a Stock object
    //Returns Stock object matching name
    public Stock getStock(String name){
        for (int i = 0; i < Stock.size(); i++){
            if (Stock.get(i).name == name){
                return Stock.get(i);
            }
        }return null;
    }
    //Returns Stock object matching name
    public static Stock getStock(int ID){
        for (int i = 0; i < Stock.size(); i++){
            if (Stock.get(i).stockID == ID){
                return Stock.get(i);
            }
        }return null;
    }

    @Override
    public void run() {}
}
