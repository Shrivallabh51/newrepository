package com.example.demo.controller;



import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Category;
import com.example.demo.entities.CategoryDto;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductDto;
import com.example.demo.entities.UserDto;
import com.example.demo.entities.Users;
import com.example.demo.services.CategoryService;
import com.example.demo.services.ProductService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
      @Autowired
	 ProductService pser;
      CategoryService cser;
      
      //http://localhost:8090/getproducts
     
     @GetMapping("/getproducts")
     public List<ProductDto> getProducts(){
   	  return pser.getAllProductsWithImageUrls();
     }
     
     @GetMapping("api/products/image/{productId}")
     public ResponseEntity<byte[]> getProductImage(@PathVariable int productId) {
         byte[] imageBytes = pser.getImageBytes(productId);

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.IMAGE_JPEG); // Adjust content type as needed

         return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
     }
     
     @GetMapping("/getproduct/{id}")
     public ResponseEntity<ProductDto> getProductById(@PathVariable int id) {
         try {
             ProductDto productDto = pser.getProductById(id);
             return ResponseEntity.ok(productDto);
         } catch (EntityNotFoundException e) {
             return ResponseEntity.notFound().build();
         }
     }
     
     @PostMapping("/addproduct")
     public Product addProduct(@RequestBody ProductDto pd) {
    	      CategoryDto cat = pd.getCategorydto();
    	      UserDto udto = pd.getUserdto();
    	       
    	      Product p = new Product();
    			 p.setDescription(pd.getDescription());
    			 p.setPrice(pd.getPrice());
    			 p.setStock_Qty(pd.getStock_Qty());
    			 p.setProduct_Name(pd.getProduct_Name());
    			 
    			 
    			 Category category = new Category();
    		        category.setCat_id(cat.getCat_id()); 
    			 p.setCategory(category);
    			 
    			 Users user = new Users();
    			   user.setUser_Id(udto.getUser_Id());
    			   p.setUsers(user);
    			 
    	    return  pser.saveProduct(p);
     }
     
    @PostMapping(value = "/uploadproductimg/{pid}", consumes = "multipart/form-data")
    public boolean uploadImage(@PathVariable int pid,@RequestBody MultipartFile file) {
    	boolean flag = true;
    	try {
		flag = pser.uploadImage(pid, file.getBytes());
		} catch (Exception e) {
			// TODO: handle exception
			flag = false;
		}
    	return flag;
    }
 
    @PutMapping( value = "/updateproduct/{product_id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable int product_id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestBody ProductDto updatedProductDto) {
        try {
            // Fetch the existing product by ID
            Product existingProduct = pser.getProductByIdupdte(product_id);

            // Update the relevant fields
            existingProduct.setDescription(updatedProductDto.getDescription());
            existingProduct.setPrice(updatedProductDto.getPrice());
            existingProduct.setStock_Qty(updatedProductDto.getStock_Qty());
            existingProduct.setProduct_Name(updatedProductDto.getProduct_Name());
            
            // ... other fields you want to update ...
           
            if (file != null && !file.isEmpty()) {
                pser.uploadImage(product_id, file.getBytes());
            }
            
            
            // Save the updated product
            pser.saveProduct(existingProduct);

            return ResponseEntity.ok("Product updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating product: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/deleteproduct/{pid}")
    public void deleteProduct(@RequestParam int pid) {
    	 pser.deleteProduct(pid);
    }

}