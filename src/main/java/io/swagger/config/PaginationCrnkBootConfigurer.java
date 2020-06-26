package io.swagger.config;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.queryspec.pagingspec.NumberSizePagingBehavior;
import io.crnk.operations.server.OperationsModule;
import io.crnk.operations.server.TransactionOperationFilter;
import io.crnk.spring.setup.boot.core.CrnkBootConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaginationCrnkBootConfigurer implements CrnkBootConfigurer {
    public void configure(CrnkBoot crnkBoot){
        crnkBoot.addModule(NumberSizePagingBehavior.createModule());

        OperationsModule operationsModule = OperationsModule.create();
        operationsModule.addFilter( new TransactionOperationFilter() );
        crnkBoot.addModule(operationsModule);
    }
}
