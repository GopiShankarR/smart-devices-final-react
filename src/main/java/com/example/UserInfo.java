package com.example;
import java.io.Serializable;

public class UserInfo implements Serializable {
	private int id;
	private String username;
	private String password;
	private String usertype;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
  
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public UserInfo(String username, String password, String usertype) { 
		this.username = username;
		this.password = password;
		this.usertype = usertype;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", username=" + username + ", password=" + password + ", usertype=" + usertype
				+ "]";
	}
}
