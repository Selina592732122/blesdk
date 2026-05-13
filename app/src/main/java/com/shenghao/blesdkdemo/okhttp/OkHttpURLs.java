package com.shenghao.blesdkdemo.okhttp;


import com.shenghao.blesdkdemo.http.Network;

public class OkHttpURLs {

    public static final String URL_GET_DATE = "GetDateServices.asmx";

    /**
     * 登录
     */
    public static String loginSystem() {
        return String.format("%s/login/token", Network.getOkHttpServerHost());
    }

    /**
     * 获取新token
     */
    public static String getValidToken() {
        return String.format("%s/user/token", Network.getOkHttpServerHost());
    }

    /**
     * 获取设备列表
     */
    public static String getDeviceList() {
        return String.format("%s/user/terminal", Network.getOkHttpServerHost());
    }

    /**
     * 账号注册
     */
    public static String getSmsAuthCode() {
        return String.format("%s/login/authCode", Network.getOkHttpServerHost());
    }

    /**
     * 账号注销
     */
    public static String cancellation() {
        return String.format("%s/user/cancellation", Network.getOkHttpServerHost());
    }

    /**
     * 获取用户角色
     */
    public static String getUserRole() {
        return String.format("%s/user/role", Network.getOkHttpServerHost());
    }

    /**
     * 绑定设备
     */
    public static String bindTerminal() {
        return String.format("%s/user/terminal", Network.getOkHttpServerHost());
    }
    /**
     * 绑定设备
     */
    public static String updateVin() {
        return String.format("%s/user/updateVin", Network.getOkHttpServerHost());
    }

    /**
     * 上传设备信息
     */
    public static String uploadTerminalInfo() {
        return String.format("%s/user/terminal/parts", Network.getOkHttpServerHost());
    }

    /**
     * 解绑设备
     */
    public static String unbindTerminal() {
        return String.format("%s/user/terminal/unbind", Network.getOkHttpServerHost());
    }
    /**
     * 解绑设备
     */
    public static String allUnbind() {
        return String.format("%s/user/terminal/allUnbind", Network.getOkHttpServerHost());
    }

    /**
     * 重命名设备
     */
    public static String reNameTerminal() {
        return String.format("%s/user/terminal/name", Network.getOkHttpServerHost());
    }

    /**
     * 修改电池数量
     */
    public static String changeBatteryCount() {
        return String.format("%s/user/terminal/batteries", Network.getOkHttpServerHost());
    }

    /**
     * 设置当前默认设备
     */
    public static String updateDefaultTerminal() {
        return String.format("%s/user/terminal/default", Network.getOkHttpServerHost());
    }

    /**
     * 获取最新位置信息
     */
    public static String getLatestGpsInfo() {
        return String.format("%s/travel/location", Network.getOkHttpServerHost());
    }

    /**
     * 获取行程记录列表
     */
    public static String getRidingRecordList() {
        return String.format("%s/travel/records", Network.getOkHttpServerHost());
    }
    public static String getRidingRecordList2() {
        return String.format("%s/travel/recordList", Network.getOkHttpServerHost());
    }

    /**
     * 获取行程记录详情
     */
    public static String getRidingRecordDetail() {
        return String.format("%s/travel/record", Network.getOkHttpServerHost());
    }

    /**
     * 获取行程总里程
     */
    public static String getRidingAllDistance() {
        return String.format("%s/travel/mileages", Network.getOkHttpServerHost());
    }

    /**
     * 删除骑行记录
     */
    public static String removeRidingRecord() {
        return String.format("%s/travel/record", Network.getOkHttpServerHost());
    }

    /**
     * 获取版本更新
     */
    public static String getUpgradeVersion() {
        return String.format("%s/app/version?platform=android", Network.getOkHttpServerHost());
    }

    /**
     * 获取隐私政策url
     */
    public static String getPrivacyUrl() {
        return "https://shenghao.jifangwulian.com/app/privacypolicyshenghao.html";
    }

    /**
     * 设置圆形围栏
     */
    public static String addGeofenceCircle() {
        return String.format("%s/fence/circular", Network.getOkHttpServerHost());
    }

    /**
     * 设置多边形围栏
     */
    public static String addGeofencePolygon() {
        return String.format("%s/fence/polygon", Network.getOkHttpServerHost());
    }

    /**
     * 获取电子围栏列表
     */
    public static String getGeofenceList() {
        return String.format("%s/fence/list", Network.getOkHttpServerHost());
    }

    /**
     * 获取电子围栏详情
     */
    public static String getGeofenceDetail() {
        return String.format("%s/fence/detail", Network.getOkHttpServerHost());
    }

    /**
     * 删除电子围栏
     */
    public static String removeGeofence() {
        return String.format("%s/fence/remove", Network.getOkHttpServerHost());
    }

    /**
     * 获取通知列表
     */
    public static String getNoticeList() {
        return String.format("%s/notice/list", Network.getOkHttpServerHost());
    }

    /**
     * 删除通知
     */
    public static String removeNotice() {
        return String.format("%s/notice/remove", Network.getOkHttpServerHost());
    }

    /**
     * 删除全部通知
     */
    public static String removeAllNotice() {
        return String.format("%s/notice/all/remove", Network.getOkHttpServerHost());
    }

    /**
     * 已读全部通知
     */
    public static String readAllNotice() {
        return String.format("%s/notice/all/read", Network.getOkHttpServerHost());
    }

    /**
     * 获取通知详情
     */
    public static String getNoticeDetail() {
        return String.format("%s/notice/detail", Network.getOkHttpServerHost());
    }

    /**
     * 获取通知未读数
     */
    public static String getNoticeUnreadNum() {
        return String.format("%s/notice/unread/num", Network.getOkHttpServerHost());
    }

    /**
     * 测试sse推送接口
     */
    public static String getSseTest() {
        return String.format("%s/notice/test", Network.getOkHttpServerHost());
    }

    /**
     * 通知消息监听(sse连接)
     */
    public static String getNoticeListener() {
        return String.format("%s/notice/listener", Network.getOkHttpServerHost());
    }

    /**
     * 获取报警设置列表
     */
    public static String getAlarmSettingList() {
        return String.format("%s/user/alarm/setting", Network.getOkHttpServerHost());
    }

    /**
     * 报警设置
     */
    public static String changeAlarmSetting() {
        return String.format("%s/user/alarm/setting", Network.getOkHttpServerHost());
    }

    /**
     * 断开/恢复油电
     */
    public static String changePowerControl() {
        return String.format("%s/device/8105", Network.getOkHttpServerHost());
    }

    /**
     * 获取客服电话
     */
    public static String getFeedbackTel() {
        return String.format("%s/app/contact", Network.getOkHttpServerHost());
    }
    /**
     * 会员-下订单
     */
    public static String createMemberPayOrder() {
        return String.format("%s/member/order", Network.getOkHttpServerHost());
    }

    /**
     * 会员-查询套餐类型
     */
    public static String getMemberPayType() {
        return String.format("%s/member/type", Network.getOkHttpServerHost());
    }

    /**
     * 会员-查询设备套餐有效期
     */
    public static String getMemberExpireAt() {
        return String.format("%s/member/expireAt", Network.getOkHttpServerHost());
    }
    /**
     *上传头像等信息
     */
    public static String getUpdateUserInfo() {
        return String.format("%s/user/updateUserInfo", Network.getOkHttpServerHost());
    }
    //sos
    public static String getUpdateUser() {
        return String.format("%s/user/updateUser", Network.getOkHttpServerHost());
    }

    /**
     *获取头像等信息
     */
    public static String getUserInfo() {
        return String.format("%s/user/getUserInfo", Network.getOkHttpServerHost());
    }
    /**
     *极光绑定id
     */
    public static String bindJpushId() {
        return String.format("%s/user/bindJpushId", Network.getOkHttpServerHost());
    }
    /**
     *换绑手机号
     */
    public static String changePhone() {
        return String.format("%s/user/changePhone", Network.getOkHttpServerHost());
    }
    /**
     *获取绑定邮箱验证码
     */
    public static String getEmailCode() {
        return String.format("%s/user/getEmailCode", Network.getOkHttpServerHost());
    }
    /**
     *获取绑定邮箱验证码
     */
    public static String bindEmail() {
        return String.format("%s/user/bindEmail", Network.getOkHttpServerHost());
    }
    /**
     *设置邮箱密码并绑定邮箱
     */
    public static String bindEmailAndPassword() {
        return String.format("%s/user/bindEmailAndPassword", Network.getOkHttpServerHost());
    }
    /**
     *设置邮箱密码并绑定邮箱
     */
    public static String emailLogin() {
        return String.format("%s/login/emailLogin", Network.getOkHttpServerHost());
    }
    public static String phoneLogin() {
        return String.format("%s/login/phoneLogin", Network.getOkHttpServerHost());
    }
    /**
     *获取验证码（换绑手机）
     */
    public static String getBindCode() {
        return String.format("%s/user/getBindCode", Network.getOkHttpServerHost());
    }
    /**
     *车控信息查询
     */
    public static String getCarControlInfo() {
        return String.format("%s/vc/getCarControlInfo", Network.getOkHttpServerHost());
    }
    /**
     *车控信息设置
     */
    public static String setCarControl() {
        return String.format("%s/vc/setCarControl", Network.getOkHttpServerHost());
    }

    /**
     * 获取省市
     * @return
     */
    public static String getRegionList() {
        return String.format("%s/storeData/getRegionList", Network.getOkHttpServerHost());
    }

    /**
     * 获取门店
     * @return
     */
    public static String getStoreList() {
        return String.format("%s/storeData/getStoreList", Network.getOkHttpServerHost());
    }
    /**
     *获取车型列表
     */
    public static String getVehicleModelMenu() {
        return String.format("%s/vm/getVehicleModelMenu", Network.getOkHttpServerHost());
    }

    /**
     * 判断微信是否绑过手机
     * @return
     */
    public static String wxLoginBindPhone() {
        return String.format("%s/login/wxLoginBindPhone", Network.getOkHttpServerHost());
    }

    /**
     * 微信登录
     * @return
     */
    public static String wxLogin() {
        return String.format("%s/login/wxLogin", Network.getOkHttpServerHost());
    }
    /**
     * 一键挪车
     * @return
     */
    public static String oneKey() {
        return String.format("%s/device/8500", Network.getOkHttpServerHost());
    }
    public static String antiWolf() {
        return String.format("%s/device/antiWolf", Network.getOkHttpServerHost());
    }
    public static String adjust() {
        return String.format("%s/device/adjust", Network.getOkHttpServerHost());
    }
    public static String generateQRCode() {
        return String.format("%s/share/generateQRCode", Network.getOkHttpServerHost());
    }
    public static String qrCodeInvite() {
        return String.format("%s/share/qrCodeInvite", Network.getOkHttpServerHost());
    }
    public static String searchAccount() {
        return String.format("%s/share/searchAccount", Network.getOkHttpServerHost());
    }
    public static String shareInvite() {
        return String.format("%s/share/shareInvite", Network.getOkHttpServerHost());
    }
    public static String cancelShare() {
        return String.format("%s/share/cancelShare", Network.getOkHttpServerHost());
    }
    public static String getSharedList() {
        return String.format("%s/share/getSharedList", Network.getOkHttpServerHost());
    }
    public static String systemNoticeList() {
        return String.format("%s/share/systemNoticeList", Network.getOkHttpServerHost());
    }
    public static String updateMemberRemark() {
        return String.format("%s/share/updateMemberRemark", Network.getOkHttpServerHost());
    }
    public static String inviteResponse() {
        return String.format("%s/share/inviteResponse", Network.getOkHttpServerHost());
    }
    /**
     * keyPair
     */
    public static String keyPair() {
        return String.format("%s/device/8300", Network.getOkHttpServerHost());
    }
    public static String otaUpdate() {
        return String.format("%s/device/otaUpdate", Network.getOkHttpServerHost());
    }
    public static String getTheme() {
        return String.format("%s/user/getTheme", Network.getOkHttpServerHost());
    }
    public static String setTheme() {
        return String.format("%s/user/setTheme", Network.getOkHttpServerHost());
    }
}
