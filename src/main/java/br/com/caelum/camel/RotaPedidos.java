package br.com.caelum.camel;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		
		context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
//				errorHandler(deadLetterChannel("file:error")
//				.redeliveryDelay(2000)
//				.logExhaustedMessageHistory(true)
//				.maximumRedeliveries(3)
//				.useOriginalMessage()
//				.onRedelivery(new Processor() {
//					
//					@Override
//					public void process(Exchange exchange) throws Exception {
//						// TODO Auto-generated method stub
//						int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
//						int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
//						System.out.println("Redelivery dessa bagassa " + counter + "/" + max );
//					}
//				}));
				
				//deve ser configurado antes de qualquer rota
				onException(Exception.class)
				    .handled(true)
				    .maximumRedeliveries(3)
				    .redeliveryDelay(4000)				    
				    .onRedelivery(new Processor() {

				            @Override
				            public void process(Exchange exchange) throws Exception {
				                    int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
				                    int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
				                    System.out.println("Redelivery - " + counter + "/" + max );;
				            }
				    })
//				    .to("file:error-parsing");
				    .to("activemq:queue:pedidos.DLQ");
				
//				from("file:pedidos?delay=5s&noop=true")
				from("activemq:queue:pedidos")
				.to("validator:pedido.xsd");
//				.multicast()
//				.parallelProcessing()
//				.to("direct:http")
//				.to("direct:soap");
				
				
				from("direct:http")
					.routeId("rota-pedidos")
	//				.log("${exchange.pattern}")
					.setProperty("pedidoId", xpath("/pedido/id/text()"))
				    .setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()"))
				    
					.split()
			        	.xpath("/pedido/itens/item")
			        .filter()
			        	.xpath("/item/formato[text()='EBOOK']")
			        	.setProperty("ebookId", xpath("/item/livro/codigo/text()"))		            
					.marshal().xmljson()
					.log("Teste Camel trabalhando !!! - ${id} - ${body}")
					.setHeader(Exchange.FILE_NAME, simple("${file:name.noext}-${header.CamelSplitIndex}.json"))
	//				.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)\
					.setHeader(Exchange.HTTP_QUERY, 
				            simple("clienteId=${property.clienteId}&pedidoId=${property.pedidoId}&ebookId=${property.ebookId}"))
	
					.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
					.to("http4://localhost:8080/webservices/ebook/item");
				
				from("direct:soap")
			    	.routeId("rota-soap")
			    	.to("xslt:pedido-para-soap.xslt")
			    	.log("Resultado do Template: ${body}")
			    	.setHeader(Exchange.CONTENT_TYPE, constant("text/xml"))
//			    	.to("mock:soap");
			    	.to("http4://localhost:8080/webservices/financeiro");
			}

		});
		
		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
