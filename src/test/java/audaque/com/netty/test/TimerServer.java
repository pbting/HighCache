package audaque.com.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TimerServer {

	//will geven a port of the server
		private int port ;
		
		public TimerServer(int port) {
			// TODO Auto-generated constructor stub
			this.port = port ;
		}
		
		public void run()throws Exception{
			EventLoopGroup bossEvent = new NioEventLoopGroup();
			EventLoopGroup workerEvent = new NioEventLoopGroup();
			
			try{
				//拿到一个可以开启服务器端的“启瓶器”
				ServerBootstrap serverBootstrap = new ServerBootstrap();
				
				//设置好 启瓶器的相关初始化操作
				serverBootstrap.group(bossEvent, workerEvent).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						//最重要的就是在这里，当客户端链接过来的时候，该如何做处理，都封装在这个Handler里面
						ch.pipeline().addLast(new TimerServerHandler(),new TimerEncoder());
					}
				}).option(ChannelOption.SO_BACKLOG,128).
				childOption(ChannelOption.SO_KEEPALIVE,true);
				
				
				//Bind and start to accept incomming connections
				ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
				
				//wait until the server socket is closed
				System.out.println("-----the netty server is start...");
				
				channelFuture.channel().closeFuture().sync();
				
			}finally{
				bossEvent.shutdownGracefully();
				workerEvent.shutdownGracefully();
			}
		}
	
	public static void main(String[] args) {
		
		try {
			new TimerServer(10001).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
