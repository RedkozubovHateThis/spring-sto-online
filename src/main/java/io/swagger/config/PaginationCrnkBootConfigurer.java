package io.swagger.config;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.queryspec.pagingspec.NumberSizePagingBehavior;
import io.crnk.spring.setup.boot.core.CrnkBootConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaginationCrnkBootConfigurer implements CrnkBootConfigurer {
    public void configure(CrnkBoot crnkBoot){
        crnkBoot.addModule(NumberSizePagingBehavior.createModule());
    }
}
