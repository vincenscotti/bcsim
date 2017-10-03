package com.bcsim.core;

import java.util.Random;
import java.util.concurrent.*;

public class DefaultMiner implements Miner {
    private int timeout;
    private int range;
    private MinerAsyncCallback callback;
    private final Thread minerThread;
    private final BlockingQueue<Block> mineQueue;
    private final BlockingQueue<Block> skipQueue;

    public DefaultMiner(int timeout, int range) {
        this(null, timeout, range);
    }

    public DefaultMiner(MinerAsyncCallback callback, int timeout, int range) {
        this.timeout = timeout;
        this.range = range;
        this.callback = callback;

        mineQueue = new ArrayBlockingQueue<Block>(100);
        skipQueue = new ArrayBlockingQueue<Block>(100);

        minerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                    Block b;
                    while (true) {
                        try {
                            b = mineQueue.take();
                        } catch (InterruptedException e) {
                            continue;
                        }

                        int amount = DefaultMiner.this.timeout;

                        if (DefaultMiner.this.range > 0) {
                            amount += new Random().nextInt(DefaultMiner.this.range);
                        }

                        try {
                            Thread.sleep(amount);
                        } catch (InterruptedException e) {
                            if (skipQueue.contains(b)) {
                                skipQueue.clear();
                                continue;
                            }
                        }

                        b.setPow(true);
                        DefaultMiner.this.callback.miningCompleted(b, amount);
                    }
            }
        });
        minerThread.setDaemon(true);
        minerThread.start();
    }

    public void setCallback(MinerAsyncCallback callback) {
        this.callback = callback;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public void mine(Block b) {
        if (!b.isValidBeforeMining()) {
            throw new IllegalArgumentException("Block is not valid");
        }

        mineQueue.add(b);
    }

    @Override
    public void stopMining(Block b) {
        mineQueue.remove(b);
        skipQueue.add(b);
        minerThread.interrupt();
    }
}
