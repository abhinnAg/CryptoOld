package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

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

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    if (lastBlock == null){
      newBlock.dgst = start_string;
      newBlock.nonce = calculateNonce(newBlock.dgst, newBlock.trsummary);
    }
    else{
      CRF obj = new CRF(64);
      newBlock.nonce = calculateNonce(lastBlock.dgst, newBlock.trsummary);
      newBlock.dgst = obj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
    }
    newBlock.previous = lastBlock;
    lastBlock = newBlock;
  }
}
