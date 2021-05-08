package com.oxlog;

import java.io.Closeable;
import java.nio.ByteBuffer;

import static com.oxlog.utils.BufferUtils.marginToBuffer;
import static java.lang.System.arraycopy;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public interface Worker<T> extends Closeable {

    /**
     * 消息发送接口, 无阻塞
     * @param message
     * @return
     */
    boolean sendMessage(T message);

    void close();
}

class ByteEvent {
    private long id;
    private ByteBuffer buffer;
    private int bufferLen;

    public void clear(){
        if (nonNull(buffer)){
            buffer.clear();
        }
        this.id = 0L;
        this.bufferLen = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public int getBufferLen() {
        return bufferLen;
    }

    public void setBufferLen(int bufferLen) {
        this.bufferLen = bufferLen;
    }
}

abstract class ByteMessage {
    private long id;

    public abstract void apply(ByteEvent byteEvent);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

class DataByteMessage extends ByteMessage {
    private byte[] data;
    private int dataLength;

    public DataByteMessage(long id, byte[] data) {
        this.setId(id);
        this.data = data;
        this.dataLength = nonNull(data) ? data.length : 0;
    }

    public DataByteMessage(long id, byte[] data, int len) {
        this.setId(id);
        this.data = data;
        this.dataLength = len;
    }

    @Override
    public void apply(ByteEvent event) {
        event.clear();
        if (isNull(data) || 0 == dataLength){
            return;
        }
        event.setId(this.getId());
        ByteBuffer buffer = event.getBuffer();
        if (isNull(buffer) || buffer.capacity() < dataLength) {
            buffer = ByteBuffer.allocate(marginToBuffer(dataLength));
            event.setBuffer(buffer);
        }
        arraycopy(data, 0, buffer.array(), 0, dataLength);
        event.setBufferLen(dataLength);
    }
}
