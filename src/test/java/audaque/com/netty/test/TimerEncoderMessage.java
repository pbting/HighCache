package audaque.com.netty.test;

import audaque.com.netty.pojo.test.LinuxTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TimerEncoderMessage extends MessageToByteEncoder<LinuxTime>{

	@Override
	protected void encode(ChannelHandlerContext arg0, LinuxTime arg1,
			ByteBuf arg2) throws Exception {
	
		arg2.writeLong(arg1.getTime());
	}

}
