package com.yuanding.schoolpass.utils.download;

import java.io.Serializable;

public class WebResource implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 文件存储目录
	 */
	public String filePath;//
	/**
	 * 文件名字
	 */
	public String fileName;//
	/**
	 * 文件的网络存储地
	 */
	public String url;//
	/**
	 * id
	 */
	public long id;
	/**
	 * 结束
	 */
	public Object taget = null;

}
