package com.bcsim.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DefaultLink implements Link {
    private Node peerA;
    private Node peerB;
    private int timeout;
    private int range;

    public DefaultLink(int timeout, int range) {
        this.timeout = timeout;
        this.range = range;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public void addPeer(Node peer) {
        if (this.peerA == null) {
            this.peerA = peer;
        } else if (this.peerB == null) {
            this.peerB = peer;
        } else {
            throw new IllegalArgumentException("P2PLink supports only 2 peers");
        }
    }

    @Override
    public List<Node> getPeers() {
        ArrayList<Node> peerList = new ArrayList<Node>();
        peerList.add(peerA);
        peerList.add(peerB);

        return Collections.unmodifiableList(peerList);
    }

    @Override
    public void transfer(final Node from, final Message m) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int amount = timeout;

                    if (range > 0) {
                        timeout += new Random().nextInt(range);
                    }

                    Thread.sleep(amount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                if (peerA == from) {
                    peerB.receive(DefaultLink.this, m);
                } else if (peerB == from) {
                    peerA.receive(DefaultLink.this, m);
                } else {
                    throw new IllegalArgumentException("Cannot send message from foreign nodes");
                }
            }
        });
        t.setDaemon(false);
        t.start();
    }
}
