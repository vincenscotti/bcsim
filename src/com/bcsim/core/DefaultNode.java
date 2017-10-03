package com.bcsim.core;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

public class DefaultNode extends Observable implements Node, MessageVisitor, MinerAsyncCallback {
    private final String name;
    private final BlockChain bchain;
    private final List<Link> links;
    private final List<Message> history;
    private Miner miner;
    private final KeyPair key;
    private NodeLogger logger;

    public DefaultNode(KeyPair key, String name, Miner miner) {
        this.name = name;
        links = new ArrayList<Link>();
        bchain = new BlockChain();
        history = new ArrayList<Message>();
        this.miner = miner;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public BlockChain getBlockchain() {
        return bchain;
    }

    public Miner getMiner() {
        return miner;
    }

    public PublicKey getPublicKey() {
        return key.getPublic();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void addLink(Link l) {
        //l.addPeer(this);
        links.add(l);

        setChanged();
        notifyObservers();
    }

    @Override
    public List<Link> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public NodeLogger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(NodeLogger logger) {
        this.logger = logger;
    }

    @Override
    public synchronized void emit(BlockData bd, boolean broadcast) {
        byte[] prev = null;

        if (bchain.getMainHead() != null) {
            prev = bchain.getMainHead().getHash();
        }

        emit(bd, prev, broadcast);
    }

    @Override
    public synchronized void emit(BlockData bd, byte[] previous, boolean broadcast) {
        Block b = new Block();

        b.setData(bd);

        if (previous != null) {
            b.setPrevious(previous);
        }

        b.setPublicKey(key.getPublic());
        b.updateSignature(key.getPrivate());
        b.updateHash();

        BlockMineMessage mine = new BlockMineMessage(b);

        processBlockMine(mine);

        if (broadcast) {
            send(mine);
        }
    }

    public void send(Message m) {
        sendNotOn(null, m);
    }

    private void sendNotOn(Link exclude, Message m) {
        history.add(m);

        for (Link link : links) {
            if (link != exclude) {
                link.transfer(this, m.duplicate());
            }
        }
    }

    @Override
    public synchronized void receive(Link from, Message m) {
        if (!history.contains(m)) {
            if (m.visit(this)) {
                logger.println(this + " forwarding message " + m);
                sendNotOn(from, m);
            }

            notifyObservers();
        }
    }

    @Override
    public boolean visit(BlockInfoMessage m) {
        getLogger().println(this + " got block info message " + m);

        processBlockInfo(m);

        return true;
    }

    private void processBlockInfo(BlockInfoMessage m) {
        BlockChain bc = getBlockchain();
        Block b = bc.getBlockByHash(m.getBlock().getHash());

        if (b == null) {
            bc.add(m.getBlock());
            getMiner().stopMining(m.getBlock());
            setChanged();
        }

        if (!m.getBlock().isRoot()) {
            byte[] prevHash = m.getBlock().getPrevious();
            b = bc.getBlockByHash(prevHash);
            if (b == null) {
                send(new BlockRequestMessage(prevHash));
            }
        }
    }

    @Override
    public boolean visit(BlockRequestMessage m) {
        getLogger().println(this + " got block request message " + m);

        return !processBlockRequest(m);
    }

    private boolean processBlockRequest(BlockRequestMessage m) {
        Block b = getBlockchain().getBlockByHash(m.getHash());

        if (b != null) {
            send(new BlockInfoMessage(b));

            return true;
        }

        return false;
    }

    @Override
    public boolean visit(BlockMineMessage m) {
        getLogger().println(this + " got block mine message " + m);
        processBlockMine(m);

        return true;
    }

    private void processBlockMine(BlockMineMessage m) {
        getMiner().mine(m.getBlock());
    }

    @Override
    public void miningCompleted(Block b, int timeElapsed) {
        getLogger().println(this + " completed mine for " + b + " in " + timeElapsed + " ms");
        send(new BlockInfoMessage(b));
        processBlockInfo(new BlockInfoMessage(b));

        notifyObservers();
    }
}
