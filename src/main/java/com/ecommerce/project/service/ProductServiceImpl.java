package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
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
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList(); //ProductDTO me change isliye kyuki ProductResponse me DTO return ho rha
//        if (products.isEmpty())
//        {
//            throw new APIException("No products found !!!");
//        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);//ProductResponse ke andr list ka naam content haii isliye
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException("Category", "CategoryId", categoryId));
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
//        List<Product> filteredProducts=products.stream().filter(product -> product.getProductName().toLowerCase().contains(keyword.toLowerCase()))
//                .toList();  //ye use tb krte jab findbyall krte..ab findByProductName...ye wale
//                naam se hi repository me database ko filter kr dega..its
//                jpa magic..query will be generated automaticallly
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
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
        //Convert the updated product to ProductDTO and return
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDb = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException("Product", "Product Id", productId));
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