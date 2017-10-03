package com.bcsim.core;

public interface MessageVisitor {
    boolean visit(BlockInfoMessage m);
    boolean visit(BlockRequestMessage m);
    boolean visit(BlockMineMessage m);
}
