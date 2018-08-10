
package com.yuanding.schoolpass.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.yuanding.schoolpass.service.Api;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年4月22日 下午3:53:42 
 * Api签名排序
 */
public class ApiSignSort {

    private static String checkNull(Object s) {
        return ( s== null) ? "" : s.toString();
    }

    public static String getSignature(Map<String, Object> params) {
        Map<String, String> map = new HashMap<String, String>();
        List<Map.Entry<String, String>> mappingList = null;

        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = checkNull(entry.getKey());
            String val = checkNull(entry.getValue());
            map.put(key, val);
        }
        map.put(AppStrStatic.ZCODE, AppStrStatic.ZCODE_VALUE);
        
        // 通过ArrayList构造函数把map.entrySet()转换成list
        mappingList = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        // 通过比较器实现比较排序
        Collections.sort(mappingList, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> mapping1,
                    Map.Entry<String, String> mapping2) {
                return mapping1.getKey().compareTo(mapping2.getKey());
            }
        });
        String str = "";
        for (Map.Entry<String, String> mapping : mappingList) {
            LogUtils.logD(Api.TAG, mapping.getKey() + ":" + mapping.getValue());
            String aa = mapping.getKey() + mapping.getValue();
            str += aa;
        }
        return PubMehods.getMD5(str);
    }

}
