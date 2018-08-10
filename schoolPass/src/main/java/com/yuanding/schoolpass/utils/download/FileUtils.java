package com.yuanding.schoolpass.utils.download;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.InputStreamReader;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;

/**
 * @author dllik
 *         2013-11-23
 */
public class FileUtils {
	private String SDPATH;

	private String FILESPATH;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils(Context context) {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		FILESPATH = context.getFilesDir().getPath() + "/";
	}

	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	public static boolean isSDCardExist() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在sd卡上创建文件夹（目录）
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		if (!dir.exists()) {

			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 判断制定文件是否存在
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 删除SD卡上的文件
	 * 
	 * @param fileName
	 */
	public boolean delSDFile(String fileName) {
		File file = new File(SDPATH + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		file.delete();
		return true;
	}

	/**
	 * 删除SD卡上的目录
	 * 
	 * @param dirName
	 */
	public boolean delSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		return delDir(dir);
	}

	public int getSDDirSize(String dirName) {
		File dir = new File(SDPATH + dirName);
		if (dir == null || !dir.exists() || dir.isFile()) {
			return 0;
		}
		return (int) (dir.length() / 1024 / 1024);
	}

	/**
	 * 删除目录（可以是非空目录
	 * 
	 * @param dir
	 */
	public boolean delDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile()) {
			return false;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				delDir(file);// 递归
			}
		}
		dir.delete();
		return true;
	}

	/**
	 * 重命名文件或者目录
	 * 
	 * @param fileName
	 */
	public boolean renameSDFile(String oldfileName, String newFileName) {
		File oleFile = new File(SDPATH + oldfileName);
		File newFile = new File(SDPATH + newFileName);
		return oleFile.renameTo(newFile);
	}

	/**
	 * 拷贝SD卡上的单个文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	public boolean copySDFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(SDPATH + srcFileName);
		File destFile = new File(SDPATH + destFileName);
		return copyFileTo(srcFile, destFile);
	}

	/**
	 * 拷贝SD卡上指定目录的所有文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean copySDFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(SDPATH + srcDirName);
		File destDir = new File(SDPATH + destDirName);
		return copyFilesTo(srcDir, destDir);
	}

	/**
	 * 拷贝文件,srcFile源文件，destFile目标文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	public boolean copyFileTo(File srcFile, File destFile) throws IOException {
		if (srcFile.isDirectory() || destFile.isDirectory())
			return false;// 判断是否是文�?
		FileInputStream fis = new FileInputStream(srcFile);
		FileOutputStream fos = new FileOutputStream(destFile);
		int readLen = 0;
		byte[] buf = new byte[1024];
		while ((readLen = fis.read(buf)) != -1) {
			fos.write(buf, 0, readLen);
		}
		fos.flush();
		fos.close();
		fis.close();
		return true;
	}

	/**
	 * 拷贝目录下的文件到指定目
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public boolean copyFilesTo(File srcDir, File destDir) throws IOException {
		if (!srcDir.isDirectory() || !destDir.isDirectory())
			return false;// 判断是否是目�?
		if (!destDir.exists())
			return false;// 判断目标目录是否存在
		File[] srcFiles = srcDir.listFiles();
		for (int i = 0; i < srcFiles.length; i++) {
			if (srcFiles[i].isFile()) {
				// 获得目标文件
				File destFile = new File(destDir.getPath() + "//"
						+ srcFiles[i].getName());
				copyFileTo(srcFiles[i], destFile);
			} else if (srcFiles[i].isDirectory()) {
				File theDestDir = new File(destDir.getPath() + "//"
						+ srcFiles[i].getName());
				copyFilesTo(srcFiles[i], theDestDir);
			}
		}
		return true;
	}

	/**
	 * 移动SD卡上的单个文件
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean moveSDFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(SDPATH + srcFileName);
		File destFile = new File(SDPATH + destFileName);
		return moveFileTo(srcFile, destFile);
	}

	/**
	 * 移动SD卡上的指定目录的文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean moveSDFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(SDPATH + srcDirName);
		File destDir = new File(SDPATH + destDirName);
		return moveFilesTo(srcDir, destDir);
	}

	/**
	 * 移动文件
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 * @throws IOException
	 */
	public boolean moveFileTo(File srcFile, File destFile) throws IOException {
		boolean iscopy = copyFileTo(srcFile, destFile);
		if (!iscopy)
			return false;
		delFile(srcFile);
		return true;
	}

	/**
	 * 移动目录下的文件到指定目
	 * 
	 * @param srcDir
	 * @param destDir
	 * @return
	 * @throws IOException
	 */
	public boolean moveFilesTo(File srcDir, File destDir) throws IOException {
		if (!srcDir.isDirectory() || !destDir.isDirectory()) {
			return false;
		}
		File[] srcDirFiles = srcDir.listFiles();
		for (int i = 0; i < srcDirFiles.length; i++) {
			if (srcDirFiles[i].isFile()) {
				File oneDestFile = new File(destDir.getPath() + "//"
						+ srcDirFiles[i].getName());
				moveFileTo(srcDirFiles[i], oneDestFile);
				delFile(srcDirFiles[i]);
			} else if (srcDirFiles[i].isDirectory()) {
				File oneDestFile = new File(destDir.getPath() + "//"
						+ srcDirFiles[i].getName());
				moveFilesTo(srcDirFiles[i], oneDestFile);
				delDir(srcDirFiles[i]);
			}

		}
		return true;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @return
	 */
	public boolean delFile(File file) {
		if (file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 建立私有文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File creatDataFile(String fileName) throws IOException {
		File file = new File(FILESPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 建立私有目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File creatDataDir(String dirName) {
		File dir = new File(FILESPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 删除私有文件
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean delDataFile(String fileName) {
		File file = new File(FILESPATH + fileName);
		return delFile(file);
	}

	/**
	 * 删除私有目录
	 * 
	 * @param dirName
	 * @return
	 */
	public boolean delDataDir(String dirName) {
		File file = new File(FILESPATH + dirName);
		return delDir(file);
	}

	/**
	 * 更改私有文件
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public boolean renameDataFile(String oldName, String newName) {
		File oldFile = new File(FILESPATH + oldName);
		File newFile = new File(FILESPATH + newName);
		return oldFile.renameTo(newFile);
	}

	/**
	 * 在私有目录下进行文件复制
	 * 
	 * @param srcFileName
	 *            包含路径及文件名
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean copyDataFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(FILESPATH + srcFileName);
		File destFile = new File(FILESPATH + destFileName);
		return copyFileTo(srcFile, destFile);
	}

	/**
	 * 复制私有目录里指定目录的文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean copyDataFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(FILESPATH + srcDirName);
		File destDir = new File(FILESPATH + destDirName);
		return copyFilesTo(srcDir, destDir);
	}

	/**
	 * 移动私有目录下的单个文件
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @return
	 * @throws IOException
	 */
	public boolean moveDataFileTo(String srcFileName, String destFileName)
			throws IOException {
		File srcFile = new File(FILESPATH + srcFileName);
		File destFile = new File(FILESPATH + destFileName);
		return moveFileTo(srcFile, destFile);
	}

	/**
	 * 移动私有目录下的指定目录下的文件
	 * 
	 * @param srcDirName
	 * @param destDirName
	 * @return
	 * @throws IOException
	 */
	public boolean moveDataFilesTo(String srcDirName, String destDirName)
			throws IOException {
		File srcDir = new File(FILESPATH + srcDirName);
		File destDir = new File(FILESPATH + destDirName);
		return moveFilesTo(srcDir, destDir);
	}

	/**
	 * 把输入流转化为字节数
	 */
	public byte[] readStream(InputStream in) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}

	/**
	 * 保存文件
	 */
	public File writeSDFromInput(String fileName, InputStream input) {
		byte[] buffer = new byte[1024 * 10];

		File file = new File(SDPATH + fileName);

		FileOutputStream output = null;

		int len = 0;
		try {
			file.createNewFile();
			output = new FileOutputStream(file);
			while ((len = input.read(buffer)) != -1) {// 循环获取文件内容
				output.write(buffer, 0, len);
			}
			return file;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
				output.close();
			} catch (Exception e) {

			}

		}
		return null;
	}

	/**
	 * 将指定内容写入指定目录的文件
	 */

	public static void writeToLocal(String content, String path) {
		File file = new File(path);
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取指定目录文件的内
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static String readFileContent(String filename) throws Exception {
		File file = new File(filename);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(1024 * 50);
		FileInputStream inStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		String string = new String(data);
		return string;
	}

	/**
	 * 遍历sdcard文件
	 * 
	 * @param Path
	 * @param Extension
	 * @param IsIterative
	 * @return
	 */
	public static ArrayList<File> getFiles(String Path, String Extension,
			boolean IsIterative) // 搜索目录，扩展名，是否进入子文件
	{
		File[] files = new File(Path).listFiles();
		File f;
		if (files == null) {
			System.out.println("------------is empty------------");
			return null;
		}
		ArrayList<File> list = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			f = files[i];
			if (!f.canRead()) {
				System.out.println("------------can not read------------");
				return null;
			}
			if (f.isFile()) {
				// 判断扩展
				if (f.getName().contains(Extension)) {
					list.add(f);
				}
			} else if (f.isDirectory()) {
				getFiles(f.getPath(), Extension, IsIterative);
			}
		}
		return list;
	}

	/**
	 * 获取文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFileSdcard(String fileName) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			// res = EncodingUtils.getString(buffer, "GB2312");
			res = EncodingUtils.getString(buffer, "utf-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/****
	 * 将流文件转换为字符串
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getStringByStream(InputStream inputStream) {
		String result = "";

		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		try {
			inputReader = new InputStreamReader(inputStream);
			bufReader = new BufferedReader(inputReader);
			String line = "";
			while ((line = bufReader.readLine()) != null)
				result += line;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					inputReader.close();
					bufReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
