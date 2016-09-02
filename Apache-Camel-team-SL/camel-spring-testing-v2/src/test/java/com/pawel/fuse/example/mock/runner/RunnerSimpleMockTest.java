package com.pawel.fuse.example.mock.runner;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.CamelTestContextBootstrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;

//Approach valid Apache Camel 2.10 onwards
@RunWith(CamelSpringJUnit4ClassRunner.class)
@BootstrapWith(CamelTestContextBootstrapper.class) //Spring 4.1.x onwards
@ContextConfiguration(locations = {"classpath:META-INF/spring/runner-mock-camel-context.xml"})
public class RunnerSimpleMockTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate template;

    @EndpointInject(uri = "mock:finish")
    MockEndpoint mockFinish;

    @EndpointInject(uri = "mock:first")
    MockEndpoint mockFirst;

    @EndpointInject(uri = "mock:second")
    MockEndpoint mockSecond;

    @Test
    @DirtiesContext
    //Note that we still use the @DirtiesContext annotation to ensure that the CamelContext, routes, and mock endpoints are reinitialized between test methods.
    public void testMockEndpoints() throws Exception {
        mockFinish.expectedMessageCount(3);
        mockFinish.expectedBodiesReceived("Funny World", "First Value", "Second Value");
        //or in any order
        //mockFinish.expectedBodiesReceivedInAnyOrder("First Value", "Second Value", "Funny World");

        mockFirst.expectedMessageCount(1);
        mockFirst.expectedBodiesReceived("First Value");

        mockSecond.expectedMessageCount(1);
        mockSecond.expectedBodiesReceived("Second Value");

        template.sendBody("direct:start", "Funny World");
        template.sendBodyAndHeader("direct:start", "Hello World", "myCommand", "firstCommand");
        template.sendBodyAndHeader("direct:start", "Hate World", "myCommand", "secondCommand");

        // now lets assert that the mock:xxx endpoint received yyy messages
        //mockFirst.assertIsSatisfied();
        //mockFinish.assertIsSatisfied();

        //or use this for all mock endpoints
        MockEndpoint.assertIsSatisfied(context);
    }
}
