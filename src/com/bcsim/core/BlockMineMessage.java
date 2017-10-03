package com.bcsim.core;

public class BlockMineMessage implements Message {
    private Block block;

    public BlockMineMessage(Block block) {
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
        return new BlockMineMessage(block.duplicate());
    }
}
