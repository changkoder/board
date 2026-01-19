package com.project.board.domain.user.repository;

import com.project.board.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);//왜 굳이 옵셔널?

    boolean existsByEmail(String email);//이건 용도가 뭐지
}
