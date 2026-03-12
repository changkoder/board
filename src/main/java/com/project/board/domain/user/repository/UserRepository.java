package com.project.board.domain.user.repository;

import com.project.board.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByStatus(User.Status status);

    Page<User> findByStatus(User.Status status, Pageable pageable);

    Optional<User> findByNickname(String nickname);

    boolean existsByNickname(String nickname);
}
