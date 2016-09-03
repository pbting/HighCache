package audaque.com.pbting.cache.base.persistence;

import java.util.Set;

import audaque.com.pbting.cache.exception.FilePersistException;
import audaque.com.pbting.cache.util.StringUtils;

public class GeneralDiskPersistListener extends AbstractFilePersistListener {

	/**
	 */
	private static final String CHARS_TO_CONVERT = "./\\ :;\"\'_?";

	/**
	 * @param key Cache Entry Key.
	 * @return char[] file name.
	 */
	protected char[] getCacheFileName(String key) {

		if (StringUtils.isEmpty(key)) {//
			throw new IllegalArgumentException("Invalid key '" + key + "' specified to getCacheFile.");
		}

		char[] chars = key.toCharArray();

		StringBuffer sb = new StringBuffer(chars.length + 8);

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			int pos = CHARS_TO_CONVERT.indexOf(c);

			if (pos >= 0) {//
				sb.append('_');
				sb.append(i);
			} else {
				sb.append(c);
			}
		}

		char[] fileChars = new char[sb.length()];

		/**
		 * 这个函数的形参说明如下： 第一个形参：截取的字符数组从StringBuffer中什么位置开始 二：到什么位置结束 三：截取的字符数组存放到哪个字符数组中 四：存放的这个字符数组从什么位置开始存放
		 */
		sb.getChars(0, fileChars.length, fileChars, 0);

		return fileChars;
	}

	@Override
	public void storeGroup(String groupName, Set Valve) throws FilePersistException {
	}
}
