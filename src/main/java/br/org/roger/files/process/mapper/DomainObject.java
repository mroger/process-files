package br.org.roger.files.process.mapper;

import java.util.List;

public class DomainObject {

	private String hash;
	private String timestamp;
	private String latitude;
	private String longitude;
	private String city;
	private String lastLatitude = "";
	private String lastLongitude = "";

	public DomainObject(
			String hash, String timestamp, String latitude,
			String longitude, String city) {
		this.hash = hash;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
	}

	public static List<DomainObject> calculateLastCoordinates(final List<DomainObject> domainValue) {
		for (int i = 0; i < domainValue.size(); i++) {
			DomainObject domain = domainValue.get(i);
			if (isFirstElement(i)) {
				domain.copyLastLatitudeFromLatitude();
				domain.copyLastLongitudeFromLongitude();
			} else {
				domain.setLastLatitude(domainValue.get(i - 1).getLatitude());
				domain.setLastLongitude(domainValue.get(i - 1).getLongitude());
			}
		}
		return domainValue;
	}

	private static boolean isFirstElement(int i) {
		return i == 0;
	}

	public String getHash() {
		return hash;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getCity() {
		return city;
	}

	public String getLastLatitude() {
		return lastLatitude;
	}

	public String getLastLongitude() {
		return lastLongitude;
	}
	
	public void setLastLatitude(String lastLatitude) {
		this.lastLatitude = lastLatitude;
	}

	public void setLastLongitude(String lastLongitude) {
		this.lastLongitude = lastLongitude;
	}
	
	public void copyLastLatitudeFromLatitude() {
		this.lastLatitude = latitude;
	}

	public void copyLastLongitudeFromLongitude() {
		this.lastLongitude = longitude;
	}

	@Override
	public String toString() {
		return "DomainObject [hash=" + hash + ", timestamp=" + timestamp + ", latitude=" + latitude + ", longitude="
				+ longitude + ", city=" + city + ", lastLatitude=" + lastLatitude + ", lastLongitude=" + lastLongitude
				+ "]";
	}
	
}
