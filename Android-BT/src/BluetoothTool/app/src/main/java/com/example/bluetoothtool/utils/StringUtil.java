package com.example.bluetoothtool.utils;

import org.json.JSONObject;

public class StringUtil {
	/**
	 * 十六进制形式输出byte[]
	 * 1->2 如 0x30 --> "30"
	 */
	public static String bytesToHexString(byte[] bytes) {
		if (bytes == null)
		{
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
		{

			String hex = Integer.toHexString(bytes[i] & 0xff);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			builder.append(hex.toUpperCase());
		}
		return builder.toString();
	}

	public static String bytesToHexString(byte[] data,int offset,int blen) {
		if (data == null || offset<0 || blen<0)
		{
			return null;
		}

		byte[] bytes=new byte[blen];
		System.arraycopy(data, offset, bytes, 0, blen);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
		{

			String hex = Integer.toHexString(bytes[i] & 0xff);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			builder.append(hex.toUpperCase());
		}
		return builder.toString();
	}

	/**
	 * 转变十六进制形式的byte[]
	 * 2->1 如： "30" --> 0x30
	 * @param orign
	 * @return
	 */
	public static byte[] hexStringToBytes(String orign){
		try{
			if(orign==null || orign.length()%2!=0){
				return null;
			}

			int length = orign.length()/2;
			byte[] result = new byte[length];
			for(int i=0; i<length; i++){
				result[i] = (byte) Integer.parseInt(orign.substring(i*2, i*2+2),16);
			}
			return result;
		}catch (Exception e){
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 判断字符串是否为空串
	 * @param strData
	 * @return
	 */
	public static boolean isNull(String strData){
		if(strData==null || strData.trim().length()==0){
			return true;
		}
		return false;
	}

	/**
	 * 是否整数
	 * @param str
	 * @return
	 */
	public static boolean isInt(String str){
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * 获取字符串中的汉字个数
	 * 判断依据：根据一个中文占两个字节，假如一个字符的字节数大于8，则判断为中文
	 * @param str
	 * @return
	 */
	public static int getChineseCharactersNumber(String str){
		int count=0;
		if(str!=null){
			char[] c = str.toCharArray();
			for(int i = 0; i < c.length; i++){
				String len = Integer.toBinaryString(c[i]);
				if(len.length() > 8){
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * 判断字符串是否是正常的json数据
	 * @param strJson
	 * @return
	 */
	public static boolean isJson(String strJson){
		if(strJson==null || strJson.trim().isEmpty()){
			return false;
		}
		try {
			JSONObject object = new JSONObject(strJson);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	//获取健值

	/**
	 * 从字符串(格式类似 name=tom|age=16|sex=male)中获取指定key(例如name)的值(例如tom)
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] getKeyValue( byte[] data,String key )
	{
		if (key == null || data == null)
		{
			return null;
		}

		String[] list = new String(data).split("\\|");
		//在多个键值对中寻找对应key的value
		for(int i=0;i<list.length;i++){
			byte[][] tempdata = new byte[2][];
			tempdata = ByteUtil.getByteArrayBeforeSeperator(list[i].getBytes(), (byte)'=');
			if (tempdata[0] != null && new String(tempdata[0]).equals(key))
			{
				return tempdata[1];
			}
		}

		return null;
	}

	/**
	 * 字符串中是否只包含字母和数字
	 * @param str
	 * @return
	 */
	public static boolean isLetterDigit(String str) {
		if(str==null || str.length()==0){
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) && !Character.isLetter(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}