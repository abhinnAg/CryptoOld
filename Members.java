package DSCoinPackage;

import java.util.*;

import HelperClasses.MerkleTree;
import HelperClasses.Pair;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    if (mycoins.size() == 0) return;
    else{
      Pair<String,TransactionBlock> detail = mycoins.remove(0);
      Transaction tobj = new Transaction();
      tobj.coinID = detail.get_first();
      tobj.coinsrc_block = detail.get_second();
      for (Members m : DSobj.memberlist){
        if (m.UID == destUID){
          tobj.Destination = m;
          break;
        }
      }
      tobj.Source = this;
      for(int i = 0; i < in_process_trans.length; ++i){
        if (in_process_trans[i] == null){
          in_process_trans[i] = tobj;
          break;
        }
      }
      DSobj.pendingTransactions.AddTransactions(tobj);
    }
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    TransactionBlock source = DSObj.bChain.lastBlock;
    while (source != null){
      boolean found = false;
      for (Transaction t : source.trarray){
        if (t == tobj) {
          found = true;
          break;
        }
      }
      if (found) break;
      source = source.previous;
    }
    if (source == null) throw new MissingTransactionException();

    MerkleTree merkleTree = new MerkleTree();
    merkleTree.Build(source.trarray);
    List<Pair<String,String>> siblinPairs = new ArrayList<>();
    merkleTree.siblingCoupledList(siblinPairs, tobj, merkleTree.rootnode);
    siblinPairs.add(new Pair<String,String>(merkleTree.rootnode.val, null));

    List<Pair<String,String>> blockInfo = new ArrayList<>();
    TransactionBlock transactionBlock = DSObj.bChain.lastBlock;
    do{
      Pair<String,String> temp = new Pair<String,String>(transactionBlock.dgst, 
      transactionBlock.previous.dgst + "#" + transactionBlock.trsummary + "#" + transactionBlock.nonce);
      blockInfo.add(temp);
      transactionBlock = transactionBlock.previous;
    }
    while (transactionBlock != source.previous);
    blockInfo.add(new Pair<String,String>(transactionBlock.dgst, null));
    Collections.reverse(blockInfo);

    for(int i = 0; i < in_process_trans.length; ++i){
      if (in_process_trans[i] == tobj){
        in_process_trans[i] = null;
        break;
      } 
    }

    tobj.Destination.mycoins.add(new Pair<String,TransactionBlock>(tobj.coinID, null));

    return new Pair<List<Pair<String,String>>,List<Pair<String,String>>>(siblinPairs, blockInfo);
  }

  public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException {
    Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
    Set<String> s = new HashSet<>();
    int added = 0;
    while(added < DSObj.bChain.tr_count - 1){
      Transaction transaction = DSObj.pendingTransactions.RemoveTransaction();
      if (s.contains(transaction.coinID)) continue;
      s.add(transaction.coinID);
      trarray[added] = transaction;
      added += 1;
    }
    Transaction minerReward = new Transaction();
    minerReward.coinID = Long.toString(Long.parseLong(DSObj.latestCoinID) + 1);
    DSObj.latestCoinID = minerReward.coinID;
    minerReward.Destination = this;
    minerReward.Source = null;
    minerReward.coinsrc_block = null;

    trarray[trarray.length-1] = minerReward;

    TransactionBlock transactionBlock = new TransactionBlock(trarray);
    DSObj.bChain.InsertBlock_Honest(transactionBlock);
  }  

  public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {
    Transaction[] trarray = new Transaction[DSObj.bChain.tr_count];
    Set<String> s = new HashSet<>();
    int added = 0;
    while(added < DSObj.bChain.tr_count - 1){
      Transaction transaction = DSObj.pendingTransactions.RemoveTransaction();
      if (s.contains(transaction.coinID)) continue;
      s.add(transaction.coinID);
      trarray[added] = transaction;
      added += 1;
    }
    Transaction minerReward = new Transaction();
    minerReward.coinID = Long.toString(Long.parseLong(DSObj.latestCoinID) + 1);
    DSObj.latestCoinID = minerReward.coinID;
    minerReward.Destination = this;
    minerReward.Source = null;
    minerReward.coinsrc_block = null;

    trarray[trarray.length-1] = minerReward;

    TransactionBlock transactionBlock = new TransactionBlock(trarray);
    DSObj.bChain.InsertBlock_Malicious(transactionBlock);
  }  
}
