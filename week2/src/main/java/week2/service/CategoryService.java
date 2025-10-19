package week2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import week2.dto.CategoryDto;
import week2.entity.Category;
import week2.exception.ResourceNotFoundException;
import week2.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<CategoryDto> getAllCategories() {
        logger.info("Fetching all categories from database");
        List<Category> categories = categoryRepository.findAll();
        logger.debug("Found {} categories", categories.size());
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public CategoryDto getCategoryById(Integer id) {
        logger.info("Fetching category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category", "id", id);
                });
        logger.debug("Found category: {}", category.getName());
        return convertToDto(category);
    }
    
    public CategoryDto createCategory(CategoryDto categoryDto) {
        logger.info("Creating new category: {}", categoryDto.getName());
        Category category = convertToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        logger.info("Successfully created category with id: {}", savedCategory.getCategoryId());
        return convertToDto(savedCategory);
    }
    
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        logger.info("Updating category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category", "id", id);
                });
        
        category.setName(categoryDto.getName());
        
        Category updatedCategory = categoryRepository.save(category);
        logger.info("Successfully updated category with id: {}", id);
        return convertToDto(updatedCategory);
    }
    
    public void deleteCategory(Integer id) {
        logger.info("Deleting category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category", "id", id);
                });
        categoryRepository.delete(category);
        logger.info("Successfully deleted category with id: {}", id);
    }
    
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());
        return dto;
    }
    
    private Category convertToEntity(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}

