/**
 * @Title: TcpServerHandler.java
 * @date:Sep 27, 2016 2:19:08 PM
 * @Description:TODO
 */
package com.frozenxia.fxraft.sample;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 * @Description TODO
 * @date Sep 27, 2016 2:19:08 PM
 *
 */
public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.
	 * channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(msg);
		// ctx.writeAndFlush("send back to client" + msg);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		// super.handlerAdded(ctx);
		System.out.println(ctx.name() + "income!");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// super.handlerRemoved(ctx);
		System.out.println(ctx.name() + "removed!");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.name() + " is inactive");
		// super.channelInactive(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// super.channelReadComplete(ctx);
		// ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
