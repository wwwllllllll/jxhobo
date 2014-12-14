package com.jxhobo.http.util;

import com.jxhobo.app.BaseApplication;

/**
 * Created with IntelliJ IDEA.
 * User: fengerhu
 * Date: 14-8-14
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
public class Account {
    private final String KEY_USER_ID = "userId";

    private final String KEY_USER_NAME = "userName";

    private final String KEY_PHONE = "phone";

    private final String KEY_EMAIL = "email";

    private final String KEY_USER_TOKEN = "userToken";

    private final String KEY_USER_TYPE = "userType";

    private final String KEY_LIMIT_TIME = "limitTime";

    private final String USER_CARD = "user_card";

    private final String RECOMMEND_NAME = "recommend_name";

    private final String RECOMMEND_PHONE = "recommend_phone";

    private final String KEY_USER_LAST_LOGIN_TIME = "userLastLoginTime";

    private final String KEY_USER_SKIP_LOGIN_TIME = "userSkipLoginTime";

    private final String KEY_USER_CHANNEL_ID = "userChannelId";

    private final String NOTIFY_LAST_TIME = "notifyLastTime";


    private final long loginExpiredTime = 7 * 3600 * 24 * 1000;

    private final String RECEIVED_NOTIFY_IDS = "receivedNotifyIds";

    private final String TEMPLATE_CODE = "templateCode";

    private static Account instance = new Account();


    public static Account getInstance() {
        return instance;
    }

    public Long getUserId() {
        return BaseApplication.instance().getSharedPreferences().getLong(KEY_USER_ID, -1l);
    }

    public Account setUserId(Long userId) {
        BaseApplication.instance().getSharedPreferences().edit().putLong(KEY_USER_ID, userId).commit();
        return this;
    }

    public String getUserName() {
        return BaseApplication.instance().getSharedPreferences().getString(KEY_USER_NAME, "");
    }

    public Account setUserName(String userName) {
        BaseApplication.instance().getSharedPreferences().edit().putString(KEY_USER_NAME, userName).commit();
        return this;
    }

    public String getToken() {
        return BaseApplication.instance().getSharedPreferences().getString(KEY_USER_TOKEN, "");
    }

    public Account setToken(String token) {
        BaseApplication.instance().getSharedPreferences().edit().putString(KEY_USER_TOKEN, token).commit();
        return this;
    }

    public Long getLastLoginTime() {
        return BaseApplication.instance().getSharedPreferences().getLong(KEY_USER_LAST_LOGIN_TIME, -1);
    }

    public Account setLastLoginTime(Long lastLoginTime) {
        BaseApplication.instance().getSharedPreferences().edit().putLong(KEY_USER_LAST_LOGIN_TIME, lastLoginTime).commit();
        return this;
    }

    public Long getSkipLoginTime() {
        return BaseApplication.instance().getSharedPreferences().getLong(KEY_USER_SKIP_LOGIN_TIME, -1);
    }

    public Account setSkipLoginTime(Long skipLoginTime) {
        BaseApplication.instance().getSharedPreferences().edit().putLong(KEY_USER_SKIP_LOGIN_TIME, skipLoginTime).commit();
        return this;
    }

    public String getChannelId() {
        return BaseApplication.instance().getSharedPreferences().getString(KEY_USER_CHANNEL_ID, "");
    }

    public Account setChannelId(String channelId) {
        BaseApplication.instance().getSharedPreferences().edit().putString(KEY_USER_CHANNEL_ID, channelId).commit();
        return this;
    }

    public boolean isLoginExpired() {
        Long lastLoginTime = getLastLoginTime();
        if (lastLoginTime == -1 || ((System.currentTimeMillis() - lastLoginTime) > loginExpiredTime)) {
            return true;
        }
        return false;
    }

    public Account setPhone(String phone) {
        BaseApplication.instance().getSharedPreferences().edit().putString(KEY_PHONE, phone).commit();
        return this;
    }

    public String getPhone() {
        return BaseApplication.instance().getSharedPreferences().getString(KEY_PHONE, "");
    }

    public Account setEmail(String email) {
        BaseApplication.instance().getSharedPreferences().edit().putString(KEY_EMAIL, email).commit();
        return this;
    }

    public String getEmail() {
        return BaseApplication.instance().getSharedPreferences().getString(KEY_EMAIL, "");
    }

    public Long getNotifyLastTime() {
        return BaseApplication.instance().getSharedPreferences().getLong(NOTIFY_LAST_TIME, -1);
    }

    public Account setNotifyLastTime(long time) {
        BaseApplication.instance().getSharedPreferences().edit().putLong(NOTIFY_LAST_TIME, time).commit();
        return this;
    }

    private String getReceivedNotifyIds() {
        return BaseApplication.instance().getSharedPreferences().getString(RECEIVED_NOTIFY_IDS, "");
    }

    private Account setReceivedNotifyIds(String ids) {
        BaseApplication.instance().getSharedPreferences().edit().putString(RECEIVED_NOTIFY_IDS, ids).commit();
        return this;
    }


    /**
     * 获取当前用户的类型
     *
     * @return
     */
    public int getUserType() {
        return BaseApplication.instance().getSharedPreferences().getInt(KEY_USER_TYPE, 0);
    }

    /**
     * 设置当前用户的类型
     *
     * @return
     */

    public Account setUserType(int userType) {
        BaseApplication.instance().getSharedPreferences().edit().putInt(KEY_USER_TYPE, userType).commit();
        return this;
    }


    /**
     * 获取当前用户的身份证号
     *
     * @return
     */
    public String getUserCard() {
        return BaseApplication.instance().getSharedPreferences().getString(USER_CARD, "");
    }

    /**
     * 设置当前用户的身份证号
     *
     * @return
     */

    public Account setUserCard(String userCard) {
        BaseApplication.instance().getSharedPreferences().edit().putString(USER_CARD, userCard).commit();
        return this;
    }


    /**
     * 获取当前用户推荐人姓名
     *
     * @return
     */
    public String getRecommendName() {
        return BaseApplication.instance().getSharedPreferences().getString(RECOMMEND_NAME, "");
    }

    /**
     * 设置当前用户的推荐人姓名
     *
     * @return
     */

    public Account setRecommendName(String recommendName) {
        BaseApplication.instance().getSharedPreferences().edit().putString(RECOMMEND_NAME, recommendName).commit();
        return this;
    }


    /**
     * 获取当前用户的推荐人手机号
     *
     * @return
     */
    public String getRecommendPhone() {
        return BaseApplication.instance().getSharedPreferences().getString(RECOMMEND_PHONE, "");
    }

    /**
     * 设置当前用户的类型
     *
     * @return
     */

    public Account setRecommendPhone(String recommendPhone) {
        BaseApplication.instance().getSharedPreferences().edit().putString(RECOMMEND_PHONE, recommendPhone).commit();
        return this;
    }


    /**
     * 获取当前用户的类型
     *
     * @return
     */
    public int getUserLimitTime() {
        return BaseApplication.instance().getSharedPreferences().getInt(KEY_LIMIT_TIME, 0);
    }

    /**
     * 设置当前用户的类型
     *
     * @return
     */

    public Account setUserLimitTime(int limitTime) {
        BaseApplication.instance().getSharedPreferences().edit().putInt(KEY_LIMIT_TIME, limitTime).commit();
        return this;
    }


    public void updateInfo(String phone, String password, String userToken, int userType, int limitTime) {
        setPhone(phone).setUserType(userType).setToken(userToken).setUserLimitTime(limitTime);
    }

    public void updateInfo(String phone, String password, String userName, String userCard,
                           String recommendName, String recommendPhone, String userToken, int userType, int limitTime) {
        setPhone(phone).setUserType(userType).setToken(userToken)
                .setUserLimitTime(limitTime).setUserName(userName)
                .setUserCard(userCard).setRecommendName(recommendName)
                .setRecommendPhone(recommendPhone);
    }

    public void updateInfo(String userToken, int userType, int limitTime) {
        setUserType(userType).setToken(userToken).setUserLimitTime(limitTime);
    }

    /**
     * 添加通知id
     *
     * @param id
     */
    public void addNotifyId(String id) {
        String receivedNotifyIds = getReceivedNotifyIds();
        if (receivedNotifyIds.length() > 3000) {
            //缓存100-200条左右
            receivedNotifyIds = receivedNotifyIds.substring(receivedNotifyIds.indexOf(","));
        }

        if (receivedNotifyIds.equals(""))
            receivedNotifyIds = id;
        else
            receivedNotifyIds += "," + id;
        setReceivedNotifyIds(receivedNotifyIds);
    }

    /**
     * 判断通知是否已存在
     *
     * @param id
     * @return
     */
    public boolean isExitsNotify(String id) {
        String receivedNotifyIds = getReceivedNotifyIds();
        receivedNotifyIds = "," + receivedNotifyIds + ",";
        return receivedNotifyIds.contains("," + id + ",");
    }


}
