package audaque.com.netty.pojo.test;

import java.util.List;
import java.util.Vector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ObjectEchoClientHandler extends ChannelInboundHandlerAdapter{

	private final List<LinuxTime> times = new Vector<LinuxTime>(ObjectEchoClient.SIZE);
	
	public ObjectEchoClientHandler() {
		//准备要发送的数据
		for(int i=0 ; i < ObjectEchoClient.SIZE;i++){
			times.add(new LinuxTime());
		}
	}
	/**
	 * 客户端发数据 发生在这里
	 * 
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("---netty client send message start.");
		ctx.writeAndFlush(times);
	}
	/**
	 * 服务器端返回的数据读 是在这里
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		 List<LinuxTime> messages = (List<LinuxTime>) msg;
		System.out.println("netty-client-read:"+messages.size());
		ctx.close();
	}
	
	/**
	 * 读完成后的操作
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
		cause.printStackTrace();
		
		ctx.close();
	}
}
