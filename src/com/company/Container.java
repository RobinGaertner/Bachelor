package com.company;

public class Container {


    private PublicKey publicKey;
    private PrivateKeyRing privateKeyRing;

    public void setPrivateKeyRing(PrivateKeyRing privateKeyRing) {
        this.privateKeyRing = privateKeyRing;
    }

    public Container(PublicKey key, PrivateKeyRing ring) {
        this.publicKey = key;
        this.privateKeyRing = ring;
    }

    public PrivateKeyRing getPrivateKeyRing() {
        return privateKeyRing;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }


    @Override
    public String toString() {
        return "Containter{" +
                "publicKey=" + publicKey +
                ", privateKeyRing=" + privateKeyRing +
                '}';
    }
}
