package audaque.com.netty.test;

import java.util.Date;

import audaque.com.netty.pojo.test.LinuxTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimerClientHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		ByteBuf byteBuf = (ByteBuf)msg;
		LinuxTime linuxTime = (LinuxTime) msg;
		System.out.println(linuxTime);
		//读取服务器端返回的数据
//		long currentTimeMillis = (byteBuf.readUnsignedInt() - 2208988800L) * 1000L;
		
//        System.out.println("the message from the server is:"+new Date(currentTimeMillis));
		ctx.close();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
        ctx.close();
	}
}
