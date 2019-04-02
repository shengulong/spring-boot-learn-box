/**
 * Copyright(C) 2019 Hangzhou zhaoyunxing Technology Co., Ltd. All rights reserved.
 */
package io.github.xyz.spring.boot.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author zhaoyunxing
 * @date: 2019-04-02 14:29
 * @des:
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        //添加依块去写
        pipeline.addLast(new ChunkedWriteHandler());
        /**
         * Creates a new instance.
         * @param maxContentLength the maximum length of the aggregated content in bytes.
         * If the length of the aggregated content exceeds this value,
         * {@link #handleOversizedMessage(ChannelHandlerContext, HttpMessage)} will be called.
         */
        pipeline.addLast(new HttpObjectAggregator(8192));
        //添加websocket
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler());

    }
}
