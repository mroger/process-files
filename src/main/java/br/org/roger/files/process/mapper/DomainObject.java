package br.org.roger.files.process.mapper;

import java.util.ArrayList;
import java.util.List;

public class DomainObject implements Cloneable {

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
	
	public DomainObject(
			String hash, String timestamp, String latitude,
			String longitude, String lastLatitude,
			String lastLongitude, String city) {
		this.hash = hash;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.lastLatitude = lastLatitude;
		this.lastLongitude = lastLongitude;
		this.city = city;
	}

	public static List<DomainObject> calculateLastCoordinates(final List<DomainObject> domainValues) {
		List<DomainObject> newDomainValues = new ArrayList<>();
		DomainObject domain;
		for (int indexElement = 0; indexElement < domainValues.size(); indexElement++) {
			domain = getDomainWithLastCoordinates(domainValues, indexElement);
			newDomainValues.add(domain);
		}
		return newDomainValues;
	}

	private static DomainObject getDomainWithLastCoordinates(final List<DomainObject> domainValues, int i) {
		DomainObject currentInputDomain = domainValues.get(i);
		String lastLatitude;
		String lastLongitude;
		if (isFirstElement(i)) {
			lastLatitude = currentInputDomain.getLatitude();
			lastLongitude = currentInputDomain.getLongitude();
		} else {
			DomainObject previousInputDomain = domainValues.get(i-1);
			lastLatitude = previousInputDomain.getLatitude();
			lastLongitude = previousInputDomain.getLongitude();
		}
		return new DomainObject(
			currentInputDomain.getHash(),
			currentInputDomain.getTimestamp(),
			currentInputDomain.getLatitude(), 
			currentInputDomain.getLongitude(),
			lastLatitude, 
			lastLongitude,
			currentInputDomain.getCity());
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((lastLatitude == null) ? 0 : lastLatitude.hashCode());
		result = prime * result + ((lastLongitude == null) ? 0 : lastLongitude.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainObject other = (DomainObject) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (lastLatitude == null) {
			if (other.lastLatitude != null)
				return false;
		} else if (!lastLatitude.equals(other.lastLatitude))
			return false;
		if (lastLongitude == null) {
			if (other.lastLongitude != null)
				return false;
		} else if (!lastLongitude.equals(other.lastLongitude))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return "DomainObject [hash=" + hash + ", timestamp=" + timestamp + ", latitude=" + latitude + ", longitude="
				+ longitude + ", city=" + city + ", lastLatitude=" + lastLatitude + ", lastLongitude=" + lastLongitude
				+ "]";
	}
	
}
