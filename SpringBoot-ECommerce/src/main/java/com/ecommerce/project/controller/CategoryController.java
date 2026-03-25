package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
//    public CategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }

    @GetMapping("/echo")
//    public ResponseEntity<String> echoMessage(@RequestParam(name = "message", defaultValue = "Hi CEO Udita, You are going to win!") String message){
    public ResponseEntity<String> echoMessage(@RequestParam (name="message", required=false) String message){
        return new ResponseEntity<>("Echoed Message: "+ message, HttpStatus.OK);
    }

    @GetMapping("/public/categories")
//    @RequestMapping(value="/public/categories", method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required=false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required=false) String sortBy,
            @RequestParam(name= "sortOrder", defaultValue = AppConstants.SORT_DIR, required=false) String sortOrder
    ){
//        return categoryService.getAllCategories();
            CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
         return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
//    @RequestMapping(value="/public/categories" ,method= RequestMethod.POST)
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
//        return "Category Added Successfully !";
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);

    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
//        String status = categoryService.deleteCategory(categoryId);
//        return new ResponseEntity<>(status, HttpStatus.OK);

        //**

//        try {
//            String status = categoryService.deleteCategory(categoryId);
////            return new ResponseEntity<>(status, HttpStatus.OK);
////            return ResponseEntity.ok(status);
//            return ResponseEntity.status(HttpStatus.OK)
//                                 .body(status);
//        }catch(ResponseStatusException e){
//            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
//        }

          CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId);
          return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);
//          return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId) {
//    try{
//        Category savedCategory =  categoryService.updateCategory(category, categoryId);
//        return new ResponseEntity<>("Category with category id: " + categoryId+ " has been updated!", HttpStatus.OK);
//    }catch(ResponseStatusException e){
//        return new ResponseEntity(e.getReason(), e.getStatusCode());
//        }
//    }
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
//        return new ResponseEntity<>("Category with category id: " + categoryId + " has been updated! ", HttpStatus.OK);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }
}
