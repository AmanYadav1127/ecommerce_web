package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    AuthUtil authUtil;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //find existing cart or create one
        Cart cart=createCart();
        //retrieve product details
        Product product=productRepository.findById(productId).orElseThrow(()->
                new ResourceNotFoundException("Product","ProductId",productId));
        //perform validation
        CartItem cartItem = cartItemRepository
                .findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if(cartItem!=null)
        {
            throw new APIException("Product "+ product.getProductName()+" already exists in the cart");
        }
        if(product.getQuantity()<quantity)
        {
            throw new APIException("Please,make an order of the"+product.getProductName()+
                    "is less than or equal to the quantity"+product.getQuantity());
        }
        //create cart item

        CartItem newCartItem=new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setProductPrice(product.getPrice());
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setQuantity(quantity);
        //save cart item
        cartItemRepository.save(newCartItem);
        cart.getCartItems().add(newCartItem);
        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
        cartRepository.save(cart);

        //return updated cart

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        List<CartItem>cartItems=cart.getCartItems();
        Stream<ProductDTO>productStream=cartItems.stream().map(item -> {ProductDTO map=modelMapper.map(item.getProduct(),ProductDTO.class);
        map.setQuantity(item.getQuantity());
        return map;
        });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();
        if(carts.size()==0){
            throw new APIException("No carts found !!");
        }
        List<CartDTO> cartDTOS=carts.stream().map(cart ->{ CartDTO cartDTO= modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products=cart.getCartItems().stream().
                map(p->modelMapper.map(p.getProduct(),ProductDTO.class)).toList();
        cartDTO.setProducts(products);
        return cartDTO;
        }).toList();
        return cartDTOS;
    }

    private Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }
        Cart cart=new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }
}
