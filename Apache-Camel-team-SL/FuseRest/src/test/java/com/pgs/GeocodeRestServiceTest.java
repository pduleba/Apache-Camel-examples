package com.pgs;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.SimpleBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.cxf.helpers.IOUtils;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pgs.model.Location;
import com.pgs.model.LocationInfo;

/**
 * Created by jpolitowicz on 23.08.2016.
 */

public class GeocodeRestServiceTest extends CamelSpringTestSupport {

    @Test
    public void locationTest() throws Exception {

        context.getRouteDefinition("getLocationRoute").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                //add mock to the end of the route
                weaveAddLast().to("mock:end");

                //replace matching endpoint with mock
                weaveByToString(".*https4.*").replace().to("mock:google.api.geocoding");
            }
        });

        // we must manually start when we are done with all the advice with
        context.start();

        MockEndpoint endMock = getMockEndpoint("mock:end");
        MockEndpoint googleApiMock = getMockEndpoint("mock:google.api.geocoding");

        String json = IOUtils.toString(this.getClass().getResourceAsStream("/geocodeRestService/location/geolocation.json"));
        googleApiMock.returnReplyBody(new SimpleBuilder(json));

        Map<String, Object> headers = new HashMap<>();
        headers.put("operationName", "getLocation");

        template.sendBodyAndHeaders("direct:rest.geocodeService", null, headers);

        endMock.expectedMessageCount(1);
        endMock.assertIsSatisfied();

        Response response = endMock.getExchanges().get(0).getIn().getBody(Response.class);

        assertEquals(51.5194133, ((Location)response.getEntity()).getLatitude(), 0);
        assertEquals(-0.1269566, ((Location)response.getEntity()).getLongitude(), 0);

        context.stop();
    }

    @Test
    public void locationInfoTest() throws Exception {

        locationInfoTestRouteAdvice();

        // we must manually start when we are done with all the advice with
        context.start();

        MockEndpoint mockEnd = getMockEndpoint("mock:end");
        MockEndpoint mockSchoolsCountQuery = getMockEndpoint("mock:getSchoolsCount");
        MockEndpoint mockGymsCountQuery = getMockEndpoint("mock:getGymsCount");

        String schoolsCountJson = IOUtils.toString(this.getClass()
                .getResourceAsStream("/geocodeRestService/locationInfo/schoolsCount.json"));
        String gymsCountJson = IOUtils.toString(this.getClass()
                .getResourceAsStream("/geocodeRestService/locationInfo/gymsCount.json"));

        mockSchoolsCountQuery.returnReplyBody(new SimpleBuilder(schoolsCountJson));
        mockGymsCountQuery.returnReplyBody(new SimpleBuilder(gymsCountJson));

        Map<String, Object> headers = new HashMap<>();
        headers.put("operationName", "getLocationInfo");

        template.sendBodyAndHeaders("direct:rest.geocodeService", null, headers);

        Response response = mockEnd.getExchanges().get(0).getIn().getBody(Response.class);
        assertEquals(48, ((LocationInfo)response.getEntity()).getGymsCount());
        assertEquals(163, ((LocationInfo)response.getEntity()).getSchoolsCount());

        context.stop();
    }

    private void locationInfoTestRouteAdvice() throws Exception {
        context.getRouteDefinition("getLocationInfoRoute").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveAddLast().to("mock:end");
            }
        });

        context.getRouteDefinition("getSchoolsCountRoute").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveByToString(".*https4.*").replace().to("mock:getSchoolsCount");
            }
        });

        context.getRouteDefinition("getGymsCountRoute").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveByToString(".*https4.*").replace().to("mock:getGymsCount");
            }
        });
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("classpath:META-INF/spring/camel-context.xml");
    }
}
