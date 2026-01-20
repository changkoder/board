package com.project.board.global.config;

import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void init(){
        categoryRepository.save(new Category("자유"));
        categoryRepository.save(new Category("질문"));
        categoryRepository.save(new Category("정보공유"));
    }
}
