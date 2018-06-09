package com.deng.fileutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopy {
     

	    public static void main(String args[]) {
	        File file = new File("D:/BTPrinterDemo(android)");
	        File file2 = new File("d:/androidws");
	        copyFile(file, file2);
	    }

	        // 递归方法
	    public static void copyFile(File file, File file2) {
	                // 当找到目录时，创建目录
	        if (file.isDirectory()) {
	            file2.mkdir();
	            File[] files = file.listFiles();
	            for (File file3 : files) {
	                  // 递归
	                  copyFile(file3, new File(file2, file3.getName()));
	            }
	                //当找到文件时
	        } else if (file.isFile()) {
	            File file3 = new File(file2.getAbsolutePath());
	            if(file3.getAbsolutePath().endsWith(".java")){
	            	file3=new File(file3.getAbsolutePath().replace(".java", ".txt"));
	            }
	            try {
	                file3.createNewFile();
	                copyDatas(file.getAbsolutePath(), file3.getAbsolutePath());
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	        // 复制文件数据的方法
	    public static void copyDatas(String filePath, String filePath1) {
	        FileInputStream fis = null;
	        FileOutputStream fos = null;
	        try {
                // 字节流
                fis = new FileInputStream(filePath);
	            fos = new FileOutputStream(filePath1);
	            byte[] buffer = new byte[1024];
	            while (true) {
	                int temp = fis.read(buffer, 0, buffer.length);
	                if (temp == -1) {
	                    break;
	                } else {
	                    fos.write(buffer, 0, temp);
	                }
	            }
	        } catch (IOException e) {
	            System.out.println(e);
	        } finally {
	            try {
	                fis.close();
	                fos.close();
	            } catch (IOException e) {
	                System.out.println(e);
	            }
	        }
	    }
	}    
