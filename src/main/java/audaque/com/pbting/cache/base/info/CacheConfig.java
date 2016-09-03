package audaque.com.pbting.cache.base.info;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import audaque.com.pbting.cache.util.FileUtils;
import audaque.com.pbting.cache.util.StringUtils;

public class CacheConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final transient Log log = LogFactory.getLog(CacheConfig.class);

	/**
	 * name of the properties file
	 */

	private Properties properties = null;

	public CacheConfig(String configPath) throws IllegalArgumentException {

		initProperties(configPath);
	}

	private void initProperties(String configPath) throws IllegalArgumentException {
		// here should do corresponding processing according to each file type
		if ("properties".equals(FileUtils.getExtension(configPath))) {

			initByProperties(configPath);
		} else if ("xml".equals(FileUtils.getExtension(configPath))) {
			/**
			 * here did not add the properties file configuration, then which provide the xml file to find whether
			 * configuration
			 */
			initByXml(configPath);
		} else
			throw new IllegalArgumentException("please enter the corect configuration file,configuration file for the properties file or xml file.");
	}

	private void initByXml(String configPath) {
		try {
			String content = FileUtils.read(configPath);

			this.setProperties(Jsoup.parse(content).getElementsByTag("property"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setProperties(Elements elements) {
		this.properties = new Properties();

		for (Element ele : elements) {
			properties.setProperty(ele.attr("id").trim(), ele.text().trim());
		}
	}

	private void initByProperties(String configPath) {
		URL url = null;

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		if (contextClassLoader != null) {
			url = contextClassLoader.getResource(configPath);// in here please

			if (url == null)// 防止测试线程而拿不到相关的url
				url = CacheConfig.class.getResource(configPath);
		}

		this.properties = this.loadProperties(url, "will config the cache continaer according to the properties file");
	}

	public CacheConfig(Properties properties) {

		log.info("it start to load the properties file of config the cache.");

		if (properties == null)
			throw new IllegalArgumentException("the input parameters of the properties cann't be empty. ");

		this.properties = properties;
	}

	public void set(String key, String value) throws IllegalAccessException {

		if (StringUtils.isEmpty(key))
			throw new IllegalAccessException("the input of the key is null!");

		if (StringUtils.isEmpty(value))
			return;

		if (properties == null)
			properties = new Properties();

		properties.put(key, value);
	}

	public String getProperty(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}

		if (properties == null) {
			return null;
		}

		return properties.getProperty(key);
	}

	/**
	 * Retrieves all of the configuration properties. This property set should be treated as immutable.
	 * 
	 * @return The configuration properties.
	 */
	public Properties getProperties() {
		return properties;
	}

	public Object get(Object key) {
		return properties.get(key);
	}

	/**
	 * how to load the properties file
	 */

	@Deprecated
	private static Properties loadProperties(String fileName, String infor) throws IOException {
		// 1
		URL url = null;

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		if (contextClassLoader != null) {
			url = contextClassLoader.getResource(fileName);// in here please
			// note the spell of
			// the file name

			if (url == null)
				url = CacheConfig.class.getResource(fileName);

		}

		System.out.println("the url object :" + url);
		if (url == null) {

			log.warn("nchucache:the file name of the configuration is : " + fileName);

			// 在这里 说明没有添加properties 文件的配置，从而找是否提供了 xml 文件的配置
			String content = FileUtils.read(fileName);

			Document doc = null;
			System.out.println("--------->use xml config the system<---------");
			doc = Jsoup.parse(content);

			Elements elements = doc.getElementsByTag("property");

			Properties prop = new Properties();

			for (Element ele : elements) {
				prop.setProperty(ele.attr("id").trim(), ele.text().trim());
			}

			return prop;
		}
		return loadProperties(url, infor);
	}

	private static Properties loadProperties(URL url, String infor) {

		log.info("NchuCache: the URL of the config file is:" + url + " for " + infor);

		Properties pro = new Properties();
		InputStream ins = null;
		try {
			ins = url.openStream();

			pro.load(ins);

			log.info("NchuCache-properties infor is: " + pro);
		} catch (IOException e) {
			log.error("NchuCache: the url[" + url + e + "],can't found by the configuration file");

		} finally {
			try {
				ins.close();
			} catch (Exception e) {
				log.warn("NchuCache: " + e.getMessage());
			}
		}

		return pro;
	}
}
