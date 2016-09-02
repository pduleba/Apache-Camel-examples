package com.pgs.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by jpolitowicz on 25.08.2016.
 *
 * Strategy which copies headers specified in header "CopyHeaders" coma separated
 */
public class HeaderAggregationStrategy implements AggregationStrategy {

    private static final Logger log = LoggerFactory.getLogger(HeaderAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        if (oldExchange != null && newExchange != null) {
            String[] copyHeaders = StringUtils.split(oldExchange.getIn().getHeader("CopyHeaders", String.class), ",");

            log.info("Headers to copy {}", Arrays.toString(copyHeaders));

            for (String header : copyHeaders) {
                if (oldExchange.getIn().getHeaders().containsKey(header)) {
                    newExchange.getIn().getHeaders().put(header, oldExchange.getIn().getHeader(header));
                }
            }
        }

        return newExchange;
    }
}
