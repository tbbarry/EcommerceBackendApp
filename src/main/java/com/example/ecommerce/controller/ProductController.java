package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

 private final ProductService productService;

 public ProductController(ProductService productService) {
  this.productService = productService;
 }

 @PostMapping
 public Product create(@RequestBody Product product){
  return productService.create(product);
 }

 @GetMapping
 public List<Product> getAll(){
  return productService.getAll();
 }

}