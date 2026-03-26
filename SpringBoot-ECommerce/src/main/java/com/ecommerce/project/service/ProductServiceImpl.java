package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category", "categoryId", categoryId));
        Product product =  modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        double  specialPrice =
                product.getPrice() - ((product.getDiscount()*0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        product.setImage("default.png");
        Product savedProduct =  productRepository.save(product);


        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream()
                .map((product)-> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse =  new ProductResponse();
        productResponse.setContent(productDTOs);
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
    Category category =  categoryRepository.findById(categoryId)
            .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));

    List<Product> products =  productRepository.findByCategoryOrderByPriceAsc(category);
    List<ProductDTO> productDTOS = products.stream()
            .map((product)->modelMapper.map(product, ProductDTO.class))
            .toList();
    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {
//      tra -> %tra% Any String which has tra in it will be returned
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' +keyword+ '%');
        List<ProductDTO> productDTOS =  products.stream()
                .map((product) -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product =  modelMapper.map(productDTO, Product.class);

        //Getting the existing product from DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //Update the product info with the one in Request Body
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        // Save to Database
        Product savedProduct =  productRepository.save(productFromDb);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

}
