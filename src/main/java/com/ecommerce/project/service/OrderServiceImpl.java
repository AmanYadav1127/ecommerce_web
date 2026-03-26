package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService{
    @Autowired
    CartRepository cartRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartService cartService;
    @Autowired
    ModelMapper modelMapper;
    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        //Getting user cart
        Cart cart=cartRepository.findCartByEmail(emailId);
        if(cart==null)
        {
            throw new ResourceNotFoundException("Cart","email",emailId);
        }
        Address address=addressRepository.findById(addressId).orElseThrow(()->
                new ResourceNotFoundException("Address","addressId",addressId));

        // Create a new order with payment info
        Order order=new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setAddress(address);
        order.setOrderStatus("Order CONFIRMED");
        Payment payment=new Payment(paymentMethod,pgPaymentId,pgStatus,pgResponseMessage,pgName);
        payment.setOrder(order);
        payment=paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder=orderRepository.save(order);

        //get item from the cart into the order item

        List<CartItem>cartItems=cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIException("Cart is empty");
        }
        List<OrderItem>orderItems=new ArrayList<>();
        for(CartItem cartItem:cartItems)
        {
            OrderItem orderItem=new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);
        //update the stock of the product

        cart.getCartItems().forEach(item -> {
            int quantity=item.getQuantity();
            Product product=item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);
            //clear the cart
            cartService.deleteProductFromCart(cart.getCartId(),item.getProduct().getProductId());
        });

        //send back the order summary
        OrderDTO orderDTO=modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item->
                orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));
        orderDTO.setOrderId(addressId);
        return orderDTO;
    }
}
