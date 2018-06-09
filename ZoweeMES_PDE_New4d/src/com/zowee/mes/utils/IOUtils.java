package com.zowee.mes.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class IOUtils 
{
	
	private static final String TAG = "IOUtils";
	/*
	 * 读取输入流数据 放置在一个字节数组中 但不关闭输入流 主要针对非阻塞的输入流
	 */
	public static byte[] parseInStreamToBytes(InputStream inStream)
	{
		if(null==inStream) throw new NullPointerException("inStream can not be null");
		byte[] bufBytes = new byte[1024];
		int len = -1;
		byte[] inStreamBytes = null;
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		
		try 
		{
			
			while(-1!=(len=inStream.read(bufBytes)))
			{
				bytesOut.write(bufBytes, 0, len);
			}
			inStreamBytes = bytesOut.toByteArray();
		//	Log.i(TAG,"000"+inStreamBytes.length);
			//Log.i(TAG,"456");
		}
		catch (Exception e) 
		{
			Log.i(TAG, e.toString());
			//Log.i(TAG,"4555");
		}
		finally
		{
			try
			{
				bytesOut.close();
				inStream.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i(TAG, e.toString());
			}
		}
		
		return inStreamBytes;
	}
	
	/*
	 * 对输入流的数据进行解析 并以字符串的形式返回 encoding默认UTF-8
	 */
	public static String parseInStreamToStr(InputStream inStream,String encoding)
	{
		if(StringUtils.isEmpty(encoding))
			encoding = "UTF-8";
		//Log.i(TAG, "888");
		byte[] inStreamBytes = parseInStreamToBytes(inStream);
		//Log.i(TAG, "111");
		if(null==inStreamBytes||0==inStreamBytes.length) return null;
		String strVal = null;
		
		try 
		{
			strVal = new String(inStreamBytes, encoding);
		} 
		catch (UnsupportedEncodingException e) 
		{
			//e.printStackTrace();
			Log.i(TAG, e.toString());
		}
		Log.i(TAG, strVal);
		return strVal;
	}
	
	/**
	 * @param inStream
	 * @param encoding
	 * @return
	 * @description 对具有阻塞的输入流进行解析  并以字符串的形式返回
	 */
	public static String parBloInStreamToStr(InputStream inStream,String encoding)
	{
		if(StringUtils.isEmpty(encoding)) encoding = "UTF-8";
		byte[] inStreamBytes = parBloInStreamToBytes(inStream);
		if(null==inStreamBytes) return null;
		String inStreamVal = null;
		try 
		{
			inStreamVal = new String(inStreamBytes, encoding);
		} 
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			Log.i(TAG, e.toString());
			return null;
		}
		
		return inStreamVal;
	}
	
	/**
	 * @param inStream
	 * @return
	 * @description  对具有阻塞的输入流进行解析  并以字节数组的形式返回
	 */
	public static byte[] parBloInStreamToBytes(InputStream inStream)
	{
		if(null==inStream) throw new NullPointerException("InputStream can not be null");
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		byte[] inStreamBytes = null;
		try 
		{
			while(inStream.available()>0)
			{
				bytesOut.write(inStream.read());
			}
			inStreamBytes = bytesOut.toByteArray();
		}
		catch (Exception e)
		{
			Log.i(TAG, e.toString());
			return null;
		}
		finally
		{ 
			
			try
			{
				bytesOut.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, e.toString());
			}
		}
		
		return inStreamBytes;
	}
	
	/**
	 * @param instream
	 * @param out
	 * @return
	 * @description 将输入流写入到输出流 
	 */
	public static boolean parseInstreamToOutstream(InputStream instream, OutputStream out)
	{
		try 
		{
			if(null==instream||0==instream.available()||null==out)
				return false;
			byte[] bytesItem = new byte[1024];
			int len = -1;
			while(-1!=(len=instream.read(bytesItem)))  
			{
				out.write(bytesItem, 0, len);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i(TAG, e.toString());
			return false;
		}
		finally
		{
			try 
			{
				instream.close();
				out.flush();
				out.close();
			}
			catch (Exception e2) 
			{
				Log.i(TAG,e2.toString());
			}
		}
		
		return true;
	}
	
	
}
