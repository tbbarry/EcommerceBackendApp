package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

 private final ProductRepository productRepository;

 public ProductService(ProductRepository productRepository) {
  this.productRepository = productRepository;
 }

 public Product create(Product product){
  return productRepository.save(product);
 }

 public List<Product> getAll(){
  return productRepository.findAll();
 }

}