package com.shenghao.present;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shenghao.okhttp.OkHttpHelper;
import com.shenghao.okhttp.OkHttpMethod;
import com.shenghao.okhttp.OkHttpRequestParams;
import com.shenghao.okhttp.OkHttpResultCallBack;
import com.shenghao.okhttp.OkHttpURLs;

public class OkHttpPresent {
    private static final String TAG = "OkHttpPresent";

    /**
     * 获取短信验证码
     */
    public static void getSmsAuthCode(String phone, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("phone", phone);
        String url = OkHttpURLs.getSmsAuthCode();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取设备列表
     */
    public static void getDeviceList(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getDeviceList();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 登录
     */
    public static void loginSystem(String phone, String code, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("phone", phone);
        requestParams.addBody("code", code);
        String url = OkHttpURLs.loginSystem();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取用户角色（NORMAL-普通用户；MANUFACTURER-车辆生产厂家；GPS_DEVICE_MANAGER-GPS设备管理员）
     */
    public static void getUserRole(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getUserRole();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 使用旧token换新token
     */
    public static void getValidToken(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getValidToken();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 使用旧token换新token
     */
    public static String getValidTokenSync() {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getValidToken();
        return OkHttpHelper.getInstance()
                .execute(OkHttpMethod.M_GET, url, null, requestParams);
    }

    /**
     * 账号注销
     */
    public static void cancellation(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.cancellation();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 绑定设备
     */
    public static void bindTerminal(String terminalNo,String vehicleNo, int batteryCount,int vehicleModel, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("vin", vehicleNo);
        requestParams.addBody("batteries", String.valueOf(batteryCount));
        requestParams.addBody("vehicleModel", String.valueOf(vehicleModel));
        String url = OkHttpURLs.bindTerminal();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 绑定设备车架号
     */
    public static void updateVin(String terminalNo,String vehicleNo,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("vin", vehicleNo);
        String url = OkHttpURLs.updateVin();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 上传设备信息
     *
     * @param part1 车架号
     * @param part2 控制器
     */
    public static void uploadTerminalInfo(String terminalNo, String part1, String part2, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("part1", part1);
        requestParams.addBody("part2", part2);
        String url = OkHttpURLs.uploadTerminalInfo();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 解绑设备
     */
    public static void unbindTerminal(String terminalNo, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        String url = OkHttpURLs.unbindTerminal();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 解绑所有设备
     */
    public static void allUnbind(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.allUnbind();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 重命名设备
     */
    public static void reNameTerminal(String terminalNo, String name, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("name", name);
        String url = OkHttpURLs.reNameTerminal();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 修改设备电池数量
     */
    public static void changeBatteryCount(String terminalNo, int batteries, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("batteries", String.valueOf(batteries));
        String url = OkHttpURLs.changeBatteryCount();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 设置当前默认设备
     */
    public static void updateDefaultTerminal(String terminalNo, int status, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("status", String.valueOf(status));
        String url = OkHttpURLs.updateDefaultTerminal();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 获取最新位置
     */
    public static void getLatestGpsInfo(String terminalNo, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        String url = OkHttpURLs.getLatestGpsInfo();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取行程记录列表
     */
    public static String getRidingRecordListSync(int minStartId, String terminalNo) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("minId", String.valueOf(minStartId));
//        requestParams.addBody("size", String.valueOf(RidingDataActivity.PAGE_SIZE));
        String url = OkHttpURLs.getRidingRecordList();
        return OkHttpHelper.getInstance().execute(OkHttpMethod.M_GET, url, null, requestParams);
    }
    public static String getRidingRecordListSync2(int pageNum,int pageSize, String terminalNo) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("pageNum", String.valueOf(pageNum));
        requestParams.addBody("pageSize", String.valueOf(pageSize));
        String url = OkHttpURLs.getRidingRecordList2();
        return OkHttpHelper.getInstance().execute(OkHttpMethod.M_GET, url, null, requestParams);
    }

    /**
     * 获取行程记录详情
     */
    public static void getRidingRecordDetail(String terminalNo, int id, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("id", String.valueOf(id));
        String url = OkHttpURLs.getRidingRecordDetail();
        OkHttpHelper.getInstance().enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取行程总里程
     */
    public static void getRidingAllDistance(String terminalNo, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        String url = OkHttpURLs.getRidingAllDistance();
        OkHttpHelper.getInstance().enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取行程总里程
     */
    public static String getRidingAllDistanceSync(String terminalNo) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        String url = OkHttpURLs.getRidingAllDistance();
        return OkHttpHelper.getInstance().execute(OkHttpMethod.M_GET, url, null, requestParams);
    }

    /**
     * 删除骑行记录
     */
    public static void removeRidingRecord(String terminalNo, int id, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("id", String.valueOf(id));
        String url = OkHttpURLs.removeRidingRecord();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 获取版本更新
     */
    public static void getUpgradeVersion(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getUpgradeVersion();
        OkHttpHelper.getInstance().enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 设置圆形围栏
     */
    public static void addGeofenceCircle(String centerPoint, int radius, int id, String terminalNo, String fenceName, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("centerPoint", centerPoint);
        requestParams.addBody("radius", String.valueOf(radius));
        if (id != 0) {
            requestParams.addBody("id", String.valueOf(id));
        }
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("fenceName", fenceName);
        String url = OkHttpURLs.addGeofenceCircle();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 设置多边形围栏
     */
    public static void addGeofencePolygon(String points, int id, String terminalNo, String fenceName, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("points", points);
        if (id != 0) {
            requestParams.addBody("id", String.valueOf(id));
        }
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("fenceName", fenceName);
        String url = OkHttpURLs.addGeofencePolygon();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 获取电子围栏列表
     */
    public static void getGeofenceList(String terminalNo, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        String url = OkHttpURLs.getGeofenceList();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取电子围栏详情
     */
    public static void getGeofenceDetail(int id, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("id", String.valueOf(id));
        String url = OkHttpURLs.getGeofenceDetail();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 删除电子围栏
     */
    public static void removeGeofence(int id, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("id", String.valueOf(id));
        String url = OkHttpURLs.removeGeofence();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 获取通知列表
     */
    public static void getNoticeList(int minId, int size, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("minId", String.valueOf(minId));
        requestParams.addBody("size", String.valueOf(size));
        String url = OkHttpURLs.getNoticeList();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 删除通知消息
     */
    public static void removeNotice(String idList, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("idList", idList);
        String url = OkHttpURLs.removeNotice();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 删除全部通知消息
     */
    public static void removeAllNotice(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.removeAllNotice();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 已读全部通知消息
     */
    public static void readAllNotice(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.readAllNotice();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取通知详情
     */
    public static void getNoticeDetail(int id, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("id", String.valueOf(id));
        String url = OkHttpURLs.getNoticeDetail();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取消息未读数量
     */
    public static void getNoticeUnreadNum(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getNoticeUnreadNum();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 测试SSE接口
     * //TODO superLT
     */
    public static void getNoticeTest(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getSseTest();
        requestParams.addBody("phone", "17821712531");
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 查询报警设置
     */
    public static void getAlarmSettingList(String terminalNo,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo",terminalNo);
        String url = OkHttpURLs.getAlarmSettingList();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 报警设置更改
     */
    public static void changeAlarmSetting(String terminalNo,String noticeType, int value, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("noticeType", noticeType);
        requestParams.addBody("value", String.valueOf(value));
        String url = OkHttpURLs.changeAlarmSetting();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 请求断开/恢复油电
     *
     * @param command 64-断开油电;65-恢复油电
     */
    public static void changePowerControl(String terminalNo, int command, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("command", String.valueOf(command));
        String url = OkHttpURLs.changePowerControl();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 获取客服电话
     */
    public static void getFeedbackTel(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getFeedbackTel();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 会员下订单
     *
     * @param amount 金额，单位分
     * @param amountType 套餐类型，ONE_YEAR/TWO_YEAR/THREE_YEAR
     */
    public static void createMemberPayOrder(String terminalNo, int amount, String amountType, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("amount", String.valueOf(amount));
        requestParams.addBody("amountType", amountType);
        String url = OkHttpURLs.createMemberPayOrder();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 会员-查询套餐类型
     */
    public static void getMemberPayType(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getMemberPayType();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 会员-查询设备套餐有效期
     */
    public static void getMemberExpireAt(String terminalNo, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        String url = OkHttpURLs.getMemberExpireAt();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 头像上传
     * @param nickName
     * @param fileData
     * @param callBack
     */
    public static void updateUserInfo(String nickName,String fileData, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("nickName", nickName);
        requestParams.addBody("fileData", fileData);
        String url = OkHttpURLs.getUpdateUserInfo();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    //sos设置
    public static void updateUser(String emergencyContact,String emergencyContactPhone,String emergencyContactRelation,OkHttpResultCallBack callBack) {
//        OkHttpRequestParams requestParams = new OkHttpRequestParams();
//        requestParams.addBody("emergencyContact", emergencyContact);
//        requestParams.addBody("emergencyContactPhone", emergencyContactPhone);
//        requestParams.addBody("emergencyContactRelation", emergencyContactRelation);
//        String url = OkHttpURLs.getUpdateUser();
//        OkHttpHelper.getInstance()
//                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("emergencyContact", emergencyContact);
        jsonObject.addProperty("emergencyContactPhone", emergencyContactPhone);
        jsonObject.addProperty("emergencyContactRelation", emergencyContactRelation);
        String jsonBody = new Gson().toJson(jsonObject);
        String url = OkHttpURLs.getUpdateUser();
        OkHttpHelper.getInstance().postJson(url,null,jsonBody,callBack);
    }

    /**
     * 获取昵称等信息
     * @param callBack
     */
    public static void getUserInfo(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getUserInfo();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 上传极光推送id
     * @param callBack
     */
    public static void bindJpushId(String registrationId,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("registrationId", registrationId);
        String url = OkHttpURLs.bindJpushId();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 修改手机
     * @param callBack
     */
    public static void changePhone(String phone,String code,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("phone", phone);
        requestParams.addBody("code", code);
        String url = OkHttpURLs.changePhone();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }

    /**
     * 获取绑定邮箱验证码 type 1:绑定邮箱
     * @param callBack
     */
    public static void getEmailCode(String email,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("email", email);
        String url = OkHttpURLs.getEmailCode();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 获取绑定邮箱验证码,忘记密码 type 2:找回邮箱密码
     * @param callBack
     */
    public static void getEmailCode(String email,String type,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("email", email);
        requestParams.addBody("type", type);
        String url = OkHttpURLs.getEmailCode();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 验证邮箱
     * @param callBack
     */
    public static void bindEmail(String email,String code,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("email", email);
        requestParams.addBody("code", code);
        String url = OkHttpURLs.bindEmail();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 设置邮箱密码并绑定邮箱
     * @param callBack
     */
    public static void bindEmailAndPassword(String email,String password,String phone,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("email", email);
        requestParams.addBody("password", password);
        if(!TextUtils.isEmpty(phone))
            requestParams.addBody("phone",phone);//忘记密码时候没有这个参数
        String url = OkHttpURLs.bindEmailAndPassword();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 设置邮箱密码登录
     * @param callBack
     */
    public static void emailLogin(String email,String password,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("email", email);
        requestParams.addBody("password", password);
        String url = OkHttpURLs.emailLogin();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    public static void phoneLogin(String phone,String password,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("phone", phone);
        requestParams.addBody("password", password);
        String url = OkHttpURLs.phoneLogin();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 获取短信验证码(换绑手机的时候)
     */
    public static void getBindCode(String phone, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("phone", phone);
        String url = OkHttpURLs.getBindCode();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 车控信息查询
     * @param callBack
     */
    public static void getCarControlInfo(String terminalNo,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        String url = OkHttpURLs.getCarControlInfo();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 车控信息设置
     * @param callBack
     */
    public static void setCarControl(String terminalNo,String type,String status,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("type", type);
        requestParams.addBody("status", status);
        String url = OkHttpURLs.setCarControl();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取省市
     * @param callBack
     */
    public static void getRegionList(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getRegionList();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }

    /**
     * 获取门店
     * @param callBack
     */
    public static void getStoreList(String 	cityId,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("cityId", cityId);
        String url = OkHttpURLs.getStoreList();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 获取车型列表
     * @param callBack
     */
    public static void getVehicleModelMenu(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getVehicleModelMenu();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 判断微信是否绑过手机
     * @param callBack
     */
    public static void wxLoginBindPhone(String openId,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.wxLoginBindPhone();
        requestParams.addBody("openId",openId);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 微信登录
     * @param callBack
     */
    public static void wxLogin(String phone,String code,String openId,String avatar,String nickName,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.wxLogin();
        requestParams.addBody("phone",phone);
        requestParams.addBody("code",code);
        requestParams.addBody("openId",openId);
        requestParams.addBody("avatar",avatar);
        requestParams.addBody("nickName",nickName);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    public static void oneKey(String terminalNo,String type,int direction,int angle,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.oneKey();
        requestParams.addBody("terminalNo",terminalNo);
        requestParams.addBody("type",type);
        requestParams.addBody("param",direction+"");//前进0 后退1
        requestParams.addBody("angle",angle+"");
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    public static void airControl(String terminalNo,int type,int param,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.oneKey();
        requestParams.addBody("terminalNo",terminalNo);
        requestParams.addBody("type",type+"");//type：   06-尾门开关。07-空调开关。0B-车窗开关 21-制冷制热开关,10=危险报警灯   27=灯光控制   29=后视镜折叠 30=伴我回家
        requestParams.addBody("param",param+"");//前进0 后退1  param：0-关 1-开
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    public static void airControl2(String terminalNo,int type,int param,int angle,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.oneKey();
        requestParams.addBody("terminalNo",terminalNo);
        requestParams.addBody("type",type+"");//type：   06-尾门开关。07-空调开关。0B-车窗开关 21-制冷制热开关,10=危险报警灯   27=灯光控制   29=后视镜折叠 30=伴我回家
        requestParams.addBody("param",param+"");//前进0 后退1  param：0-关 1-开
        requestParams.addBody("angle",angle+"");//前进0 后退1  param：0-关 1-开
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    public static void antiWolf(String terminalNo,int param,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.antiWolf();
        requestParams.addBody("terminalNo",terminalNo);
        requestParams.addBody("param",param+"");//param：0-关 1-开
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    public static void adjust(String terminalNo,int status,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.adjust();
        requestParams.addBody("terminalNo",terminalNo);
        requestParams.addBody("status",status+"");// 3-制热3档 2-制热2档 1-制热1档 0-关闭空调 -1-制冷1档 -2-制冷2档 -3-制冷3档
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 生成二维码
     * @param callBack
     */
    public static void generateQRCode(String terminalNo,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.generateQRCode();
        requestParams.addBody("terminalNo",terminalNo);///user/batteryRating
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 二维码邀请
     * @param callBack
     */
    public static void qrCodeInvite(String terminalNo,String inviterPhone, String timestamp,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.qrCodeInvite();
        requestParams.addBody("terminalNo",terminalNo);
        requestParams.addBody("inviterPhone",inviterPhone);
        requestParams.addBody("timestamp",timestamp);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 查询账号
     * @param callBack
     */
    public static void searchAccount(String phone,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.searchAccount();
        requestParams.addBody("phone",phone);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 发起共享邀请
     * @param callBack
     */
    public static void shareInvite(String phone,String terminalNo,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.shareInvite();
        requestParams.addBody("phone",phone);
        requestParams.addBody("terminalNo",terminalNo);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 取消-退出 共享
     * @param callBack
     */
    public static void cancelShare(String terminalNo,String phone,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.cancelShare();
        requestParams.addBody("terminalNo",terminalNo);
        if(!TextUtils.isEmpty(phone))
            requestParams.addBody("phone",phone);//退出共享时不传值
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 获取共享用户列表
     * @param callBack
     */
    public static void getSharedList(String terminalNo,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getSharedList();
        requestParams.addBody("terminalNo",terminalNo);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 系统通知
     * @param callBack
     */
    public static void systemNoticeList(int minId, int size,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.systemNoticeList();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 备注
     * @param callBack
     */
    public static void updateMemberRemark(String terminalNo,String phone,String remark,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.updateMemberRemark();
        requestParams.addBody("terminalNo",terminalNo);
        requestParams.addBody("phone",phone);
        requestParams.addBody("remark",remark);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 同意-拒绝 邀请
     * @param callBack
     */
    public static void inviteResponse(String status,String id,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.inviteResponse();
        requestParams.addBody("status",status);
        requestParams.addBody("id",id);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 钥匙配对
     *
     */
    public static void keyPair(String terminalNo, OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        requestParams.addBody("terminalNo", terminalNo);
        requestParams.addBody("command", "2");
        requestParams.addBody("content", "BT433PAIR#");
        String url = OkHttpURLs.keyPair();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * ota升级
     * @param terminalNo
     * @param callBack
     */
    public static void otaUpdate(String terminalNo,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.otaUpdate();
        requestParams.addBody("terminalNo",terminalNo);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
    /**
     * 主题列表
     * @param callBack
     */
    public static void getTheme(OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.getTheme();
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_GET, url, null, requestParams, callBack);
    }
    /**
     * 主题设置
     * @param callBack
     */
    public static void setTheme(String themeId,OkHttpResultCallBack callBack) {
        OkHttpRequestParams requestParams = new OkHttpRequestParams();
        String url = OkHttpURLs.setTheme();
        requestParams.addBody("themeId",themeId);
        OkHttpHelper.getInstance()
                .enqueueAll(OkHttpMethod.M_POST, url, null, requestParams, callBack);
    }
}
