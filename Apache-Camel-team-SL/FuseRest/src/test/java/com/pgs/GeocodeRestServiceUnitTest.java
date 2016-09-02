package com.pgs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgs.model.Location;
import com.pgs.processor.GeocodeProcessor;
import org.apache.cxf.helpers.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by jpolitowicz on 22.08.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/camel-context.xml")
public class GeocodeRestServiceUnitTest {

    @Autowired
    private GeocodeProcessor geocodeProcessor;

    @Test
    public void testLocationPositive() throws Exception {

        String json = IOUtils.toString( this.getClass().getResourceAsStream("/geocodeRestService/location/geolocation.json"));

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapObject = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});

        Response response = geocodeProcessor.processLocation(mapObject);

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(51.5194133, ((Location)response.getEntity()).getLatitude(), 0);
        Assert.assertEquals(-0.1269566, ((Location)response.getEntity()).getLongitude(), 0);
    }

    @Test
    public void testLocationNegative() throws Exception {

        String json = IOUtils.toString(this.getClass().getResourceAsStream("/geocodeRestService/location/geolocation-negative.json"));

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapObject = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        Response response = geocodeProcessor.processLocation(mapObject);

        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}
