package banking;

public class Bank {
    // TODO HashMap<String, Account>

    public void openAccount(String id, double initialDeposit) throws InvalidAccountException {
        throw new UnsupportedOperationException("TODO");
    }

    public Account getAccount(String id) throws InvalidAccountException {
        throw new UnsupportedOperationException("TODO");
    }

    public void transfer(String fromId, String toId, double amount)
            throws InvalidAccountException, InsufficientFundsException {
        throw new UnsupportedOperationException("TODO");
    }
}
