package audaque.com.netty.test;

import java.util.List;

import audaque.com.netty.pojo.test.LinuxTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class TimerDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1,
			List<Object> arg2) throws Exception {
	System.out.println("timer decider=-------"+arg1.readableBytes());
		if(arg1.readableBytes() < 4)
			return ;
		
		arg0.writeAndFlush(new LinuxTime());
	}

}
