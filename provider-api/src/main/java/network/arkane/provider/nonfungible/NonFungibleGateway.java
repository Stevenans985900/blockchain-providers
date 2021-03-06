package network.arkane.provider.nonfungible;

import network.arkane.provider.chain.SecretType;
import network.arkane.provider.nonfungible.domain.NonFungibleAsset;
import network.arkane.provider.nonfungible.domain.NonFungibleContract;

import java.util.List;

public interface NonFungibleGateway {

    SecretType getSecretType();

    List<NonFungibleAsset> listNonFungibles(String walletId, String... contractAddresses);

    NonFungibleAsset getNonFungible(String contractAddress, String tokenId);

    NonFungibleContract getNonFungibleContract(String contractAddress);
}
