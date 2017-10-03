package com.bcsim.core;

import java.util.List;

public interface Link {
    void addPeer(Node peer);
    List<Node> getPeers();
    void transfer(Node from, Message m);
}
