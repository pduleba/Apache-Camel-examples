package com.rest.routebuilder;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component("customXmlJsonRestRouteConvertion")
public class CustomXmlJsonRestRouteConvertion extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("cxfrs://http://localhost:9090?resourceClasses=com.rest.service.OrderService&bindingStyle=SimpleConsumer").beanRef(
				"fulfillmentCenterOneProcessor", "transformToOrderRequestMessage").log("My HttpResponse = ${body}");
	}

}
