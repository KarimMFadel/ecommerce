package com.tornado.ecommerce.model.repository;

import org.springframework.stereotype.Repository;

import com.tornado.ecommerce.model.entity.Role;
import com.tornado.ecommerce.model.repository.generic.GenericRepository;

@Repository
public interface RoleRepository extends GenericRepository<Role,Long> {

}
