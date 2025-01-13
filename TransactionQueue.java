package DSCoinPackage;

import java.util.LinkedList;
import java.util.Queue;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;
  public Queue<Transaction> queue = new LinkedList<>();

  public void AddTransactions (Transaction transaction) {
    if (firstTransaction == null){
      firstTransaction = transaction;
      numTransactions = 1;
    }
    lastTransaction = transaction;
    queue.add(transaction);
    numTransactions += 1;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if (queue.size() == 0) throw new EmptyQueueException();
    else{
      numTransactions -= 1;
      Transaction transaction = queue.poll();
      if (queue.size() == 0){
        this.firstTransaction = null;
        this.lastTransaction = null;
      }
      else{
        this.firstTransaction = queue.peek();
      }
      return transaction;
    }
  }

  public int size() {
    return queue.size();
  }
}
