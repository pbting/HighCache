package audaque.com.netty.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

	public static void main(String[] args) {
		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		
		try{
			Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(eventLoopGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {

					ch.pipeline().addLast(new TimerDecoder(),new TimerClientHandler());
				}
			});
			
			ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",10001);
			
			try {
				channelFuture.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}finally{
		
			eventLoopGroup.shutdownGracefully();	
		}
	}
}
