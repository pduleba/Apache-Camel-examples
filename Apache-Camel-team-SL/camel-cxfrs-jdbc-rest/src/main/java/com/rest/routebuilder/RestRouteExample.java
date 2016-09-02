package com.rest.routebuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component("restRouteExample")
public class RestRouteExample extends RouteBuilder {

	@Bean
	public XmlJsonDataFormat xmlToJson() {
		List<XmlJsonDataFormat.NamespacesPerElementMapping> namespaces = new ArrayList<XmlJsonDataFormat.NamespacesPerElementMapping>();
		namespaces.add(new XmlJsonDataFormat.NamespacesPerElementMapping("u", "http://www.pluralsight.com/orderfulfillment/Order"));
		XmlJsonDataFormat xmlJsonDataFormat = new XmlJsonDataFormat();
		xmlJsonDataFormat.setEncoding("UTF-8");
		xmlJsonDataFormat.setForceTopLevelObject(true);
		xmlJsonDataFormat.setTrimSpaces(true);
		xmlJsonDataFormat.setRootName("Order");
		xmlJsonDataFormat.setSkipNamespaces(true);
		xmlJsonDataFormat.setRemoveNamespacePrefixes(true);
		xmlJsonDataFormat.setExpandableProperties(Arrays.asList("d", "u"));
		xmlJsonDataFormat.setNamespaceMappings(namespaces);

		return xmlJsonDataFormat;
	}

	@Override
	public void configure() throws Exception {
		from("cxfrs://http://localhost:9090?resourceClasses=com.rest.service.OrderService&bindingStyle=Default").log("BEFORE (XML) = ${body}")
				.marshal("xmlToJson").log("BEFORE (JSON) = ${body}");
	}
}