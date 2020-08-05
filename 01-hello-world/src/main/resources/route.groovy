import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder

def addRoute(CamelContext camelContext){
    camelContext.addRoutes(new RouteBuilder() {
        @Override
        void configure() throws Exception {
            from("quartz://report?cron=* * * * * ?")
                    .setBody(constant("Hello World"))
                    .to("log:mainInfo");
        }
    })
}

addRoute(context)