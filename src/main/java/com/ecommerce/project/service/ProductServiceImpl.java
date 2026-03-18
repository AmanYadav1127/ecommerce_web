package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    CartService cartService;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Value("${project.image")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category", "CategoryId", categoryId));
        boolean ifProductNotPresent=true;
        List<Product>products=category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equalsIgnoreCase(productDTO.getProductName())) {
                ifProductNotPresent=false;
                break;
            }
        }
        if (ifProductNotPresent)
        {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else
        {
            throw new APIException("Product already exists !!!");
        }

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product>pageProducts=productRepository.findAll(pageDetails);
        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList(); //ProductDTO me change isliye kyuki ProductResponse me DTO return ho rha
//        if (products.isEmpty())
//        {
//            throw new APIException("No products found !!!");
//        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);//ProductResponse ke andr list ka naam content haii isliye
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category", "CategoryId", categoryId));
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product>pageProducts=productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        if(products.size()==0){
            throw new APIException(category.getCategoryName()+" category does not have any products !!!");
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword( String keyword, Integer pageNumber, Integer pageSize, String sortBy,String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product>pageProducts=productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',pageDetails);
        List<Product> products = pageProducts.getContent();
//        List<Product> filteredProducts=products.stream().filter(product -> product.getProductName().toLowerCase().contains(keyword.toLowerCase()))
//                .toList();  //ye use tb krte jab findbyall krte..ab findByProductName...ye wale
//                naam se hi repository me database ko filter kr dega..its
//                jpa magic..query will be generated automaticallly
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        if(products.size()==0){
            throw new APIException("No products found with keyword " + keyword + " !!!");
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(@Valid ProductDTO productDTO, Long productId) {
        //Get the existing product from the database
        Product productFromDb = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "Product Id", productId));
        //Update the product details
        Product product = modelMapper.map(productDTO, Product.class);
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        productFromDb.setSpecialPrice(specialPrice);
        //Save the updated product back to the database
        Product updatedProduct = productRepository.save(productFromDb);

        List<Cart> carts=cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs= carts.stream().map(cart ->{CartDTO cartDTO= modelMapper.map(cart, CartDTO.class);
                                                List<ProductDTO> products=cart.getCartItems().stream().
                                                        map(p -> modelMapper.map(p.getProduct(),ProductDTO.class)).
                                                        toList();
        cartDTO.setProducts(products);
        return cartDTO;
                }).toList();

        cartDTOs.forEach(cart -> cartService.updateProductInCart(cart.getCartId(),productId));


        //Convert the updated product to ProductDTO and return
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDb = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "Product Id", productId));
        List<Cart>carts=cartRepository.findCartsByProductId(productId);
        //database me se product dlt karne se phle us cart se me dlt krdo
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
        productRepository.delete(productFromDb);
        return modelMapper.map(productFromDb, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get the existing product from the database
        Product productFromDb = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "Product Id", productId));
        //Upload the image to the server and get the image URL
        //Get the file name of the uploaded image
//        String path = "images/"; is line ki jrurat nhi beacuse ham isko app.properties se control krrhe
        String fileName = fileService.uploadImage(path,image);
        //updating the new file name to the product
        productFromDb.setImage(fileName);
        //Save the updated product back to the database
        Product updatedProduct = productRepository.save(productFromDb);
        //return dto after mapping product to dto
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}