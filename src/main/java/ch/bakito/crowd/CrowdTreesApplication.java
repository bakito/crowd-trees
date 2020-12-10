package ch.bakito.crowd;

import ch.bakito.crowd.constants.ImageType;
import ch.bakito.crowd.controller.CrowdTreeController;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Properties;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

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
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .showCommonExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CrowdTreesApplication.class, args);
    }
}
