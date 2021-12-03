package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaVelocite {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		CamelContext context = new DefaultCamelContext();


		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub

				from("direct:entrada")
					.setHeader("data", constant("8/12/2015"))
					.to("velocity:template.vm")
					.log("${body}")
					.to("mock:saida");
			}
		});
			
		context.start();
		
		ProducerTemplate producer = context.createProducerTemplate();
		producer.sendBody("direct:entrada", "Apache Camel rocks!!!");
		
		Thread.sleep(3000);
		context.stop();
	}
}
