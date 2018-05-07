package ch.bakito.crowd;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import ch.bakito.crowd.constants.ImageType;
import ch.bakito.crowd.controller.CrowdTreeController;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class CrowdTreesApplication {

  private static final ResponseMessage ANY_SERVER_ERROR = new ResponseMessageBuilder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Any server error").build();

  @Value("#{environment.CROWD_URL}")
  private String crowdUrl;
  @Value("#{environment.CROWD_APPLICATION_USER}")
  private String crowdUser;
  @Value("#{environment.CROWD_APPLICATION_PASSWORD}")
  private String crowdPassword;

  @Bean
  public ClientPropertiesImpl clientProperties() {
    Properties properties = new Properties();
    properties.setProperty("crowd.base.url", crowdUrl);
    properties.setProperty("application.name", crowdUser);
    properties.setProperty("application.password", crowdPassword);
    return ClientPropertiesImpl.newInstanceFromProperties(properties);
  }

  @Bean
  public Docket api() {
    //@formatter:off
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(basePackage(CrowdTreeController.class.getPackage().getName())).build()
        .apiInfo(new ApiInfoBuilder().title("PASS SCS REST API").build())
        .produces(stream(ImageType.values()).map(ImageType::getMineType).collect(toSet()))
        .globalResponseMessage(RequestMethod.GET,
            asList(ANY_SERVER_ERROR,
            new ResponseMessageBuilder()
                .code(HttpStatus.NOT_FOUND.value())
                .message("the object with the provided id could not be found")
                .build())
        )
        .globalResponseMessage(RequestMethod.POST,
            singletonList(ANY_SERVER_ERROR)
        );
    //@formatter:on
  }

  @Bean
  UiConfiguration uiConfig() {
    return new UiConfiguration(null, "list", "alpha", "schema", UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, null);
  }

  public static void main(String[] args) {
    SpringApplication.run(CrowdTreesApplication.class, args);
  }
}
