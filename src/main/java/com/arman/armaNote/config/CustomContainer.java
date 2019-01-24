package com.arman.armaNote.config;

import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Reason to add this class: 
// i got error while setting cookie to response in AuthenticationController
// java.lang.IllegalArgumentException: An invalid character [32] was present in the Cookie value
// this happened bcz by default tomcat8.5+ uses Rfc6265CookieProcessor to validate cookie value. This Rfc standard invalidates cookie if some
// comma or special chars are there. So here I am telling tomcat to use LegacyCookieProcessor instead of Rfc.
// Also important to note is that we must need to setContext path first otherwise spring will give error as no context set (as its using
// context in bean created below.)
// https://www.baeldung.com/embeddedservletcontainercustomizer-configurableembeddedservletcontainer-spring-boot
@Configuration
public class CustomContainer implements
  WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
  
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(6060);
        factory.setContextPath("");
     }
    
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
    	return (factory) -> factory.addContextCustomizers(
    			(context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
    }
}