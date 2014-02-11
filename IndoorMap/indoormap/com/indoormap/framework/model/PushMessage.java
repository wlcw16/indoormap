package com.indoormap.framework.model;

import java.io.Serializable;

public class PushMessage implements Serializable {

	private static final long serialVersionUID = 2014012011050000L;
	private String id;
	private String title;
	private String hyperlink;
	private String detail;
	private String picPath;
	private String triggerLocations = "";
	private long startTime;
	private long endTime;
	private long timestamp;
	private String state;
	private boolean isInAging = true;
	private boolean hasPopup = false;
	private boolean isRead = false;

	public void decode(String input, PushMessage oldObj) {
		try {
			String[] fields = input.split("#");
			id = fields[1];
			title = fields[6];
			detail = fields[9];
			picPath = fields[10];
			triggerLocations = fields[14];
			timestamp = Long.valueOf(fields[15]);
			state = fields[16];
			if (oldObj == null) {
				isInAging = true;
				hasPopup = false;
				isRead = false;
			} else {
				isInAging = oldObj.isInAging;
				hasPopup = oldObj.hasPopup;
				isRead = oldObj.isRead;
			}
		} catch (Exception e) {

		}
	}

	public boolean checkIsTrigger(String location) {
		boolean result = false;
		if (triggerLocations.indexOf(location) != -1) {
			return result = true;
		}
		return result;
	}

	public boolean checkIsInAging() {
		boolean result = false;
		if (System.currentTimeMillis() > startTime && System.currentTimeMillis() < endTime) {
			result = true;
		}
		return result;
	}

	public boolean checkIsOverAging() {
		boolean result = false;
		if (System.currentTimeMillis() > endTime) {
			result = true;
		}
		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHyperlink() {
		return hyperlink;
	}

	public void setHyperlink(String hyperlink) {
		this.hyperlink = hyperlink;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getTriggerLocations() {
		return triggerLocations;
	}

	public void setTriggerLocations(String triggerLocations) {
		this.triggerLocations = triggerLocations;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setInAging(boolean isInAging) {
		this.isInAging = isInAging;
	}

	public boolean isInAging() {
		return isInAging;
	}

	public void setHasPopup(boolean hasPopup) {
		this.hasPopup = hasPopup;
	}

	public boolean isHasPopup() {
		return hasPopup;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public boolean isRead() {
		return isRead;
	}

}
