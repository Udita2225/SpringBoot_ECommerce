package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
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

    @GetMapping("/public/categories")
//    @RequestMapping(value="/public/categories", method = RequestMethod.GET)
    public ResponseEntity<List<Category>> getAllCategories(){
//        return categoryService.getAllCategories();
        List<Category> categoryList = categoryService.getAllCategories();
         return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }

//    @PostMapping("api/public/categories")
    @RequestMapping(value="/public/categories" ,method= RequestMethod.POST)
    public ResponseEntity<String> createCategory(@RequestBody Category category){
        categoryService.createCategory(category);
//        return "Category Added Successfully !";
        return new ResponseEntity<>("Category Added Successfully", HttpStatus.CREATED);


    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
//        String status = categoryService.deleteCategory(categoryId);
//        return new ResponseEntity<>(status, HttpStatus.OK);
        try {
            String status = categoryService.deleteCategory(categoryId);
//            return new ResponseEntity<>(status, HttpStatus.OK);
//            return ResponseEntity.ok(status);
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(status);
        }catch(ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category,
                                                 @PathVariable Long categoryId){
    try{
        Category savedCategory =  categoryService.updateCategory(category, categoryId);
        return new ResponseEntity<>("Category with category id: " + categoryId+ " has been updated!", HttpStatus.OK);
    }catch(ResponseStatusException e){
        return new ResponseEntity(e.getReason(), e.getStatusCode());
        }
    }

}
