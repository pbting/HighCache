package audaque.com.pbting.cache.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class FileUtils {

	// 使用io 工具包
	public static String read(String fileName) throws IOException {

		return IOUtils.toString(FileUtils.class.getClassLoader().getResourceAsStream(fileName));
	}
	
	//will return input stream
	public static InputStream getInputStream(String fileName) throws IOException{
		
		return FileUtils.class.getClassLoader().getResourceAsStream(fileName) ;
	}
	
	// 文件类型是Excel
	public static final byte EXCEL_FILE = 0;

	// 文件类型是SQL
	public static final byte SQL_FILE = 1;

	/**
	 * 文件的拷贝 从一个文件拷贝到另一个文件
	 * 
	 * @author Administrator
	 * @throws IOException
	 * @param sourcepath
	 *            源文件的路径
	 * @param goalpath目的文件的路径
	 * 
	 */
	public static synchronized void copyFile(String goalpath, boolean append,
			String sourcepath) throws IOException {

		CheckFile.checkCopyFile(goalpath, sourcepath);

		FileInputStream fis = new FileInputStream(new File(sourcepath));

		File d_file = new File(goalpath);
		if (!d_file.exists())
			d_file.createNewFile();

		FileOutputStream fos = new FileOutputStream(d_file, append);

		// 准备缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// 获取相应的通道
		FileChannel fis_channel = fis.getChannel();
		FileChannel fos_channel = fos.getChannel();
		int length = 0;
		while (true) {

			buffer.clear();// 置postion位置会0，Limit=capacity

			length = fis_channel.read(buffer);
			if (length == -1) {
				break;
			}
			buffer.flip();
			fos_channel.write(buffer);
		}

		fis_channel.close();
		fos_channel.close();
	}

	/*
	 * 删除文件
	 */
	public static void delete(String path) {
		delete(new File(path));
	}

	public static void delete(File file) {
		file.delete();
	}

	public static void delete(String... paths) {
		for (String path : paths) {
			delete(new File(path));
		}
	}

	public static void delete(File... files) {
		for (File file : files)
			delete(file);
	}

	/**
	 * 
	 * @param goalpath
	 *            目的文件路径
	 * @param apped
	 *            是否向目的文件进行添加
	 * @param sourcepaths
	 *            多个源文件路径进行拷贝
	 * @throws IOException
	 *             注意：该方法目前不适用于Excel文件中有多个工作溥
	 */
	public static synchronized void copyFile(String goalpath, boolean append,
			String... sourcepaths) throws IOException {
		// 1、检测目标文件是否存在

		FileInputStream fis = null;

		File d_file = new File(goalpath);
		if (!d_file.exists())
			d_file.createNewFile();

		FileOutputStream fos = new FileOutputStream(d_file, append);
		FileChannel fos_channel = fos.getChannel();

		FileChannel fis_channel = null;

		// 准备缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		// 获取相应的通道
		int length = 0;
		for (String path : sourcepaths) {
			CheckFile.checkCopyFile(goalpath, path);
			fis = new FileInputStream(new File(path));
			fis_channel = fis.getChannel();
			while (true) {

				buffer.clear();// 置postion位置会0，Limit=capacity

				length = fis_channel.read(buffer);
				if (length == -1) {
					break;
				}
				buffer.flip();
				fos_channel.write(buffer);
			}
		}
		fis_channel.close();
		fis.close();
		fis = null;
		fos_channel.close();
		fos.flush();
		fos.close();
		fos = null;
		System.out.println("操作完成----");
	}

	public static class CheckFile {

		private static void checkCopyFile(String goalpath, String sourcepath)
				throws FileNotFoundException {
			// 1、检测输入的文件路径是否为null
			if (CheckFile.isNull(sourcepath)) {
				throw new NullPointerException("指定源文件路径参数(sourcepath)不能为空！");
			}

			if (CheckFile.isNull(goalpath)) {
				throw new NullPointerException("指定目的文件路径参数(goalpath)不能为空！");
			}

			// 2、检测源文件和目的文件文件是否存在
			File source_file = new File(sourcepath);
			if (!source_file.exists())// 如果所文件不存在
				throw new FileNotFoundException("进行拷贝的源文件不存在!");
		}

		/**
		 * 检测文件名是否为null
		 */
		public static boolean isNull(String filePath) {
			return (filePath == null ? true : (filePath.trim().length() == 0));
		}

		/**
		 * 检测是否是Excel文件 .xls扩展名（.XLS）
		 */

		public static boolean isExcelFile(String path) {

			if (isNull(path))// 如果为null,则直接返回false
				return false;
			// 文件路径长度至少是4个长度，如果小于4个长度，则该文件路径处错
			int length = path.length();
			// System.out.println(path+"文件长度"+length);
			if (length <= 4)
				return false;

			char last_1 = path.charAt(length - 1);
			char last_2 = path.charAt(length - 2);
			char last_3 = path.charAt(length - 3);
			char last_4 = path.charAt(length - 4);
			if ((last_1 == 's' || last_1 == 'S')
					&& (last_2 == 'l' || last_2 == 'L')
					&& (last_3 == 'x' || last_3 == 'X') && last_4 == '.')
				return true;

			return false;
		}

		/**
		 * 检测文件是否是.csv文件
		 * 
		 * @param excelSheet
		 */
		public static boolean isCSVFile(String path) {
			if (isNull(path))// 如果为null,则直接返回false
				return false;
			// 文件路径长度至少是4个长度，如果小于4个长度，则该文件路径处错
			int length = path.length();
			// System.out.println(path+"文件长度"+length);
			if (length <= 4)
				return false;

			char last_1 = path.charAt(length - 1);
			char last_2 = path.charAt(length - 2);
			char last_3 = path.charAt(length - 3);
			char last_4 = path.charAt(length - 4);
			if ((last_1 == 'v' || last_1 == 'V')
					&& (last_2 == 's' || last_2 == 'S')
					&& (last_3 == 'c' || last_3 == 'C') && last_4 == '.')
				return true;

			return false;
		}

		public static boolean isSQLFile(String path) {
			if (isNull(path))
				return false;

			int length = path.length();
			// System.out.println(path+"文件长度"+length);
			if (length <= 4)
				return false;

			char last_1 = path.charAt(length - 1);
			char last_2 = path.charAt(length - 2);
			char last_3 = path.charAt(length - 3);
			char last_4 = path.charAt(length - 4);
			if ((last_1 == 'l' || last_1 == 'L')
					&& (last_2 == 'q' || last_2 == 'Q')
					&& (last_3 == 's' || last_3 == 'S') && last_4 == '.')
				return true;

			return false;

		}

		private static void checkPath(String path) {
			if (path == null)
				throw new IllegalArgumentException("请初始化文件的路径");
		}

		//
		private static File checkFileIsExit(String path) throws IOException {
			// 1、检测文件是否存在
			File csvFile = new File(path);
			File parent = csvFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();// 创建多级目录
			}
			csvFile.createNewFile();// 创建文件
			return csvFile;
		}
	}

	// 得到文件扩展名
	public static String getExtension(File f) {
		return getExtension(f.getName());
	}

	//the extension will not contains the "." for example if you geven the demo.txt then will return “txt” extension
	public static String getExtension(String fileName) {
		String ext = "";

		int i = fileName.lastIndexOf('.');

		if (i > 0 && i < fileName.length() - 1) {
			ext = fileName.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	// 得到一个新的文件名，该文件是以UUID来命名的，是一个唯一的文件名
	public static String getNewFileName(String original) {
		return StringUtils.join(new String[] {
				java.util.UUID.randomUUID().toString(), ".",
				getExtension(original) });
	}

	// 得到相对于tomcat网站应用所在的相对路径
	public static String getWebAppRealPath(HttpSession session,
			String directoryName) {
		StringBuffer sb = new StringBuffer();
		// //得到创建文件的日期字符串
		String ctxDir = session.getServletContext().getRealPath(
				String.valueOf(File.separatorChar));

		if (!ctxDir.endsWith(String.valueOf(File.separatorChar))) {
			ctxDir = ctxDir + File.separatorChar;
		}

		return sb.append(ctxDir).append(directoryName + File.separatorChar)
				.toString();
	}

	/**
	 * 目前只适用于mysql
	 * 
	 * @param dbUseName
	 *            数据库用户名
	 * @param dbPassword
	 *            数据库密码
	 * @param dbName
	 *            数据库实例名
	 * @param tableName
	 *            导出的那张表
	 * @param path
	 *            导出的路径
	 * @return
	 * @throws IOException
	 */
	public static File exportSQLFile(String dbUseName, String dbPassword,
			String dbName, String tableName, String path) throws IOException {

		StringBuffer sb = new StringBuffer(1000);
		sb.append("cmd /c mysqldump -u ");
		sb.append(dbUseName + " ");
		sb.append("-p");
		sb.append(dbPassword + " ");
		sb.append(dbName + " ");
		sb.append(tableName + " > ");
		sb.append(path);
		Runtime.getRuntime().exec(sb.toString());
		return new File(path);
	}

	public static boolean exportSQLFile(String dbUseName, String dbPassword,
			String dbName, String[] path, String... tableName)
			throws IOException {

		StringBuffer sb = new StringBuffer(1000);
		for (int i = 0; i < path.length; i++) {
			sb.append("cmd /c mysqldump -u ");
			sb.append(dbUseName + " ");
			sb.append("-p");
			sb.append(dbPassword + " ");
			sb.append(dbName + " ");
			sb.append(tableName[i] + " > ");
			sb.append(path[i]);
			Runtime.getRuntime().exec(sb.toString());
			sb.delete(0, sb.toString().length());
		}
		return true;
	}
}
