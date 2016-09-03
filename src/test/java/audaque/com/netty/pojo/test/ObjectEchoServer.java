package audaque.com.netty.pojo.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
/**
 * 分布式缓存框架要考虑的问题（客户端与服务端及时进行通信）
 * 问题：
 * 		--1、什么时候再去同步从机缓存数据
 * 		--2、当有从机连接过来该如何处理
 * 		--3、当有从机挂了怎么办
 * 		--4、挂了的从机从新连接后的数据同步该如何处理
 * 		--5、如何来实现负载均衡
 * 		--6、最重要的一点是，主机挂了该如何处理
 * @author pbting
 *
 */
public class ObjectEchoServer {

	static final boolean SSL = System.getProperty("ssl") != null;
	
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(bossGroup, workerGroup);
			
			serverBootstrap.channel(NioServerSocketChannel.class);
			
			serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
			
			serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ChannelPipeline pipeline = ch.pipeline();
					
					pipeline.addLast(new ObjectEncoder(),
							new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
							new ObjectEchoServerHandler());
				}
			});
			System.out.println("----start and listener on 10004 port ----");
			//开启并监听客户端的连接
			serverBootstrap.bind(10004).sync().channel().closeFuture().sync();
		}finally{
			
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
