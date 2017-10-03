package com.bcsim.core;

import java.util.List;

public interface Node {
    void addLink(Link l);
    List<Link> getLinks();
    NodeLogger getLogger();
    void setLogger(NodeLogger l);
    void emit(BlockData bd, boolean broadcast);
    void emit(BlockData bd, byte[] previous, boolean broadcast);
    void receive(Link from, Message m);
}
