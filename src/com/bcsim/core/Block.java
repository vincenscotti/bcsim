package com.bcsim.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import java.util.Random;

public class Block {
    private byte[] hash;
    private byte[] previous;
    private PublicKey publicKey;
    private BlockData data;
    private boolean pow;
    private byte[] signature;

    private Block previousBlock;

    public Block() {
        previous = new byte[0];
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Block)) {
            return false;
        }

        Block o = (Block) other;

        return NodeUtils.compareHashes(hash, o.hash);
    }

    @Override
    public String toString() {
        return NodeUtils.bytesToHex(hash);
    }

    private byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            out.write(previous);
            out.write(publicKey.getEncoded());
            out.write(data.toBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public Block duplicate() {
        Block ret = new Block();

        ret.hash = hash.clone();
        ret.previous = previous.clone();
        ret.publicKey = publicKey;
        ret.data = new BlockData(data);
        ret.pow = pow;
        ret.signature = signature.clone();

        return ret;
    }

    public boolean isRoot() {
        for (byte b : previous) {
            if (b != 0) {
                return false;
            }
        }

        return true;
    }

    public boolean isValid() {
        return isValidBeforeMining() && pow;
    }

    public boolean isValidBeforeMining() {
        if (!NodeUtils.compareHashes(hash, calculateHash())) {
            return false;
        }

        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(data.toBytes());

            return signature.verify(this.signature);
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return false;
    }

    private byte[] calculateHash() {
        return NodeUtils.calculateHash(toBytes());
    }

    public void updateHash() {
        hash = calculateHash();
    }

    public void updateSignature(PrivateKey privkey) {
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privkey);
            signature.update(data.toBytes());

            this.signature = signature.sign();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getPrevious() {
        return previous;
    }

    public void setPrevious(byte[] previous) {
        this.previous = previous;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public BlockData getData() {
        return data;
    }

    public void setData(BlockData data) {
        this.data = data;
    }

    public boolean getPow() {
        return pow;
    }

    public void setPow(boolean pow) {
        this.pow = pow;
    }

    public byte[] getSignature() {
        return signature;
    }

    public Block getPreviousBlock() {
        return previousBlock;
    }

    public void setPreviousBlock(Block previousBlock) {
        this.previousBlock = previousBlock;
    }
}
