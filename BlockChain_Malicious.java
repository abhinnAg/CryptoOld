package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public String calculateNonce(String dgst, String trSummary){
    String nonce = "1000000001";
    CRF obj = new CRF(64);
    String a = obj.Fn(dgst + "#" + trSummary + "#" + nonce);
    while(!a.startsWith("0000")){
      long longValue = Long.parseLong(a) + 1;
      nonce = Long.toString(longValue);
      a = obj.Fn(dgst + "#" + trSummary + "#" + nonce);
    }
    return nonce;
  }

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    if (!tB.dgst.startsWith("0000")) return false;

    CRF obj = new CRF(64);
    String d = start_string;
    if (tB.previous != null) d = tB.previous.dgst;
    d = obj.Fn(d + "#" + tB.trsummary + "#" + tB.nonce);
    if (d != tB.dgst) return false;

    MerkleTree temp = new MerkleTree();
    String correct = temp.Build(tB.trarray);
    if (!correct.equals(tB.trsummary)) return false;

    for (Transaction transaction : tB.trarray){
      if (!tB.checkTransaction(transaction)) return false;
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int longest = 0;
    TransactionBlock longestChainLastBlock = null;
    for (TransactionBlock transactionBlock : lastBlocksList){
      TransactionBlock temp = transactionBlock;
      TransactionBlock last = null;
      int chainLength = 0;
      while(temp!= null){
        if(checkTransactionBlock(temp)){
          chainLength += 1;
          if (last == null){
            last = temp;
          }
        }
        else{
          chainLength = 0;
          last = null;
        }
        temp = temp.previous;
      }
      if (chainLength > longest){
        longestChainLastBlock = last;
        longest = chainLength;
      }
    }
    return longestChainLastBlock;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    TransactionBlock last = FindLongestValidChain();
    if (last == null){
      newBlock.dgst = start_string;
      newBlock.nonce = calculateNonce(start_string, newBlock.trsummary);
    }
    else{
      CRF obj = new CRF(64);
      newBlock.nonce = calculateNonce(last.dgst, newBlock.trsummary);
      newBlock.dgst = obj.Fn(last.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
    }
    newBlock.previous = last;
    if (last != null){
      for (int i = 0; i < lastBlocksList.length; ++i){
        if (lastBlocksList[i] == last){
          lastBlocksList[i] = newBlock;
          break;
        }
        if (lastBlocksList[i] == null){
          lastBlocksList[i] = newBlock;
          break;
        }
      }
    }
    else{
      for (int i = 0; i < lastBlocksList.length; ++i){
        if (lastBlocksList[i] == null){
          lastBlocksList[i] = newBlock;
          break;
        }
      }      
    }
  }
}
