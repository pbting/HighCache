package audaque.com.netty.test;

import audaque.com.netty.pojo.test.LinuxTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimerServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		final ByteBuf in = ctx.alloc().buffer(4);
		
		in.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
		
//		Person person = new Person("pbting",23);
		
		ChannelFuture future = ctx.writeAndFlush(new LinuxTime());
		
		//if you need to continue to process then doesn't add flower code or write it
//		future.addListeners(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
		cause.printStackTrace();
		
		super.exceptionCaught(ctx, cause);
		
		ctx.close();
	}
}
