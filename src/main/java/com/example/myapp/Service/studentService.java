package com.example.myapp.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.myapp.entity.Student;
import com.example.myapp.repository.studentRepository;

@Service
public class studentService {

    private final studentRepository studentRepository;

    public studentService(studentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {
        studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).get();
    }

    public void updateStudent(Long id, Student student) {
        studentRepository.findById(id).ifPresent(existingStudent -> {
            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());
            existingStudent.setEmail(student.getEmail());
            studentRepository.save(existingStudent);
        });
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

}
