package pl.kwojcik.project_lab.order;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kwojcik.project_lab.gen.api.dto.NewOrderDTO;
import pl.kwojcik.project_lab.gen.api.dto.OrderDTO;
import pl.kwojcik.project_lab.gen.api.dto.OrderPositionDTO;
import pl.kwojcik.project_lab.order.model.OrderEntity;
import pl.kwojcik.project_lab.order.model.OrderPositionEntity;
import pl.kwojcik.project_lab.products.ProductEntity;
import pl.kwojcik.project_lab.user.UserRepository;
import pl.kwojcik.project_lab.user.model.AppPermission;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

// #Zadanie_1__9
//start L1 Facade
@Transactional
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderPositionRepository orderPositionRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderPositionRepository orderPositionRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderPositionRepository = orderPositionRepository;
        this.userRepository = userRepository;
    }


    //start L4 aspectJ
    @Secured(AppPermission.ROLE_ORDER_MODIFY)
    public OrderDTO createOrder(NewOrderDTO newOrderDTO, Authentication auth) {
        var customer = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        var order = new OrderEntity();
        var orderPositions = newOrderDTO.getOrderPositions()
                .stream()
                .map(op -> mapToOrderPosition(op, order))
                .collect(Collectors.toCollection(HashSet::new));
        order.setOrderPositions(orderPositions);
        order.setCustomer(customer);

        var createdOrder = orderRepository.save(order);
        return mapToOrderDTO(createdOrder);
    }

    @Transactional //start L4 aspectJ
    @Secured(AppPermission.ROLE_ORDER_MODIFY)
    public void deleteOrder(long orderId) {
        this.orderRepository.deleteById(orderId);
    }

    @Secured(AppPermission.ROLE_ORDER_VIEW)
    @Cacheable("clientOrders") //start L4 aspectJ
    public List<OrderDTO> getClientOrders(Authentication auth) {
        var customer = userRepository.findByUsername(auth.getName())
                .orElseThrow();
        var orders = orderRepository.findAllByCustomerId(customer.getId());
        return orders
                .stream()
                .map(this::mapToOrderDTO)
                .toList();
    }

    private OrderPositionEntity mapToOrderPosition(OrderPositionDTO orderPositionDTO, OrderEntity orderRef) {
        return new OrderPositionEntity(
                orderPositionDTO.getAmount(),
                new ProductEntity(orderPositionDTO.getProductId()),
                orderRef
        );
    }

    private OrderDTO mapToOrderDTO(OrderEntity entity) {
        var dto = new OrderDTO()
                .id(entity.getId())
                .creationDate(entity.getCreationDate())
                .orderPositions(entity.getOrderPositions()
                        //start L4 stream collection processing
                        .stream()
                        .map(op -> new OrderPositionDTO()
                                .amount(op.getProductAmount())
                                .productId(op.getProduct().getId())
                        )
                        .toList()
                )
                .customerId(entity.getCustomer().getId());
        return dto;
    }

}
