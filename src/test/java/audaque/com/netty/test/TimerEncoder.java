package audaque.com.netty.test;

import audaque.com.netty.pojo.test.LinuxTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class TimerEncoder extends ChannelOutboundHandlerAdapter {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		// TODO Auto-generated method stub
		LinuxTime linuxTime = (LinuxTime)msg;
		
		ByteBuf buffer = ctx.alloc().buffer(4);
		
		buffer.writeLong(linuxTime.getTime());	
		
		ctx.write(buffer, promise);
	}
}
