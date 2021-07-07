package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				from("file:pedidos?delay=5s&noop=true")
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
			}

		});
		
		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}
