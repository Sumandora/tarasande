package de.florianmichael.tarasande.util.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.MessageToMessageEncoder
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventPacketTransform

class EventDecoder : MessageToMessageDecoder<ByteBuf>() {

    override fun decode(ctx: ChannelHandlerContext?, msg: ByteBuf?, out: MutableList<Any>?) {
        TarasandeMain.get().managerEvent.call(EventPacketTransform(EventPacketTransform.Type.DECODE, msg))

        out?.add(msg!!.retain())
    }
}

class EventEncoder : MessageToMessageEncoder<ByteBuf>() {

    override fun encode(ctx: ChannelHandlerContext?, msg: ByteBuf?, out: MutableList<Any>?) {
        TarasandeMain.get().managerEvent.call(EventPacketTransform(EventPacketTransform.Type.ENCODE, msg))

        out?.add(msg!!.retain())
    }
}