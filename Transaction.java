package DSCoinPackage;

public class Transaction {

  public String coinID;
  public Members Source;
  public Members Destination;
  public TransactionBlock coinsrc_block;

  public Transaction(){
    
  }

  public Transaction(Transaction t){
    this.coinID = t.coinID;
    this.Source = t.Source;
    this.Destination = t.Destination;
    this.coinsrc_block = t.coinsrc_block;
  }
  
}

