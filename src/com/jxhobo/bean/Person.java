package com.jxhobo.bean;

public class Person {
    private String _id;
    private String key;
    private int zhucecode;
    private String token;
    private int userType;
    private int uId;

	public Person() {
		super();
	}
	
	public Person(String _id, String key, int zhucecode) {
		super();
		this._id = _id;
		this.key = key;
		this.zhucecode = zhucecode;
	}

    public Person(String _id, String key, int zhucecode, String token, int userType, int uId) {
        this._id = _id;
        this.key = key;
        this.zhucecode = zhucecode;
        this.token = token;
        this.userType = userType;
        this.uId = uId;
    }

    public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public int getZhucecode() {
		return zhucecode;
	}

	public void setZhucecode(int zhucecode) {
		this.zhucecode = zhucecode;
	}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }
}
