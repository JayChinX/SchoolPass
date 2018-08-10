
package com.yuanding.schoolpass.baidu;

import android.content.Context;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.yuanding.schoolpass.utils.LogUtils;

/***
 * 61 ： GPS定位结果，GPS定位成功。 62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
 * 63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。 65 ： 定位缓存的结果。 66 ：
 * 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。 67 ：
 * 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
 * 161： 网络定位结果，网络定位定位成功。 162： 请求串密文解析失败。 167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
 * 502： key参数错误，请按照说明文档重新申请KEY。 505： key不存在或者非法，请按照说明文档重新申请KEY。 601：
 * key服务被开发者自己禁用，请按照说明文档重新申请KEY。 602： key
 * mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“
 * ;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。 501～700：key验证失败，请按照说明文档重新申请KEY。
 */
public class BaiduLocation {
    private BDLocation location = null;
    private IGetLocation mGetLocation;
    private int over_time = 15;// 定时超时时间
    private int over_time_temp = 15;// 定时超时时间
    private boolean mIsGps = false;
    private boolean isLocated = false;
    private LocationService locationService;

    public interface IGetLocation {
        void successful(BDLocation loca);// 成功

        void overTimeDo();// 超时

        void failure(String msg);// 失败
    }

    public void setCallBack(IGetLocation getLocatin) {
        mGetLocation = getLocatin;
    }

    public BaiduLocation(Context mCon) {
        // -----------location config ------------
        locationService = new LocationService(mCon);
        //注册监听
        locationService.registerListener(mListener);
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location) {
                setLocation(location);
                if (location.getLocType() == BDLocation.TypeGpsLocation) {//常量字段61 GPS定位结果
                    isLocated = true;
                    if (!mIsGps) {
                        mIsGps = true;
                    }
                    if (mGetLocation != null)
                        mGetLocation.successful(location);
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {//常量字段62 无法获取有效定位依据，定位失败
                    if (mGetLocation != null)
                        mGetLocation.failure("请检查运营商网络或者wifi网络是否正常开启，并尝试重新请求定位");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {//常量字段63 网络连接异常
                    if (mGetLocation != null)
                        mGetLocation.failure("请确认当前网络是否通畅，并尝试重新请求定位");
                } else if (location.getLocType() == BDLocation.TypeCacheLocation) {//常量字段65 飞行模式异常
                    if (mGetLocation != null)
                        mGetLocation.failure("请确认您手机是否处于飞行模式，并尝试重新请求定位");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {//常量字段66 离线定位结果
                    isLocated = true;
                    if (mIsGps) {
                        mIsGps = false;
                    }
                    if (mGetLocation != null)
                        mGetLocation.successful(location);
                } else if (location.getLocType() == BDLocation.TypeOffLineLocationFail) {//常量字段67 离线定位失败
                    if (mGetLocation != null)
                        mGetLocation.failure("请确认当前网络是否通畅，并尝试重新请求定位");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail) {//常量字段68 网络请求失败,基站离线定位结果
                    if (mGetLocation != null)
                        mGetLocation.failure("请确认当前网络是否通畅，并尝试重新请求定位");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {//常量字段161  网络定位结果
                    logE("网络定位结果,lat =" + location.getLatitude() + ",lon=" + location.getLongitude());
                    isLocated = true;
                    if (mIsGps) {
                        mIsGps = false;
                    }
                    if (mGetLocation != null)
                        mGetLocation.successful(location);
                }else if ( 161 < location.getLocType() && location.getLocType() < 168) {//常量字段167  权限禁用
                    if (mGetLocation != null)
                        mGetLocation.failure("请您检查是否禁用获取位置信息权限，并尝试重新请求定位");
                } else if (location.getLocType() == BDLocation.TypeNone) {//常量字段0  无效定位结果
                    if (mGetLocation != null)
                        mGetLocation.failure("无效定位结果，请尝试重新请求定位");
                }else if (location.getLocType() > 500){
                    if (mGetLocation != null)
                        mGetLocation.failure("请确认当前网络是否通畅，并尝试重新请求定位");
                }
                logE("BaiduLocation==registerLocationListener" + location.getLocType() + "," + location.getAddrStr() + ","
                        + location.getLongitude() + "," + location.getLatitude());
            }
        }
    };

    Handler locationHand = new Handler();
    Runnable thread = new Runnable() {
        public void run() {
            locationHand.postDelayed(thread, 1000);
            if (over_time == 0) {
                if (mGetLocation != null)
                    mGetLocation.overTimeDo();
            } else {
                over_time--;
            }
            logE("over_time==" + over_time);
        }
    };

    public BDLocation getLocation() {
        return location;
    }

    public void setLocation(BDLocation location) {
        this.location = location;
    }

    /*
     * 开始定位,参数为是否启用定时定位
     */
    public void startLocation(Boolean startTimer) {
        locationService.start();
        if (startTimer) {
            locationHand.post(thread);
        }
        logE("开始定位====mLocationClient.start()");
    }

    /*
     * 停止定位,参数为是否启用定时定位
     */
    public void stopLocation(Boolean stopTimer) {
        locationService.stop(); // 停止定位服务
        if (stopTimer) {
            over_time = over_time_temp;
            locationHand.removeCallbacks(thread);
        }
        logE("停止定位====mLocationClient.stop()");
    }
    
    public void appOnDestroy(){
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        locationService = null;
    }

    // 判断经纬度是否有效
    public boolean judgeLegalLatlong() {
        try {
            double myLng = getLocation().getLongitude();
            double myLat = getLocation().getLatitude();

            if (isLocationValid(myLng, myLat)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    // 判断定位方式，1是Gps，0是网络定位,-1是未定位,其他的
    public int judgeLocationWay() {
        if (isLocated) {
            if (mIsGps)
                return 1;
            else
                return 0;
        } else {
            return -1;
        }
    }

    // 判断经纬度是否有效『静态方法』(可做校准后的判断)
    // 0,0 86,28 无效
    private static double DIFF = 0.1f;

    public static boolean isLocationValid(double lng, double lat) {
        if (isInRange(lng, 0, DIFF) && isInRange(lat, 0, DIFF)) {
            return false;
        }

        if (isInRange(lng, 86.0f, DIFF) && isInRange(lat, 28.0f, DIFF)) {
            return false;
        }

        if (!isJingRange(lng) || !isweiRange(lat)) {
            return false;
        }

        return true;
    }

    private static boolean isJingRange(double lng_org) {
        if (-180 <= lng_org && lng_org <= 180) {
            return true;
        }
        return false;
    }

    private static boolean isweiRange(double lat_org) {
        if (-90 <= lat_org && lat_org <= 90) {
            return true;
        }
        return false;
    }

    private static boolean isInRange(double in_value, double mid_value, double diff) {
        if ((in_value > (mid_value - diff)) && (in_value < (mid_value + diff))) {
            return true;
        }

        return false;
    }

    public static void logE(String msg) {
        LogUtils.logE("BaiduLocation", "BaiduLocation=>" + msg);
    }
}
