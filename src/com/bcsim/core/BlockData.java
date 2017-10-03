package com.bcsim.core;

import java.nio.ByteBuffer;
import java.util.Date;

public class BlockData {
    private final String message;
    private final Date date;

    public BlockData(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public BlockData(BlockData other) {
        this(other.getMessage(), other.getDate());
    }

    @Override
    public String toString() {
        return message + " @ " + date.toString();
    }

    public byte[] toBytes() {
        ByteBuffer buff = ByteBuffer.allocate(message.getBytes().length + 8);

        return buff.put(message.getBytes()).putLong(date.getTime()).array();
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }
}
