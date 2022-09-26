package de.florianmichael.tarasande

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import su.mandora.tarasande.util.math.TimeUtil

class ReceivedTracker {

    private var totalReceived = 0L
    private var received = 0L
    var receivedBps = 0L
    private var timer = TimeUtil()

    init {
        this.reset()
    }

    fun reset() {
        this.totalReceived = 0L
        this.received = 0L

        this.timer.reset()
    }

    fun handle(nBytes: Int) {
        this.totalReceived += nBytes;
        this.received += nBytes;

        if (this.timer.hasReached(1000L)) {
            this.receivedBps = this.received
            this.received = 0L
            this.timer.reset()
        }
    }
}

class TransmittedTracker {

    private var totalTransmitted = 0L
    private var transmitted = 0L
    var transmittedBps = 0L
    private var timer = TimeUtil()

    init {
        this.reset()
    }

    fun reset() {
        this.totalTransmitted = 0L
        this.transmitted = 0L

        this.timer.reset()
    }

    fun handle(nBytes: Int) {
        this.totalTransmitted += nBytes;
        this.transmitted += nBytes;

        if (this.timer.hasReached(1000L)) {
            this.transmittedBps = this.transmitted
            this.transmitted = 0L
            this.timer.reset()
        }
    }
}

class NettyStatsAdapter : ByteToMessageCodec<ByteBuf>() {

    private val incoming = ReceivedTracker()
    private val outgoing = TransmittedTracker()

    companion object {
        private var self: NettyStatsAdapter? = null

        fun get(): NettyStatsAdapter {
            self = NettyStatsAdapter()
            reset()
            return self!!
        }

        fun reset() {
            self!!.incoming.reset()
            self!!.outgoing.reset()
        }

        fun incoming(): Int {
            if (self == null)
                return 0

            return self!!.incoming.receivedBps.toInt()
        }

        fun outgoing(): Int {
            if (self == null)
                return 0

            return self!!.outgoing.transmittedBps.toInt()
        }
    }

    override fun encode(ctx: ChannelHandlerContext?, msg: ByteBuf?, out: ByteBuf?) {
        incoming.handle(msg!!.readableBytes())

        out!!.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext?, input: ByteBuf?, out: MutableList<Any>?) {
        outgoing.handle(input!!.readableBytes())
        val buf: ByteBuf = input.alloc().buffer(input.capacity())
        buf.writeBytes(input)

        out!!.add(buf)
    }
}
