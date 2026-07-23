package com.example.myapp.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.myapp.entity.Student;

public interface studentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByFirstName(String firstName);

    Optional<Student> findByLastName(String lastName);

    Optional<Student> findById(Long id);

    void deleteById(Long id);

}
