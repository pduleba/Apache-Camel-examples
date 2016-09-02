package com.pgs.model;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jpolitowicz on 17.08.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "location")
public class Location {
    @DecimalMin("-90")
    @DecimalMax("90")
    @XmlElement
    private double latitude;

    @DecimalMin("-180")
    @DecimalMax("180")
    @XmlElement
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
