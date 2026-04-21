package com.github.carlossnogueira.helpdesk.infrastructure.repository;

import com.github.carlossnogueira.helpdesk.infrastructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
