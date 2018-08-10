
package com.yuanding.schoolpass.https;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.xutils.common.task.PriorityExecutor;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import android.content.Context;

/**
 * @author  焦海利
 * @version 创建时间：2016年12月14日 下午3:20:40
 * https验证
 */

public class HttpUtil {
    /** Https 证书验证对象 */
    private static SSLContext s_sSLContext = null;
    
    private static String checkNull(Object s) {
        return ( s== null) ? "" : s.toString();
    }
    /**
     * 
     * @Title: sendPost
     * @Description: TODO(Https_Send)
     * @param @param context
     * @param @param params
     * @param @param callBack    设定文件
     * @return void    返回类型
     * @throws
     */
    public static void send(Context context,String url,Map<String, Object> mapParams,int time_out,Callback.CommonCallback callBack) {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(time_out);
        Iterator iter = mapParams.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = checkNull(entry.getKey());
            String val = checkNull(entry.getValue());
            params.addBodyParameter(key, val);
        }
//        /* 判断https证书是否成功验证 */
//        SSLContext sslContext = getSSLContext(context);
//        if (null == sslContext) {
//            if (BuildConfig.DEBUG)
//                Log.d("HttpUtil", "Error:Can't Get SSLContext!");
//            return false;
//        }
//        // 绑定SSL证书
//        params.setSslSocketFactory(sslContext.getSocketFactory());
        x.http().request(HttpMethod.POST, params, callBack);
    }
    
    /**
     * 
     * @Title: upLoadFile
     * @Description: TODO(Https_上传文件   图片、文档、其他)
     * @param @param context
     * @param @param url
     * @param @param mapParams
     * @param @param callBack    设定文件
     * @return void    返回类型
     * @throws
     */
    public static void upLoadFile(Context context,String url,Map<String, Object> mapParams,Map<String, File> fileParams,
            int time_out,Callback.ProgressCallback callBack) {
        RequestParams params = new RequestParams(url);
        params.setMultipart(true); 
        params.setConnectTimeout(time_out);
        Iterator iter = mapParams.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = checkNull(entry.getKey());
            String val = checkNull(entry.getValue());
            params.addBodyParameter(key, val);
        }

        Iterator iter_files = fileParams.entrySet().iterator();
        while (iter_files.hasNext()) {
            Map.Entry entry = (Map.Entry) iter_files.next();
            String key = checkNull(entry.getKey());
            params.addBodyParameter(key,(File)(entry.getValue()));
        }
        
//      /* 判断https证书是否成功验证 */
//      SSLContext sslContext = getSSLContext(context);
//      if (null == sslContext) {
//          if (BuildConfig.DEBUG)
//              Log.d("HttpUtil", "Error:Can't Get SSLContext!");
//          return false;
//      }
//      // 绑定SSL证书
//      params.setSslSocketFactory(sslContext.getSocketFactory());
        x.http().request(HttpMethod.POST, params, callBack);
    }
    
    /**
     * @Title: downLoadFile
     * @Description: TODO(Https_下载文件   图片、文档、其他)
     * @param @param context
     * @param @param url
     * @param @param mapParams
     * @param @param callBack    设定文件
     * @return void    返回类型
     * @throws
     */
    public static void downLoadFile(Context context,String down_path,String save_path,int time_out,Callback.ProgressCallback<File> callBack) {
        RequestParams params = new RequestParams(down_path);
        params.setConnectTimeout(time_out);
        params.setSaveFilePath(save_path);

        params.setAutoResume(true);//设置是否在下载是自动断点续传
        params.setAutoRename(false);//设置是否根据头信息自动命名文件
        params.setExecutor(new PriorityExecutor(2, true));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
        params.setCancelFast(true);//是否可以被立即停止.
        x.http().request(HttpMethod.GET,params, callBack);
    }
    
//    /**
//     * @param context Activity（fragment）的资源上下文
//     * @param params 发送的请求
//     * @param callBack 回调对象（具体接口形式参见xUtils sample的httpFragment.java）
//     * @return true=正常调用 false＝异常调用
//     */
//    public static boolean sendSync(Context context, RequestParams params,Callback.TypedCallback callBack) throws Throwable {
//        /* 判断https证书是否成功验证 */
//        SSLContext sslContext = getSSLContext(context);
//        if (null == sslContext) {
//            if (BuildConfig.DEBUG)
//                Log.d("HttpUtil", "Error:Can't Get SSLContext!");
//            return false;
//        }
//        // 绑定SSL证书
//        params.setSslSocketFactory(sslContext.getSocketFactory());
//        x.http().requestSync(HttpMethod.POST, params, callBack);
//        return true;
//    }
//    
//    /**
//     * @param context Activity（fragment）的资源上下文
//     * @return InputStream
//     */
//    public static InputStream getRequestInputstream(Context context, String path) throws Exception {
//        /* 判断https证书是否成功验证 */
//        SSLContext sslContext = getSSLContext(context);
//        if (null == sslContext) {
//            if (BuildConfig.DEBUG)
//                Log.d("HttpUtil", "Error:Can't Get SSLContext!");
//            return null;
//        }
//        // 绑定SSL证书
//        java.net.URL url = new java.net.URL(path);
//        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//        conn.setSSLSocketFactory(sslContext.getSocketFactory());
//        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        conn.setDoInput(true);
//        // 连接
//        conn.connect();
//        return conn.getInputStream();
//    }
    
    /**
     * 获取Https的证书
     * 
     * @param context Activity（fragment）的上下文
     * @return SSL的上下文对象
     */
    private static SSLContext getSSLContext(Context context) {
        if (null != s_sSLContext) {
            return s_sSLContext;
        }

        CertificateFactory certificateFactory = null;
        InputStream inputStream = null;
        KeyStore keystore = null;
        String tmfAlgorithm = null;
        Certificate ca = null;
        TrustManagerFactory trustManagerFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");

            inputStream = context.getAssets().open("weixiaobang.crt");// 这里导入SSL证书文件
            try {// 读取证书
                ca = certificateFactory.generateCertificate(inputStream);
            } finally {
                inputStream.close();
            }
            
            //创建一个证书库，并将证书导入证书库  
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);//双向验证时使用  
            keystore.setCertificateEntry("ca", ca);
            
            // 初始化信任库
            tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
            trustManagerFactory.init(keystore);

            // Create an SSLContext that uses our TrustManager
            s_sSLContext = SSLContext.getInstance("TLS");
            s_sSLContext.init(null, trustManagerFactory.getTrustManagers(), null);
            // 信任所有证书 （官方不推荐使用）
            // s_sSLContext.init(null, new TrustManager[]{new X509TrustManager()
            // {
            // @Override
            // public X509Certificate[] getAcceptedIssuers() {
            // return null;
            // }
            // @Override
            // public void checkServerTrusted(X509Certificate[] arg0, String
            // arg1)
            // throws CertificateException {
            // }
            // @Override
            // public void checkClientTrusted(X509Certificate[] arg0, String
            // arg1)
            // throws CertificateException {
            // }
            // }}, new SecureRandom());
            return s_sSLContext;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
