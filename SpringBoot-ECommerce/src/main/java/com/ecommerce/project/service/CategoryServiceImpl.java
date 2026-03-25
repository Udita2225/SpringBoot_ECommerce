package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{
//    private Long id = Long.valueOf(1);
//    private Long nextId = 1L;
    private List<Category> categories =  new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = (sortOrder.equalsIgnoreCase("asc"))
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

//        Pageable pageDetails = PageRequest.of(pageNumber, pageSize);
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category>  categoryPage =  categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();
//        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new APIException("No category created till now!");
        }

        List<CategoryDTO> categoryDTOS =  categories.stream()
                .map(category-> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
//      Pagination meta-data
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
//    category.setCategoryId(nextId++);
    Category category =  modelMapper.map(categoryDTO, Category.class);
    Category categoryFromDb =  categoryRepository.findByCategoryName(category.getCategoryName());
    if(categoryFromDb != null){
        throw new APIException("Category with the name "+ category.getCategoryName() + " already exists !!!");
    }
    Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
//        categories.remove(categoryId-1);

//        ***** Using Streams ******

//        Category category =  categories.stream()
//                .filter((c)-> c.getCategoryId().equals(categoryId))
//                .findFirst().get();
//        It the object with categoryId is not found in the ArrayList then also we need to handle that case

//          ************************        //

//        Category category =  categories.stream()
//                                     .filter((c)->c.getCategoryId().equals(categoryId))
//                                     .findFirst().orElse(null);
//        if(category!=null) {
//            categories.remove(category);
//            return "Category with categoryId " + categoryId + " is Deleted Successfully!";
//        }
//        else return "Category with categoryId "+ categoryId + " is not found";


//       Using Exceptions
//         Category category = categories.stream()
//                 .filter((c)->c.getCategoryId().equals(categoryId))
//                 .findFirst()
//                 .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
//         categories.remove(category);
//         return "Category with category id "+ categoryId + " has been deleted Successfully !";

           // Using userRepository
//           List<Category> categories = categoryRepository.findAll();
//           Category category = categories.stream()
//                   .filter(c -> c.getCategoryId().equals(categoryId))
//                   .findFirst()
//                   .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
//           categoryRepository.delete(category);
//           return "Category with categoryId: " + categoryId + " deleted Successfully";

            // Doing Optimizations in the Code
//            Category category = categoryRepository.findById(categoryId)
//                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found"));
//            categoryRepository.delete(category);
//            return "Category with categoryId: " + categoryId + " deleted successfully";

            // Making use of Custom Exception
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));
            categoryRepository.delete(category);
//            return "Category with categoryId: "+ categoryId + " deleted successfully";
            return modelMapper.map(category, CategoryDTO.class);
    }


    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
//        Optional<Category> optionalCategory =  categories.stream()
//                .filter((c)-> c.getCategoryId().equals(categoryId))
//                .findFirst();
//        if(optionalCategory.isPresent()){
//            Category existingCategory = optionalCategory.get();
//            existingCategory.setCategoryName(category.getCategoryName());
//            return existingCategory;
//        }
//        else{
//         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found");
//        }

         // Using userRepository to update the data
        /*
          List<Category> categories =   categoryRepository.findAll();
          Optional<Category> optionalCategory = categories.stream()
                  .filter((c)->c.getCategoryId().equals(categoryId))
                  .findFirst();
          if(optionalCategory.isPresent()){
              Category existingCategory = optionalCategory.get();
              existingCategory.setCategoryName(category.getCategoryName());
              Category savedCategory =  categoryRepository.save(existingCategory);
              return existingCategory;
          }
          else{
              throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Available");
          }
         */

        // Optimizations in the Code
//        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);
//        Category savedCategory = savedCategoryOptional
//                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found!"));
//        savedCategory.setCategoryName(category.getCategoryName());
//        categoryRepository.save(savedCategory);
//        return savedCategory;

        // Using Custom Exceptions
        Category category =  modelMapper.map(categoryDTO, Category.class);
        Category savedCategory =  categoryRepository.findById(categoryId).
                orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        Category sameName =  categoryRepository.findByCategoryName(category.getCategoryName());
        if(sameName != null){
            throw new APIException("Category with name "+ sameName.getCategoryName() + " already exists !!");
        }
        savedCategory.setCategoryName(category.getCategoryName());
        Category c = categoryRepository.save(savedCategory);
        CategoryDTO updatedCategoryDTO =  modelMapper.map(c, CategoryDTO.class);
        return updatedCategoryDTO;
    }
}
