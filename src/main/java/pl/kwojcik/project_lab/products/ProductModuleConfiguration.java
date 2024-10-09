package pl.kwojcik.project_lab.products;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import pl.kwojcik.project_lab.products.dao.ProductRepositoryDB;
import pl.kwojcik.project_lab.utils.PermissionCheckerService;

@Configuration
public class ProductModuleConfiguration {
    private final ProductServiceFactory productServiceFactory;


    public ProductModuleConfiguration(
            ProductRepositoryDB jpaProductRepository,
            PlatformTransactionManager txManger,
            PermissionCheckerService permissionCheckerService,
            @Value("${refactoring.variables.useTransactionsInProductService}") String useTransactionsInProductService
    ) {
        this.productServiceFactory = new ProductServiceFactory(jpaProductRepository,
                txManger,
                "true".equals(useTransactionsInProductService),
                permissionCheckerService
        );
    }

    @Bean
    public ProductService productService() {
        return productServiceFactory.createProductService();
    }
}
