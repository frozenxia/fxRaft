/**
 * @Title: TcpClient.java
 * @date:Sep 28, 2016 11:09:15 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.sample;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 *
 * @Description TODO
 * @date Sep 28, 2016 11:09:15 AM
 *
 */
public class TcpClient {
	private Channel channel;
	private Bootstrap b;
	EventLoopGroup group = new NioEventLoopGroup();

	public Channel connect(int port, String host) throws Exception {
		try {
			b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
					pipeline.addLast("frameEncode", new LengthFieldPrepender(4));
					pipeline.addLast("decode", new StringDecoder(CharsetUtil.UTF_8));
					pipeline.addLast("encode", new StringEncoder(CharsetUtil.UTF_8));
					pipeline.addLast(new SimpleChannelInboundHandler<Object>() {
						@Override
						protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
							// TODO Auto-generated method stub
							System.out.println("recieve buffer " + msg);
						}

						// @Override
						// public void channelActive(ChannelHandlerContext ctx)
						// throws Exception {
						// System.out.println("client active");
						// ctx.write("hello netty");
						// ctx.flush();
						// super.channelActive(ctx);
						// }
						//
						// @Override
						// public void channelInactive(ChannelHandlerContext
						// ctx) throws Exception {
						// System.out.println("channel inactive");
						// super.channelInactive(ctx);
						// }
					});
				}

			});
			this.channel = b.connect(host, port).sync().channel();
			return this.channel;
			// f.channel().closeFuture().sync();
		} finally {
			// group.shutdownGracefully();
		}

	}

	public int sendMsg(Object obj) {
		if (channel == null) {
			System.out.println("channel is null");
			return -1;
		}
		System.out.println("send msg to server : " + obj);
		channel.writeAndFlush(obj);
		return 0;

	}

	public void shutdown() {
		group.shutdownGracefully();
	}
}
