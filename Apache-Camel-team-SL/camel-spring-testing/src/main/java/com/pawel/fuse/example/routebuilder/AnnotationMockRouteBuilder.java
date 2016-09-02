package com.pawel.fuse.example.routebuilder;

import com.pawel.fuse.example.bean.HelloBean;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by pmackiewicz on 2016-08-26.
 */
public class AnnotationMockRouteBuilder extends SpringRouteBuilder {

    @Autowired
    private HelloBean hello;

    @Override
    public void configure() throws Exception {
        from("direct:hello")
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    // extract the name parameter from the Camel message which we want to use
                    // when invoking the bean
                    String name = exchange.getIn().getBody(String.class);

                    // invoke the bean which should have been injected by Spring
                    String answer = hello.hello(name);

                    // store the reply from the bean on the OUT message
                    exchange.getOut().setBody(answer);
                }
            });

    }
}
