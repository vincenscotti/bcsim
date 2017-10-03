package com.bcsim.core;

public class BlockRequestMessage implements Message {
    private byte[] hash;

    public BlockRequestMessage(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getHash() {
        return hash;
    }

    @Override
    public boolean visit(MessageVisitor v) {
        return v.visit(this);
    }

    @Override
    public Message duplicate() {
        return new BlockRequestMessage(hash.clone());
    }
}
