package audaque.com.netty.pojo.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ObjectEchoClient {

	public static final int SIZE = 1<< 10;
	
	private String host = null ;
	
	private int port = 0 ;
	
	public ObjectEchoClient(String host,int port) {
		// TODO Auto-generated constructor stub
		this.host = host ;
		this.port = port;
	}
	
	public void run(){
		
		//拿到一个可以连接到服务端的手
		Bootstrap bootstrap = new Bootstrap();
		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try{
			bootstrap.group(eventLoopGroup);
			
			bootstrap.channel(NioSocketChannel.class);
			
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				//设置需要处理服务端返回的数据 处理器
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ObjectEncoder(),
							new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)),
							new ObjectEchoClientHandler());
				}
			});
			
			
			//开始连过去
			try {
				ChannelFuture future = bootstrap.connect(host, port).sync();
				
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}finally{
			eventLoopGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		//连过去
		new ObjectEchoClient("127.0.0.1", 10003).run();
	}
}
