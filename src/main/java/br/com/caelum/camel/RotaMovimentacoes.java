package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaMovimentacoes {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				
				from("file:movimentacoes?delay=5s&noop=true")
				.log("Antes de transformar")
			    .to("xslt:movimentacoes-para-html.xslt")
			    .setHeader(Exchange.CONTENT_TYPE, constant("text/html"))
			    .log("${body}")
			    .to("mock:saida");
				
			}

		});

		context.start();
		Thread.sleep(3000);
		context.stop();
	}
}