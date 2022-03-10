package com.example.bluetoothtool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

//计算文件MD5值

public class FileDigest
{
	public static String getFileMD5(File file)
	{
		if (!file.isFile())
		{
			return null;
		}

		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try
		{
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1)
			{
				digest.update(buffer, 0, len);
			}
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		String data = bigInt.toString(16);
		String retdata = "";
		if (data.length() < 32)
		{
			int nLen = 32-data.length();
			byte[] bpadding = new byte[nLen];

			for (int i = 0; i < nLen; i++)
			{
				bpadding[i] = 0x30;
			}
			retdata = new String(bpadding);
		}
		retdata += data;
		return retdata;
	}

	public static String getFileMD5(String filePath)
	{
		if (filePath == null)
		{
			return null;
		}

		File file = new File(filePath);

		if (!file.isFile())
		{
			return null;
		}

		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try
		{
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1)
			{
				digest.update(buffer, 0, len);
			}
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		String data = bigInt.toString(16);
		String retdata = "";
		if (data.length() < 32)
		{
			int nLen = 32-data.length();
			byte[] bpadding = new byte[nLen];

			for (int i = 0; i < nLen; i++)
			{
				bpadding[i] = 0x30;
			}
			retdata = new String(bpadding);
		}
		retdata += data;
		return retdata;
	}


	public static Map<String, String> getDirMD5(File file,boolean listChild)
	{
		if (!file.isDirectory())
		{
			return null;
		}

		//<filepath,md5>
		Map<String, String> map = new HashMap<String, String>();
		String md5;
		File files[] = file.listFiles();
		if(files==null)
			return null;
		for (int i=0; i<files.length; i++)
		{
			File f = files[i];
			if (f.isDirectory() && listChild)
			{
				map.putAll(getDirMD5(f, listChild));
			}
			else
			{
				md5 = getFileMD5(f);
				if (md5 != null)
				{
					map.put(f.getPath(), md5);
				}
			}
		}
		return map;
	}

}