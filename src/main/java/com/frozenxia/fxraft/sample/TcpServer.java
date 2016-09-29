/**
 * @Title: TcpServer.java
 * @date:Sep 27, 2016 11:14:21 AM
 * @Description:TODO
 */
package com.frozenxia.fxraft.sample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 *
 * @Description TODO
 * @date Sep 27, 2016 11:14:21 AM
 *
 */

public class TcpServer {
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workGroup = new NioEventLoopGroup();

	public TcpServer() {

	}

	public void bind(int port) throws InterruptedException {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel arg0) throws Exception {
							// TODO Auto-generated method stub
							ChannelPipeline pipeline = arg0.pipeline();
							pipeline.addLast("frameDecoder",
									new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
							pipeline.addLast("frameEncode", new LengthFieldPrepender(4));
							pipeline.addLast("decode", new StringDecoder(CharsetUtil.UTF_8));
							pipeline.addLast("encode", new StringEncoder(CharsetUtil.UTF_8));
							pipeline.addLast(new TcpServerHandler());
						}
					}).childOption(ChannelOption.SO_KEEPALIVE, true);
			// bind socket
			System.out.println("bind port " + port);
			ChannelFuture f = b.bind(port).sync();
			// waiting for close socket
			 f.channel().closeFuture().sync();
			// System.out.println("close port " + port);
		} finally

		{
			// release resource after shutdown
			// bossGroup.shutdownGracefully();
			// workGroup.shutdownGracefully();
		}

	}

	public void shutdown() {
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
}
