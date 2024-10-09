package pl.kwojcik.project_lab.products;

import org.springframework.transaction.PlatformTransactionManager;
import pl.kwojcik.project_lab.products.dao.ProductRepositoryDB;
import pl.kwojcik.project_lab.utils.PermissionCheckerService;

// #Zadanie__1_1 factory
//start L1 Factory
public class ProductServiceFactory {
    private final ProductRepositoryDB productRepository;
    private final boolean useTransactionsInProductService;
    private final PlatformTransactionManager txMngr;
    private final PermissionCheckerService permissionCheckerService;

    public ProductServiceFactory(
            ProductRepositoryDB productRepository,
            PlatformTransactionManager txMngr,
            boolean useTransactionsInProductService, PermissionCheckerService permissionCheckerService
    ) {
        this.productRepository = productRepository;
        this.useTransactionsInProductService = useTransactionsInProductService;
        this.txMngr = txMngr;
        this.permissionCheckerService = permissionCheckerService;
    }

    public ProductService createProductService() {
        if (useTransactionsInProductService) {
            return new ProductServiceTxDecorator(
                    new ProductServiceImpl(
                            productRepository,
                            permissionCheckerService
                    ), txMngr);
        } else {
            return new ProductServiceImpl(productRepository, permissionCheckerService);
        }
    }
}
