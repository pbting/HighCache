package audaque.com.pbting.cache.base.persistence;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import audaque.com.pbting.cache.base.info.CacheConfig;
import audaque.com.pbting.cache.exception.FilePersistException;

public class CecurityDiskPersistenceListener extends AbstractFilePersistListener {

	private static Log log = LogFactory.getLog(CecurityDiskPersistenceListener.class);
	//文件目录的级别数，深度越高 越安全
	private static final int DIR_LEVELS = 3;//
	
	private static final String[] PSEUDO = 
		{ "0", "1", "2", "3", "4", "5", "6","7", "8", "9", "A", "B", "C", "D", "E", "F" };
	
	public final static String DEFAULT_HASH_ALGORITHM = "MD5";
	
	protected MessageDigest md = null;

	/**
	 * Initializes the <tt>HashDiskPersistenceListener</tt>. Namely this
	 * involves only setting up the message digester to hash the key values.
	 * 
	 */
	
	public CecurityDiskPersistenceListener(){}
	
	@Override
	public FilePersistListener config(String topic,CacheConfig config) {
		
		try {
			md = MessageDigest.getInstance(CecurityDiskPersistenceListener.DEFAULT_HASH_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return super.config(topic,config);
	}

	
	protected synchronized char[] getCacheFileName(String key) {
		if ((key == null) || (key.length() == 0)) {
			throw new IllegalArgumentException("Invalid key '" + key
					+ "' specified to getCacheFile.");
		}

		String hexDigest = byteArrayToHexString(md.digest(key.getBytes()));
		
		// CACHE-249: Performance improvement for large disk persistence usage
		StringBuffer filename = new StringBuffer(hexDigest.length() + 2* DIR_LEVELS);
		for (int i = 0; i < DIR_LEVELS; i++) {
			filename.append(hexDigest.charAt(i)).append(File.separator);
		}
		filename.append(hexDigest);

		log.info("[highCache]:CecurityDiskPersistenceListener.getCacheFileName:"+filename.toString());
		
		return filename.toString().toCharArray();
	}

	static String byteArrayToHexString(byte[] in) {
		if ((in == null) || (in.length <= 0)) {
			return null;
		}

		StringBuffer out = new StringBuffer(in.length * 2);

		for (int i = 0; i < in.length; i++) {
			byte ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4);

			// shift the bits down
			ch = (byte) (ch & 0x0F);

			// must do this is high order bit is on!
			out.append(PSEUDO[(int) ch]); // convert the nibble to a String
											// Character
			ch = (byte) (in[i] & 0x0F); // Strip off low nibble
			out.append(PSEUDO[(int) ch]); // convert the nibble to a String
											// Character
		}

		return out.toString();
	}

	@Override
	public void storeGroup(String groupName, Set Valve) throws FilePersistException {
	
	}
}
