package network.arkane.provider.sign;

import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import network.arkane.provider.secret.generation.AeternitySecretKey;
import network.arkane.provider.sign.domain.Signature;
import network.arkane.provider.sign.domain.TransactionSignature;
import org.bouncycastle.crypto.CryptoException;

public class AeternitySpendTransactionSigner implements Signer<AeternitySpendTransactionSignable, AeternitySecretKey> {

    private final TransactionService transactionService = new TransactionServiceFactory().getService();

    @Override
    public Signature createSignature( final AeternitySpendTransactionSignable signable, final AeternitySecretKey key ) {
        UnsignedTx unsignedTx = transactionService.createSpendTx( signable.getSender(), signable.getRecipient(), signable.getAmount(), signable.getPayload(), signable.getFee(), signable.getTtl(), signable.getNonce() ).blockingSingle();
        BaseKeyPair baseKeyPair = EncodingUtils.createBaseKeyPair(key.getKeyPair());
        try {
            Tx tx = transactionService.signTransaction(unsignedTx, baseKeyPair.getPrivateKey());
            return TransactionSignature.signTransactionBuilder().signedTransaction(tx.getTx()).build();
        } catch ( CryptoException e ) {
            return null;
        }
    }
}
