package com.yuanding.schoolpass.utils.download;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

/**
 * 异步下载管理
 * 
 * @author dllik
 * 
 *         2013-11-23
 */
public class AsyncDownLoadManager {
	private static Context mContext = null;

	/**
	 * 异步下载的manager
	 */
	private static AsyncDownLoadManager manager = new AsyncDownLoadManager();
	/**
	 * 资源的集合
	 */
	private List<WebResource> resList = new ArrayList<WebResource>();
	/**
	 * 下载监听map
	 */
	private HashMap<Long, OnDownLoadListener> taskMap = new HashMap<Long, OnDownLoadListener>();
	/**
	 * AsyncDownLoad 的map
	 */
	private HashMap<Long, AsyncDownLoad> curTaskMap = new HashMap<Long, AsyncDownLoad>();
	/**
	 * 正在的下载
	 */
	private AsyncDownLoad currentDownloadTask = null;
	/**
	 * 下载的监听器
	 */
	private OnDownLoadListener currentDownloadListener = null;
	/**
	 * 资源
	 */
	private WebResource currentResource = null;

	/**
	 * 任务的下载量
	 */
	private final int MAX_TASK_NUM = 3;
	/**
	 * 现在的任务数
	 */
	private int current_task_num = 0;

	private AsyncDownLoadManager() {

	}

	/**
	 * 单例模式，获取AsyncManager类的唯一实例
	 * 
	 * @return
	 */
	public static AsyncDownLoadManager getAsyncManager(Context context) {
		if (mContext == null) {
			mContext = context;
		}

		return manager;
	}

	/**
	 * 添加下载任务
	 * 
	 * @param resource
	 * @param listener
	 */
	public void addDownTask(WebResource resource, OnDownLoadListener listener) {

		if (resource == null) {
			throw new NullPointerException("resource must not null");
		}
		taskMap.put(resource.id, listener);
		resList.add(resource);
		notifyNewTask();

	}

	/**
	 * 取消下载指定的资源
	 * 
	 * @param resource
	 */
	@SuppressWarnings("unchecked")
	public synchronized boolean cancel(WebResource resource) {
		Iterator<?> iter = curTaskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Long key = (Long) entry.getKey();
			AsyncDownLoad val = (AsyncDownLoad) entry.getValue();
			if (key == resource.id) {
				curTaskMap.remove(key).cancel(true);
				current_task_num--;
				val.isCancelTask = true;
				val.cancel(true);
				val = null;
				return true;

			}

		}

		for (int index = 0; index < resList.size(); index++) {
			WebResource r = resList.get(index);
			if (r.id == resource.id) {
				resList.remove(index);
				taskMap.remove(resource.id);
				notifyNewTask();
				return true;
			}
		}

		return false;
	}

	public synchronized void cancelAll() {
		Iterator<?> iter = curTaskMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Long key = (Long) entry.getKey();
			AsyncDownLoad val = (AsyncDownLoad) entry.getValue();

			// curTaskMap.remove(key);
			val.isCancelTask = true;
			val.cancel(true);

		}

		for (int index = 0; index < resList.size(); index++) {

			taskMap.remove(resList.get(index).id);
			resList.remove(index);

		}
	}

	/**
	 * 用于�下载任务完成后，通知manager是否新的下载任务
	 */
	public synchronized void notifyNewTask() {
		if (current_task_num <= MAX_TASK_NUM) {

			if (resList.size() > 0) {
				try {
					currentResource = null;
					currentResource = resList.remove(0);
					currentDownloadListener = taskMap
							.remove(currentResource.id);

					currentDownloadTask = new AsyncDownLoad(
							currentDownloadListener);
					currentDownloadTask.execute(currentResource);
					curTaskMap.put(currentResource.id, currentDownloadTask);
					current_task_num++;

				} catch (Throwable t) {

					currentDownloadTask = null;
					currentResource = null;
					currentDownloadListener = null;
				}
			}
		}

	}

	/**
	 * 异步下载任务类，适用于下载较大的文件，如音频视频文件等，下载较小的文件如图片等，不建议用此类
	 * 
	 * @author liuwei
	 * 
	 */
	private class AsyncDownLoad extends
			AsyncTask<WebResource, Integer, WebResource> {
		WebResource resFile = null;// 网络资源
		OnDownLoadListener listener = null;// 下载监听�?

		boolean isSucessedDownLoad = false;
		String downLoadResult = null;
		public boolean isCancelTask = false;

		public AsyncDownLoad(OnDownLoadListener listener) {
			this.listener = listener;
		}

		/**
		 * 处理下载前的准备工作
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		/**
		 * 异步下载方法
		 */
		@Override
		protected WebResource doInBackground(WebResource... params) {

			resFile = params[0];
			File parentFile = new File(resFile.filePath);
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			// 缓冲文件名，在没有完全下载成功时使用的临时存储名�?
			File bufferFile = new File(resFile.filePath + resFile.fileName
					+ ".bk");
			if (bufferFile.exists()) {
				bufferFile.delete();
			}
			File saveFile = new File(resFile.filePath + resFile.fileName);
			BufferedInputStream bis = null;
			RandomAccessFile raFile = null;
			FileOutputStream fos = null;
			DataOutputStream dos = null;
			String site = resFile.url;
			long fileSize = 0;
			long downLoadSize = 0;
			try {
				// 判断是否存在已经下载的部分，如果存在计算下载的长度，没有长度则为0
				if (!bufferFile.exists()) {
					bufferFile.createNewFile();
				}
				downLoadSize = bufferFile.length();

			} catch (Exception e) {

				e.printStackTrace();
			}

			byte[] buffer = new byte[1024 * 10];
			InputStream is = null;
			try {

				HttpGet httpGet = new HttpGet(site);
				// HttpParams httpParams=new BasicHttpParams();
				// HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
				// HttpConnectionParams.setSoTimeout(httpParams, 5000);

				httpGet.addHeader("Range", "bytes=" + downLoadSize + "-");
				HttpClient httpClient = getHttpClient(mContext);
				// HttpClient httpClient=new DefaultHttpClient(httpParams);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				//
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				HttpEntity entity = null;

				switch (responseCode) {
				// 返回sc.ok说明服务器不支持断点续传下载
				case HttpStatus.SC_OK:
					entity = httpResponse.getEntity();
					fileSize = entity.getContentLength();
					if (bufferFile.exists() && bufferFile.length() > 0) {
						bufferFile.delete();
						bufferFile.createNewFile();
					}

					break;

				case HttpStatus.SC_PARTIAL_CONTENT:// 返回SC_PARTIAL_CONTENT说明服务器支持断点续传下�?
					entity = httpResponse.getEntity();
					fileSize = entity.getContentLength() + downLoadSize;

					break;

				default:
					downLoadResult = "下载失败，错误代码" + responseCode;
					return null;

				}
				// 判断是否已经存在同名的文件，如果有删�?
				if (saveFile.exists() && saveFile.length() != fileSize) {

					saveFile.delete();

				}
				is = httpResponse.getEntity().getContent();
				if (is == null) {
					downLoadResult = "下载失败，未读取到资源";
					return null;
				}
				raFile = new RandomAccessFile(bufferFile, "rw");
				raFile.seek(downLoadSize);
				int len = 0;
				bis = new BufferedInputStream(is);
				while (!isCancelTask && (len = bis.read(buffer)) != -1) {// 循环获取文件内容

					raFile.write(buffer, 0, len);
					downLoadSize += len;
					int progress = (int) (downLoadSize * 100 / fileSize);
					publishProgress(progress);
				}

				if (bufferFile.length() >= fileSize) {// 下载完毕后，将文件重命名
					bufferFile.renameTo(saveFile);

				}

				isSucessedDownLoad = true;

			} catch (FileNotFoundException e) {
				downLoadResult = "服务器错";
				isSucessedDownLoad = false;

			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				downLoadResult = "连接超时";
				isSucessedDownLoad = false;

			} catch (Exception e) {
				e.printStackTrace();
				downLoadResult = "下载失败，请检查网络是否可用";
				isSucessedDownLoad = false;

			} finally {
				try {
					if (bis != null) {
						bis.close();
						bis = null;
					}
					if (raFile != null) {
						raFile.close();
						raFile = null;
					}
					if (fos != null) {
						fos.close();
						fos = null;
					}
					if (dos != null) {
						dos.close();
						dos = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return resFile;
		}

		/**
		 * 下载进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			listener.onUpdataDownLoadProgross(resFile, values[0]);
		}

		/**
		 * 处理异步下载结束要执行的方法
		 */
		@Override
		protected void onPostExecute(WebResource result) {

			super.onPostExecute(result);
			if (isSucessedDownLoad && !isCancelTask) {

				listener.onFinshDownLoad(resFile);
				current_task_num--;

			} else if (!isSucessedDownLoad) {
				if (listener != null) {

					listener.onError(downLoadResult);
				}
			}

			cancel(true);
			manager.notifyNewTask();
		}

	}

	/**
	 * 获得httpClient实例
	 * 
	 * @param context
	 *            activity上下
	 * @return HttpClient httpclient实例
	 */
	public static HttpClient getHttpClient(Context context) {
		try {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			HttpClient client = new DefaultHttpClient(ccm, params);
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				// 获取当前正在使用的APN接入�?
				Uri uri = Uri.parse("content://telephony/carriers/preferapn");
				Cursor mCursor = context.getContentResolver().query(uri, null,
						null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					// 游标移至第一条记录，当然也只有一�?
					String proxyStr = mCursor.getString(mCursor
							.getColumnIndex("proxy"));
					if (proxyStr != null && proxyStr.trim().length() > 0) {
						HttpHost proxy = new HttpHost(proxyStr, 80);
						client.getParams().setParameter(
								ConnRouteParams.DEFAULT_PROXY, proxy);
					}
					mCursor.close();
				}
			}
			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	/**
	 * 下载监听接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnDownLoadListener {

		/**
		 * 更新下载的进
		 */
		public void onUpdataDownLoadProgross(WebResource resource, int progross);

		/**
		 * 下载完成要处理的事件
		 */
		public void onFinshDownLoad(WebResource resource);

		/**
		 * 下载出错时处
		 * 
		 * @param error
		 */
		public void onError(String error);

	}

	/**
	 * 下载监听接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnDownLoadProgrossListener {
		public void onUpdataDownLoadProgross(WebResource resource, int progross);
	}

}
