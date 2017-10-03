package com.bcsim.core;

public class BlockInfoMessage implements Message {
    private Block block;

    public BlockInfoMessage(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public boolean visit(MessageVisitor v) {
        return v.visit(this);
    }

    @Override
    public Message duplicate() {
        return new BlockInfoMessage(block.duplicate());
    }
}
