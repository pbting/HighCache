package audaque.com.netty.gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GPSDemo {

	/** 
	    * @param addr 
	    * 查询的地址 
	    * @return 
	    * @throws IOException 
	    */
	    public static Object[] getCoordinate(String addr) throws IOException { 
	        String lng = null;//经度
	        String lat = null;//纬度
	        String address = null; 
	        try { 
	            address = java.net.URLEncoder.encode(addr, "UTF-8"); 
	        }catch (UnsupportedEncodingException e1) { 
	            e1.printStackTrace(); 
	        } 
	        String key = "f247cdb592eb43ebac6ccd27f796e2d2"; 
	        String url = 
	        		String .format("http://api.map.baidu.com/geocoder?address=%s&output=json&key=%s", address, key); 
	        URL myURL = null; 
	        URLConnection httpsConn = null; 
	        try { 
	            myURL = new URL(url); 
	        } catch (MalformedURLException e) { 
	            e.printStackTrace(); 
	        } 
	        InputStreamReader insr = null;
	        BufferedReader br = null;
	        try { 
	            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理 
	            if (httpsConn != null) { 
	                insr = new InputStreamReader( httpsConn.getInputStream(), "UTF-8"); 
	                br = new BufferedReader(insr); 
	                String data = null; 
	                int count = 1;
	                while((data= br.readLine())!=null){ 
	                    if(count==5){
	                        lng = (String)data.subSequence(data.indexOf(":")+1, data.indexOf(","));//经度
	                        count++;
	                    }else if(count==6){
	                        lat = data.substring(data.indexOf(":")+1);//纬度
	                        count++;
	                    }else{
	                        count++;
	                    }
	                } 
	            } 
	        } catch (IOException e) { 
	            e.printStackTrace(); 
	        } finally {
	            if(insr!=null){
	                insr.close();
	            }
	            if(br!=null){
	                br.close();
	            }
	        }
	        return new Object[]{lng,lat}; 
	    } 
	  
	   public static String getAddressByGps(String lat,String lng){
//		   String key = "f247cdb592eb43ebac6ccd27f796e2d2"; 
		   String url =
				   "http://api.map.baidu.com/geocoder/v2/?ak=pmCgmADsAsD9rEXkqWNcTzjd&location=30.548397,104.04701&output=json&pois=1";

		   StringBuffer sb = new StringBuffer();
		   URL myURL = null; 
	        URLConnection httpsConn = null; 
	        try { 
	            myURL = new URL(url); 
	        } catch (MalformedURLException e) { 
	            e.printStackTrace(); 
	        } 
	        InputStreamReader insr = null;
	        BufferedReader br = null;
	        try { 
	            httpsConn = (URLConnection) myURL.openConnection();// 不使用代理 
	            if (httpsConn != null) { 
	                insr = new InputStreamReader( httpsConn.getInputStream(), "UTF-8"); 
	                br = new BufferedReader(insr); 
	                String data = null; 
	                while((data= br.readLine())!=null){ 
	                	sb.append(data);
	                } 
	            } 
	        } catch (IOException e) { 
	            e.printStackTrace(); 
	        } finally {
	            try {
					if(insr!=null){
					    insr.close();
					}
					if(br!=null){
					    br.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		   
		   return sb.toString() ;
	   }
	  
	    public static void main(String[] args) throws IOException {
	        Object[] o = getCoordinate("成都市天府四街");
	        System.out.println(o[0]);//经度
	        System.out.println(o[1]);//纬度
	        
	        System.out.println(getAddressByGps(o[1].toString(),o[0].toString()));
	    }

}
