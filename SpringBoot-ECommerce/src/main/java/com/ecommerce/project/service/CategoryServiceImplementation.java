package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
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
    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
    category.setCategoryId(nextId++);
    categories.add(category);
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
         Category category = categories.stream()
                 .filter((c)->c.getCategoryId().equals(categoryId))
                 .findFirst()
                 .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource Not Found"));
         categories.remove(category);
         return "Category with category id "+ categoryId + " has been deleted Successfully !";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> optionalCategory =  categories.stream()
                .filter((c)-> c.getCategoryId().equals(categoryId))
                .findFirst();
        if(optionalCategory.isPresent()){
            Category existingCategory = optionalCategory.get();
            existingCategory.setCategoryName(category.getCategoryName());
            return existingCategory;
        }
        else{
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found");
        }
    }
}
