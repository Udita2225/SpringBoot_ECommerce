package com.ecommerce.project.service;

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
    private Long nextId = 1L;
    private List<Category> categories =  new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
    category.setCategoryId(nextId++);
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

           List<Category> categories = categoryRepository.findAll();
           Category category = categories.stream()
                   .filter(c -> c.getCategoryId().equals(categoryId))
                   .findFirst()
                   .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
           categoryRepository.delete(category);
           return "Category with categoryId: " + categoryId + " deleted Successfully";
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
    }
}
