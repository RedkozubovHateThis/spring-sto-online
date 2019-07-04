package io.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
@Configuration
public class SwaggerDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Merch")
            .description("<p><span style=\"font-size: 14pt;\"><strong>Это API для приложения автодиллер. </strong></span></p> <p><em><span style=\"font-size: 12pt;\">Дополнительные атрибуты:</span></em></p> <p><span style=\"font-size: 12pt;\">1) x-primary-key - массив идентификаторов первичного ключа:</span></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; Пример - [id] или [id1,id2], где</span></em></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; [id] обычный ключ,</span></em></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; [id1,id2]- составной ключ.</span></em></p> <p><span style=\"font-size: 12pt;\">2) x-unique-index - указание уникального индекса.</span></p> <p><span style=\"font-size: 12pt;\">&nbsp; <em>Пример - [id,title]</em>&nbsp;</span></p> <p><span style=\"font-size: 12pt;\">3) foreign-key - массив идентификаторов внешнего ключа:</span></p> <p><span style=\"font-size: 12pt;\">&nbsp; &nbsp;Пример - ref_instore_activity:[id]-&gt;[ref_instore_activity_id] on update cascade on delete cascade,где</span></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; &nbsp;ref_instore_activity - внешняя таблица </span></em></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; &nbsp;[id] - поле для связи для внешней таблицы </span></em></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; &nbsp;[ref_instore_activity_id] - поле в текущей таблицы </span></em></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; &nbsp;on update cascade on delete cascade - указывается действие при обновлении и удалении. </span></em></p> <p><span style=\"font-size: 12pt;\">4) Типа blob - type object format blob.</span></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; &nbsp;Пример:</span><span style=\"font-size: 12pt;\">&nbsp;</span></em></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; job :</span></em></p> <p><em><span style=\"font-size: 12pt;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;type: object <br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;format: blob</span></em></p> ")
            .license("")
            .licenseUrl("http://unlicense.org")
            .termsOfServiceUrl("")
            .version("1.0.1")
            .contact(new Contact("","", "maxden33@gmail.com"))
            .build();
    }

    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("io.swagger.api"))
                    .build()
                .directModelSubstitute(org.threeten.bp.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.threeten.bp.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

}
