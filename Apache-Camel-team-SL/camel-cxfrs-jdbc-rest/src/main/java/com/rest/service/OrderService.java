package com.rest.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rest.domain.OrderRequest;

/**
 * Main resource service provider for orders. Services are available through the
 * path /orderFulfillment.
 *
 * @author Michael Hoffman, Pluralsight
 *
 */
@Path("/orderFulfillment")
public class OrderService {

	/**
	 * Processing an order request. Simply writes out the order request to
	 * System.err and then returns a fulfillment response object with the status
	 * code 200 and status as success.
	 *
	 * @param orderRequest
	 * @return
	 */
	@POST
	@Path("/processOrders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String processOrders(OrderRequest orderRequest) {
		System.err.println(orderRequest);
		return "SUCCESS";
	}

	@POST
	@Path("/processXmlOrders")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
	public String processXmlOrders(String order) {
		System.err.println(order);
		return "SUCCESS";
	}

}
