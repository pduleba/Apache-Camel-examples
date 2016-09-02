package com.pgs.processor;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pgs.model.ErrorDetail;
import com.pgs.model.LocationInfo;

/**
 * Created by jpolitowicz on 24.08.2016.
 */
@Service("placesProcessor")
public class PlacesProcessor {

    private static final Logger log = LoggerFactory.getLogger(PlacesProcessor.class);

    /**
     * Process respone from google places API https://developers.google.com/places/web-service/search
     *
     * @param resultMap respone from google places API
     * @return size of results array
     */
    public int getResultsCount(Map<String, Object> resultMap) {
        String status = (String)resultMap.get("status");
        log.info("Places result status [{}]", status);

        if (status != null && "OK".equals(status)) {
            ArrayList<?> results = (ArrayList<?>) resultMap.get("results");

            return results.size();
        } else {
            return -1;
        }
    }

    /**
     * Creates response with LocationInfo
     * @param gyms gyms count
     * @param schools schools count
     * @return LocationInfo response
     */
    public Response processLocationInfo(int gyms, int schools) {
        if (gyms < 0 || schools < 0) {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setErrorMessage("Request to Google Places API failed");
            errorDetail.setErrorCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorDetail).build();
        } else {
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setGymsCount(gyms);
            locationInfo.setSchoolsCount(schools);

            return Response.status(Response.Status.OK).entity(locationInfo).build();
        }
    }

}
