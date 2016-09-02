package com.pgs.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by jpolitowicz on 17.08.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "locationInfo")
public class LocationInfo {

    @XmlElement
    private int schoolsCount;

    @XmlElement
    private int gymsCount;

    public int getSchoolsCount() {
        return schoolsCount;
    }

    public void setSchoolsCount(int schoolsCount) {
        this.schoolsCount = schoolsCount;
    }

    public int getGymsCount() {
        return gymsCount;
    }

    public void setGymsCount(int gymsCount) {
        this.gymsCount = gymsCount;
    }
}
