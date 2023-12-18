package com.shop.admin.repository;

import com.shop.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {
    public User findUserByEmail(String email);
    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1% ")
    public Page<User> findAllByKeyword(String keyword, Pageable pageable);

    public Long countUserById(Integer id);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE  u.id = ?1")
    @Modifying
    public void
    updateUserStatus(Integer id, boolean enabled);
}
