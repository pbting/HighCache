package audaque.com.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {

	//will geven a port of the server
	private int port ;
	
	public DiscardServer(int port) {
		// TODO Auto-generated constructor stub
		this.port = port ;
	}
	
	public void run()throws Exception{
		EventLoopGroup bossEvent = new NioEventLoopGroup();
		EventLoopGroup workerEvent = new NioEventLoopGroup();
		
		try{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(bossEvent, workerEvent).channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addLast(new ResponseMessageServerHandler());
//					ch.pipeline().addLast(new DiscardServerHandler());
				}
			}).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
			
			
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
		// TODO Auto-generated method stub
		int port = 8089;
		
		try {
			new DiscardServer(port).run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
