package com.marsss.homesecurity;

public class Camera implements Comparable<Camera>{
	private int id;
	private String name;
	private String status;
	
	public Camera(int id, String name, String status) {
		this.id = id;
		this.name = name;
		this.status = status;
	}

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getStatus() { return status; }

	public void setStatus(String status) { this.status = status; }

	@Override
	public int compareTo(Camera o) {
		// TODO Auto-generated method stub
		return o.getId()-this.getId();
	}
	
}
