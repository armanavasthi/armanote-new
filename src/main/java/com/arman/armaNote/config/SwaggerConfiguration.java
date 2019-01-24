package com.arman.armaNote.config;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.common.collect.Lists;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

// A general swagger config class needs to have only a docket as can be seen in : http://www.springboottutorial.com/spring-boot-swagger-documentation-for-rest-services
/*
 *	But My problem was that I have jwt security enabled, so swagger was unable to hit apis.
 *	To Solve this issue, I had to do 2 things: 
 *	1. in Security Config class I had to add several ant matchers so that I can atleast access 
 *			the swagger ui etc. (as per https://github.com/koldaman/springboot-jwt-swagger/blob/master/src/main/java/cz/e23/config/WebSecurityConfig.java)
 * 	2. Changed whole code of this class as per this link: https://stackoverflow.com/a/52859954/7456022
 *  (only slight change in the way of calling securityContexts() function as I was getting error 
 *  		while calling it in Lists.newArrayList(...), also lot of unnecessary code I 
 *  		commented in new Docket() area)
 * 	
 * 	But then the problem was that we are handling authorization using cookies but swagger was sending it using header.
 *  To address that issue, I did slight addition in JwtAuthenticationFilter.java to get token from header too.
 *  
 *  
 *  Note: If we want that only in case of swagger, header authorization should be allowed but not in general, 
 *  then we can change (didn't try, please try once) AUTHORIZATION_HEADER in this file (Say "SwagAuth")
 *  and then in JwtAuthenticationFilter we can try to accept that only
 */

@Configuration
@EnableSwagger2
@Import(springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {
	public static final String AUTHORIZATION_HEADER = "SwaggerAuthorization";
	public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";

	@Bean
	public Docket swaggerSpringfoxDocket() {

		Contact contact = new Contact("Matyas Albert-Nagy", "https://justrocket.de", "matyas@justrocket.de");

		List<VendorExtension> vext = new ArrayList<>();
		ApiInfo apiInfo = new ApiInfo("Backend API", "Armanote is to read notes in browser", "0.0.1",
				"http://arman.avasthi.com/termsandcondition", contact, "MIT", "http://armanote.com", vext);

		List<SecurityContext> securityContexts = new ArrayList<>();
		securityContexts.add(securityContext());
		
		/*
		 * This way was for normal configuration i.e. if there is no security in project
		   
		   return new Docket(DocumentationType.SWAGGER_2)
		        .apiInfo(DEFAULT_API_INFO)
		        .produces(DEFAULT_PRODUCES_AND_CONSUMES)
		        .consumes(DEFAULT_PRODUCES_AND_CONSUMES);

		*/

		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).pathMapping("/")
				.apiInfo(ApiInfo.DEFAULT)
				//.forCodeGeneration(true).genericModelSubstitutes(ResponseEntity.class)
				//.ignoredParameterTypes(Pageable.class).ignoredParameterTypes(java.sql.Date.class)
				//.directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
				//.directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
				//.directModelSubstitute(java.time.LocalDateTime.class, Date.class)
				.securityContexts(securityContexts)
				.securitySchemes(Lists.newArrayList(apiKey())).useDefaultResponseMessages(false);

		docket = docket.select().paths(regex(DEFAULT_INCLUDE_PATTERN)).build();

		return docket;
	}

	private ApiKey apiKey() {
		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN)).build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
	}

}
