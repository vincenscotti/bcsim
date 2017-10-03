package com.bcsim.core;

public interface Message {
    boolean visit(MessageVisitor v);
    Message duplicate();
}
