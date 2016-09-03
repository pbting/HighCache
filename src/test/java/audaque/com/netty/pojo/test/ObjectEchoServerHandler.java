package audaque.com.netty.pojo.test;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ObjectEchoServerHandler extends ChannelInboundHandlerAdapter{

	/**
	 * 客户端发送过来的数据 在这里进行读取和处理操作
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("[netty-server-read]："+msg);
		List<LinuxTime> message = (List<LinuxTime>) msg;
		
		for(LinuxTime linuxTime : message){
			System.out.println(linuxTime);
		}
		ctx.write(msg);
	}
	
	/**
	 * 读取完成后，如果有数据进行写操作，则在这里进行刷新操作，并关闭与客户端的链接
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
		ctx.close();
	}
	
	/**
	 * 如果有
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		
		ctx.close();
	}
}
