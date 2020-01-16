package network.arkane.provider.sign;

import static network.arkane.provider.exceptions.ArkaneException.arkaneException;

import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import lombok.extern.slf4j.Slf4j;
import network.arkane.provider.secret.generation.AeternitySecretKey;
import network.arkane.provider.sign.domain.Signature;
import network.arkane.provider.sign.domain.TransactionSignature;
import org.bouncycastle.crypto.CryptoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AeternitySpendTransactionSigner implements
    Signer<AeternitySpendTransactionSignable, AeternitySecretKey> {

  private TransactionService transactionService;

  public AeternitySpendTransactionSigner(
      final @Qualifier("aeternity-transactionService") TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @Override
  public Signature createSignature(final AeternitySpendTransactionSignable signable,
      final AeternitySecretKey key) {
    AbstractTransaction<?> spendTx = transactionService.getTransactionFactory()
        .createSpendTransaction(signable.getSender(), signable.getRecipient(), signable.getAmount(),
            signable.getPayload(), signable.getTtl(), signable.getNonce());
    // the fee is optional because the SDK can calculate it automatically
    spendTx.setFee(signable.getFee());
    UnsignedTx unsignedTx = transactionService.createUnsignedTransaction(spendTx).blockingGet();
    BaseKeyPair baseKeyPair = EncodingUtils.createBaseKeyPair(key.getKeyPair());
    try {
      Tx tx = transactionService.signTransaction(unsignedTx, baseKeyPair.getPrivateKey());
      return TransactionSignature.signTransactionBuilder().signedTransaction(tx.getTx()).build();
    } catch (CryptoException e) {
      log.error("Unable to sign transaction: {}", e.getMessage());
      throw arkaneException()
          .errorCode("transaction.sign.internal-error")
          .errorCode("A problem occurred trying to sign the aeternity transaction")
          .cause(e)
          .build();
    }
  }
}