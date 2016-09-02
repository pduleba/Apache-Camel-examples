package com.rest.transformer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.rest.domain.OrderRequest;
import com.rest.generated.Order;
import com.rest.generated.OrderItemType;
import com.rest.generated.OrderType;

/**
 * Processor for the fulfillment center one restful web service route. Accepts
 * the order XML from the exchange, converts it to JSON format and then returns
 * the JSON as a string.
 *
 * @author Michael Hoffman, Pluralsight
 *
 */
@Component
public class FulfillmentCenterOneProcessor {

	private static final Logger log = LoggerFactory.getLogger(FulfillmentCenterOneProcessor.class);

	/**
	 * Accepts the order XML from the route exchange's inbound message body and
	 * then converts it to JSON format.
	 *
	 * @param orderXml
	 * @return
	 */
	public String transformToOrderRequestMessage(Order orderXml) {
		String output = null;
		try {
			if (orderXml == null) {
				throw new Exception("Order xml was not bound to the method via integration framework.");
			}

			output = processCreateOrderRequestMessage(orderXml);
		} catch (Exception e) {
			log.error("Fulfillment center one message translation failed: " + e.getMessage(), e);
		}
		return output;
	}

	protected String processCreateOrderRequestMessage(com.rest.generated.Order orderFromXml) throws Exception {
		// 2 - Build an Order Request object and return the JSON-ified object
		return new Gson().toJson(buildOrderRequestType(orderFromXml));
	}

	protected OrderRequest buildOrderRequestType(com.rest.generated.Order orderFromXml) {
		OrderType orderTypeFromXml = orderFromXml.getOrderType();

		// 1 - Build order item types
		List<OrderItemType> orderItemTypesFromXml = orderTypeFromXml.getOrderItems();
		List<com.rest.domain.OrderItem> orderItems = new ArrayList<com.rest.domain.OrderItem>();
		for (OrderItemType orderItemTypeFromXml : orderItemTypesFromXml) {
			orderItems.add(new com.rest.domain.OrderItem(orderItemTypeFromXml.getItemNumber(), orderItemTypeFromXml.getPrice(), orderItemTypeFromXml
					.getQuantity()));
		}

		// 2 - Build order
		List<com.rest.domain.Order> orders = new ArrayList<com.rest.domain.Order>();
		com.rest.domain.Order order = new com.rest.domain.Order();
		order.setFirstName(orderTypeFromXml.getFirstName());
		order.setLastName(orderTypeFromXml.getLastName());
		order.setEmail(orderTypeFromXml.getEmail());
		order.setOrderNumber(orderTypeFromXml.getOrderNumber());
		order.setTimeOrderPlaced(orderTypeFromXml.getTimeOrderPlaced().toGregorianCalendar().getTime());
		order.setOrderItems(orderItems);
		orders.add(order);

		// 3 - Build order request
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.setOrders(orders);

		// 4 - Return the order request
		return orderRequest;
	}

}
