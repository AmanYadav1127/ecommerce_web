package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, Product product) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category","CategoryId",categoryId));
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct=productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
       List<Product> products= productRepository.findAll();
       List<ProductDTO> productDTOS=products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
               .toList(); //ProductDTO me change isliye kyuki ProductResponse me DTO return ho rha
         ProductResponse productResponse=new ProductResponse();
            productResponse.setContent(productDTOS);//ProductResponse ke andr list ka naam content haii isliye
            return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category","CategoryId",categoryId));
        List<Product> products=productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS=products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword) {
        List<Product> products=productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');
//        List<Product> filteredProducts=products.stream().filter(product -> product.getProductName().toLowerCase().contains(keyword.toLowerCase()))
//                .toList();  //ye use tb krte jab findbyall krte..ab findByProductName...ye wale
//                naam se hi repository me database ko filter kr dega..its
//                jpa magic..query will be generated automaticallly
        List<ProductDTO> productDTOS=products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Product product, Long productId) {
        //Get the existing product from the database
        Product productFromDb=productRepository.findById(productId).orElseThrow(()->
                new ResourceNotFoundException("Product","Product Id",productId));
        //Update the product details
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
        productFromDb.setSpecialPrice(specialPrice);
        //Save the updated product back to the database
        Product updatedProduct=productRepository.save(productFromDb);
        //Convert the updated product to ProductDTO and return
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDb=productRepository.findById(productId).orElseThrow(()->
                new ResourceNotFoundException("Product","Product Id",productId));
        productRepository.delete(productFromDb);
        return modelMapper.map(productFromDb, ProductDTO.class);
    }
}
