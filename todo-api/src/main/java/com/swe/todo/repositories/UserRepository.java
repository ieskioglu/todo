package com.swe.todo.repositories;

import com.swe.todo.models.User;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

import java.util.Optional;

public interface UserRepository extends CouchbaseRepository<User,String> {
    Optional<User> findByEmail(String email);
}
