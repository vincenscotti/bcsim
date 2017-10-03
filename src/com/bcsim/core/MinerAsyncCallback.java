package com.bcsim.core;

public interface MinerAsyncCallback {
    void miningCompleted(Block b, int timeElapsed);
}
