package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.NoProductAvailableException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
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
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category", "categoryId", categoryId));
        Product product =  modelMapper.map(productDTO, Product.class);

        // This is not optimal. Since we have the categoryId we need to check only the products that are present in that particular Category
        // Not all the products.
//        String  productName = product.getProductName();
//        List<Product> pr = productRepository.findByProductNameLikeIgnoreCase(productName);
//        if(!pr.isEmpty())
//            throw new APIException("Product with same name is available");

        boolean isProductNotPresent =  true;
        List<Product> list =  category.getProducts();
        for(Product prod : list){
            if(prod.getProductName().equals(product.getProductName())){
                isProductNotPresent = false;
                break;
            }
        }

        if(isProductNotPresent) {
            product.setCategory(category);
            double specialPrice =
                    product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            product.setImage("default.png");
            Product savedProduct = productRepository.save(product);

            return modelMapper.map(savedProduct, ProductDTO.class);
        }
        else{
            throw new APIException("Product Already exist !");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){

        Sort sortByAndOrder = (sortOrder.equalsIgnoreCase("asc")) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        // If product size is 0 throw an Exception
        List<Product> products = productPage.getContent();
//        if(products.isEmpty()){
//            throw new NoProductAvailableException("No Product Available in the Database!");
//        }
        if(products.isEmpty()){
            throw new APIException("No Products Exist!");
        }

        List<ProductDTO> productDTOs = productPage.stream()
                .map((product)-> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse =  new ProductResponse();
        productResponse.setContent(productDTOs);
        // Set Pagination meta-data
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category =  categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));

    // If the  product repository size is 0 , throw an Exception
    Sort sortByAndSortOrder =  sortOrder.equalsIgnoreCase("asc") ?
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

    Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndSortOrder);
    Page<Product> pageProducts =  productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

    List<Product> productsList = pageProducts.getContent();
//    if(productsList.isEmpty()){
//        throw new NoProductAvailableException("No Product Available in the Database");
//    }
        if(productsList.isEmpty()) throw new APIException(category.getCategoryName() + " category does not have any products ");

//    List<Product> products =  productRepository.findByCategoryOrderByPriceAsc(category);
    List<ProductDTO> productDTOS = productsList.stream()
            .map((product)->modelMapper.map(product, ProductDTO.class))
            .toList();
    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOS);
    productResponse.setTotalPages(pageProducts.getTotalPages());
    productResponse.setPageNumber(pageProducts.getNumber());
    productResponse.setPageSize(pageProducts.getSize());
    productResponse.setTotalElements(pageProducts.getTotalElements());
    productResponse.setLastPage(pageProducts.isLast());
    return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndSortOrder =  sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndSortOrder);
        Page<Product> pageProducts =  productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%' ,pageDetails);
        List<Product> productsList = pageProducts.getContent();

//      If the product size is 0 throw an exception
        if(productsList.isEmpty()) throw new APIException("Products not found with keyword: "+ keyword);


//      tra -> %tra% Any String which has tra in it will be returned
//      List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' +keyword+ '%');
        List<ProductDTO> productDTOS =  productsList.stream()
                .map((product) -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setLastPage(pageProducts.isLast());
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

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException{
         //    Getting the product from DB
      Product productFromDb = productRepository.findById(productId)
              .orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));
        //    Upload image to server
        //    Get the file name of the uploaded image
//       String path = "images/";  Tomorrow if we want to change the location of the image being stored..that's why we have added this property in application.properties
       String fileName = fileService.uploadImage(path, image);

        //    Updating the new file name to the product
        productFromDb.setImage(fileName);

        //    Save updated product
        Product updatedProduct = productRepository.save(productFromDb);
        //    return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

}
