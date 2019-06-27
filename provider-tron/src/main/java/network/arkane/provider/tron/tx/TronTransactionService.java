package network.arkane.provider.tron.tx;

import network.arkane.provider.chain.SecretType;
import network.arkane.provider.tx.TransactionService;
import network.arkane.provider.tx.TxInfo;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
public class TronTransactionService implements TransactionService {


    public SecretType type() {
        return SecretType.TRON;
    }

    @Override
    public TxInfo getTransaction(String hash) {
        throw new NotImplementedException("This feature is not available yet for " + type().name());
    }
}
