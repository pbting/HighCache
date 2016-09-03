package audaque.com.zerocopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ZeroCopyFile {

	public static void main(String[] args) throws Exception {
		String srcPath = "E:/___computer and tecnology/j-zerocopy/TraditionalClient.java";
		File src = new File(srcPath);
		FileChannel fileChannel = new FileInputStream(src).getChannel();
		String tansf = "D:/Demo.java";
		FileChannel fos = new FileOutputStream(new File(tansf)).getChannel();
		long length = src.length();
		System.out.println(length);
		long size = fileChannel.transferTo(0, length, fos);
		fileChannel.close();
		fos.close();
		System.out.println(size);
	}
}
