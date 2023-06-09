package su.mandora.tarasande.util.connection

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.MessageToMessageEncoder
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacketTransform

class MessageToMessageDecoderEvent : MessageToMessageDecoder<ByteBuf>() {

    override fun decode(ctx: ChannelHandlerContext?, msg: ByteBuf?, out: MutableList<Any>?) {
        EventDispatcher.call(EventPacketTransform(EventPacketTransform.Type.DECODE, msg))

        out?.add(msg!!.retain())
    }
}

class MessageToMessageEncoderEvent : MessageToMessageEncoder<ByteBuf>() {

    override fun encode(ctx: ChannelHandlerContext?, msg: ByteBuf?, out: MutableList<Any>?) {
        EventDispatcher.call(EventPacketTransform(EventPacketTransform.Type.ENCODE, msg))

        out?.add(msg!!.retain())
    }
}
