package com.bcsim.core;

import java.util.*;

public class BlockChain {
    private final List<Block> heads;
    private final Map<Block, Integer> headLengths;
    private final List<Block> orphans;
    private Block mainHead;

    public BlockChain() {
        heads = new ArrayList<Block>();
        headLengths = new HashMap<Block, Integer>();
        orphans = new ArrayList<Block>();
    }

    public List<Block> getHeads() {
        return Collections.unmodifiableList(heads);
    }

    public Block getMainHead() {
        return mainHead;
    }

    public void add(Block b) {
        if (!b.isValid()) {
            throw new IllegalArgumentException("Block is not valid");
        }

        orphans.add(b);

        orphanAdded();
    }

    public Block getBlockByHash(byte[] hash) {
        Block ptr;

        for (Block b : heads) {
            ptr = b;
            do {
                if (NodeUtils.compareHashes(hash, ptr.getHash())) {
                    return ptr;
                }

                ptr = ptr.getPreviousBlock();
            } while (ptr != null);
        }

        return null;
    }

    private void orphanAdded() {
        int nAdds;

        do {
            nAdds = 0;

            Iterator<Block> i = orphans.iterator();

            while (i.hasNext()) {
                Block b = i.next();

                if (b.isRoot()) {
                    heads.add(b);
                    headLengths.put(b, 1);
                    i.remove();
                    nAdds++;
                    continue;
                }

                Block prev = getBlockByHash(b.getPrevious());

                if (prev != null) {
                    int newsize;

                    b.setPreviousBlock(prev);

                    if (heads.contains(prev)) {
                        newsize = headLengths.get(prev) + 1;
                        headLengths.remove(prev);
                        heads.remove(prev);
                    } else {
                        newsize = calculateLength(b);
                    }

                    heads.add(b);
                    headLengths.put(b, newsize);

                    if (prev == mainHead) {
                        mainHead = b;
                    }

                    i.remove();

                    nAdds++;
                }
            }
        } while (nAdds > 0);

        updateMainHead();
        dropSideChains();
    }

    private int calculateLength(Block b) {
        int len = 0;

        while (b != null) {
            len++;
            b = b.getPreviousBlock();
        }

        return len;
    }

    private void updateMainHead() {
        int mainSize = 0;
        if (mainHead != null) {
            mainSize = headLengths.get(mainHead);
        }

        for (Map.Entry<Block, Integer> entry : headLengths.entrySet()) {
            if (entry.getValue() > mainSize) {
                mainHead = entry.getKey();
                mainSize = entry.getValue();
            }
        }
    }

    private void dropSideChains() {
        List<Block> toDrop = new ArrayList<Block>();

        int mainSize = 0;
        if (mainHead != null) {
            mainSize = headLengths.get(mainHead);
        }

        for (Map.Entry<Block, Integer> entry : headLengths.entrySet()) {
            if (entry.getValue() < mainSize - 1) {
                toDrop.add(entry.getKey());
            }
        }

        for (Block b : toDrop) {
            heads.remove(b);
            headLengths.remove(b);
        }
    }
}
