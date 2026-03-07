package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImplementation implements CategoryService{
//    private Long id = Long.valueOf(1);
//    private Long nextId = 1L;
    private List<Category> categories =  new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new APIException("No category created till now!");
        }
        return categories;
    }

    @Override
    public void createCategory(Category category) {
//    category.setCategoryId(nextId++);
    Category savedCategory =  categoryRepository.findByCategoryName(category.getCategoryName());
    if(savedCategory != null){
        throw new APIException("Category with the name "+ category.getCategoryName() + " already exists !!!");
    }
    categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
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
            return "Category with categoryId: "+ categoryId + " deleted successfully";
    }


    @Override
    public Category updateCategory(Category category, Long categoryId) {
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
        Category savedCategory =  categoryRepository.findById(categoryId).
                orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        Category sameName =  categoryRepository.findByCategoryName(category.getCategoryName());
        if(sameName != null){
            throw new APIException("Category with name "+ sameName.getCategoryName() + " already exists !!");
        }
        savedCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(savedCategory);
        return savedCategory;
    }
}
