package br.org.roger.files.process;

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
