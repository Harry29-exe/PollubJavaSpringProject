package pl.kwojcik.project_lab.products;

import pl.kwojcik.project_lab.gen.api.dto.ProductDTO;
import pl.kwojcik.project_lab.products.dao.ProductRepository;
import pl.kwojcik.project_lab.user.model.AppPermission;
import pl.kwojcik.project_lab.utils.CacheResultService;
import pl.kwojcik.project_lab.utils.PerformanceLogger;
import pl.kwojcik.project_lab.utils.PermissionCheckerService;

import java.util.List;

import static pl.kwojcik.project_lab.utils.PerformanceLogger.logExecutionTime;

// #Zadanie__1_5 Adapter
//start L1 Adapter
class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final PermissionCheckerService permissionCheckerService;
    private final CacheResultService<Long, ProductDTO> cacheResultService = new CacheResultService<>();

    public ProductServiceImpl(ProductRepository productRepository, PermissionCheckerService permissionCheckerService) {
        this.productRepository = productRepository;
        this.permissionCheckerService = permissionCheckerService;
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        //start L4 functional interface -- usage1&2
        return logExecutionTime(() -> permissionCheckerService.checkPermissionsAndExecute(
                AppPermission.PRODUCT_MODIFY,
                () -> {
                    var entity = mapProduct(productDTO);
                    var createdEntity = productRepository.save(entity);
                    return mapProduct(createdEntity);
                }));
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public ProductDTO getProduct(Long productId) {
        //start L4 functional interface -- usage1&2
        return cacheResultService.executeOrGetCached(productId, id -> {
            var entity = productRepository.findById(id).orElseThrow();
            return mapProduct(entity);
        });
    }


    public List<ProductDTO> getProducts() {
        var products = productRepository.findAll();

        return products.stream().map(this::mapProduct).toList();
    }


    public ProductDTO updateProduct(ProductDTO productDTO) {
        var entity = mapProduct(productDTO);
        var updatedEntity = productRepository.save(entity);
        return mapProduct(updatedEntity);
    }

    private ProductDTO mapProduct(ProductEntity productEntity) {
        return new ProductDTO()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice());
    }

    private ProductEntity mapProduct(ProductDTO dto) {
        var entity = new ProductEntity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        return entity;
    }
}
