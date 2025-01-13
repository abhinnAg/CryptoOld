package DSCoinPackage;

import HelperClasses.MerkleTree;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  public TransactionBlock(Transaction[] t) {
    MerkleTree merkleTree = new MerkleTree();
    if (trarray == null || trarray.length == 0){
      this.trarray = new Transaction[t.length];
      for (int i = 0; i < t.length; ++i){
        Transaction temp = new Transaction(t[i]);
        trarray[i] = temp;
      }
    }
    this.trsummary = merkleTree.Build(this.trarray);
    this.Tree = merkleTree;
    this.previous = null;
    this.dgst = null;
  }

  /*
   * Checking on 3 fronts - 
   * a) Positioning of the block. i.e the coinsrc_block is in the chain and before the current block.
   * b) The coinsrc_block has the transaction with the required conditions.
   * c) To ensure a case of no double spending between the blocks. We assume that a coin will not be double spent in the same block 
   * this will be checked before mining a new block.
   */

  public boolean checkTransaction (Transaction t) {
    TransactionBlock target = t.coinsrc_block;
    boolean check1 = positioning(this, target);
    boolean check2 = validBlock(target, t);
    boolean check3 = midSpending(target, t);
    return (check1 && check2 && check3);
  }

  public boolean positioning(TransactionBlock t, TransactionBlock coinsrc) {
    TransactionBlock temp = t;
    while (temp != null){
      if (temp == coinsrc) return true;
      temp = temp.previous;
    }
    return false;
  }

  public boolean validBlock(TransactionBlock tBlock, Transaction t){
    for(Transaction transaction : tBlock.trarray){
      if (transaction.coinID != t.coinID){
        continue;
      }
      else{
        if (t.Source == transaction.Destination){
          return true;
        }
      }
    }
    return false;   
  }

  public boolean spentAgain(TransactionBlock tBlock, Transaction t){
    for(Transaction transaction : tBlock.trarray){
      if (transaction.coinID == t.coinID && transaction.Source == t.Source) return true;
    }
    return false;
  }

  public boolean midSpending(TransactionBlock tBlock, Transaction t){
    if (this == tBlock) return true;
    else {
      TransactionBlock temp = this.previous;
      while(temp!= null && temp != tBlock) {
        if (spentAgain(temp, t)) return false;
        temp = temp.previous;
      }
    }
    return true;
  }
}
