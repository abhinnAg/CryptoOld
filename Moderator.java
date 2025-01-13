package DSCoinPackage;

import HelperClasses.Pair;

public class Moderator
 {
  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) throws EmptyQueueException {
    Members moderator = new Members();
    TransactionQueue transactionQueue = new TransactionQueue();
    moderator.UID = "Moderator";
    for (int i = 0; i < coinCount; ++i){
      for (Members m : DSObj.memberlist){
        Transaction transaction = new Transaction();
        transaction.Source = moderator;
        transaction.Destination = m;
        transaction.coinID = Long.toString(Long.parseLong(DSObj.latestCoinID) + 1);
        transaction.coinsrc_block = null;
        DSObj.latestCoinID = transaction.coinID;
        transactionQueue.AddTransactions(transaction);
        i++;
      }
    }

    for (int i = 0; i < coinCount/(DSObj.bChain.tr_count); ++i){
      Transaction[] transactions = new Transaction[DSObj.bChain.tr_count];
      for (int j = 0; j < DSObj.bChain.tr_count; ++j){
        transactions[j] = transactionQueue.RemoveTransaction();
      }
      TransactionBlock transactionBlock = new TransactionBlock(transactions);
      DSObj.bChain.InsertBlock_Honest(transactionBlock);
      for (int j = 0; j < DSObj.bChain.tr_count; ++j){
        transactions[j].Destination.mycoins.add(new Pair<String,TransactionBlock>(transactions[j].coinID, transactionBlock));
      }
    }
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) throws EmptyQueueException {
    Members moderator = new Members();
    TransactionQueue transactionQueue = new TransactionQueue();
    moderator.UID = "Moderator";
    for (int i = 0; i < coinCount; ++i){
      for (Members m : DSObj.memberlist){
        Transaction transaction = new Transaction();
        transaction.Source = moderator;
        transaction.Destination = m;
        transaction.coinID = Long.toString(Long.parseLong(DSObj.latestCoinID) + 1);
        transaction.coinsrc_block = null;
        DSObj.latestCoinID = transaction.coinID;
        transactionQueue.AddTransactions(transaction);
        i++;
      }
    }

    for (int i = 0; i < coinCount/(DSObj.bChain.tr_count); ++i){
      Transaction[] transactions = new Transaction[DSObj.bChain.tr_count];
      for (int j = 0; j < DSObj.bChain.tr_count; ++j){
        transactions[j] = transactionQueue.RemoveTransaction();
      }
      TransactionBlock transactionBlock = new TransactionBlock(transactions);
      DSObj.bChain.InsertBlock_Malicious(transactionBlock);
      for (int j = 0; j < DSObj.bChain.tr_count; ++j){
        transactions[j].Destination.mycoins.add(new Pair<String,TransactionBlock>(transactions[j].coinID, transactionBlock));
      }
    }
  }
}
