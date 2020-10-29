package server;

public class Stock {
    public Client owner;
    public String name;
    public int stockID;

    public Stock(String name, int ID){
        this.name = name;
        this.owner = null;
        this.stockID = ID;
    }


    public boolean hasOwner(){
        if (this.owner == null){
            return false;
        }else{
            return true;
        }
    }

    //Set first owner of stock: returns true if successful, false if failed.
    public boolean setOwner(Client newOwner){
        this.owner = newOwner;

        //check
        if (this.owner == newOwner){
            return true;
        }else{
            return false;
        }

    }
}
