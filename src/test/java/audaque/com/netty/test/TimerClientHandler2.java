package audaque.com.netty.test;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimerClientHandler2 extends ChannelInboundHandlerAdapter {

	private ByteBuf buffer = null;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

		buffer = ctx.alloc().buffer(4);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		buffer.release();

		buffer = null;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		ByteBuf byteBuf = (ByteBuf) msg;

		// 指定写入的数据源
		buffer.writeBytes(byteBuf);

		byteBuf.release();

		if (this.buffer.readableBytes() >= 4) {

			long currentTimeMillis = (buffer.readUnsignedInt() - 2208988800L) * 1000L;
			System.out.println(new Date(currentTimeMillis));
			ctx.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
