package com.pgs.processor;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pgs.model.ErrorDetail;
import com.pgs.model.Location;

/**
 * Created by jpolitowicz on 19.08.2016.
 */
@Service("geocodeProcessor")
public class GeocodeProcessor {

    private static final Logger log = LoggerFactory.getLogger(GeocodeProcessor.class);

    /**
     * Process response from google geocoding API https://developers.google.com/maps/documentation/geocoding/intro
     * @param resultMap Response from geocoding API
     * @return Address location response
     */
    @SuppressWarnings("unchecked")
    public Response processLocation(Map<String, Object> resultMap) {

        String status = (String)resultMap.get("status");
        log.info("Geocode result status [{}]", status);

        if (status != null && "OK".equals(status)) {
            ArrayList<?> results = (ArrayList<?>) resultMap.get("results");

            Map<String, Map<String, Object>> geometryList = (Map<String, Map<String, Object>>) results.get(0);
            Map<String, Object> geometrymap = geometryList.get("geometry");
			Map<String, Object> locationMap = (Map<String, Object>) geometrymap.get("location");

            Location location = new Location();
            location.setLatitude((Double) locationMap.get("lat"));
            location.setLongitude((Double) locationMap.get("lng"));

            return Response.ok(location, MediaType.APPLICATION_JSON).build();
        } else {

            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setErrorCode(Response.Status.NOT_FOUND.getStatusCode());
            errorDetail.setErrorMessage("Address not found");

            return Response.status(Response.Status.NOT_FOUND).entity(errorDetail).build();
        }
    }
}
