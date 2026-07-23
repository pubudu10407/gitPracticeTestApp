package com.example.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.myapp.Service.studentService;
import com.example.myapp.entity.Student;

import org.springframework.ui.Model;

@Controller
public class appController {

    private final studentService studentService;

    public appController(studentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "studnet registry");
        model.addAttribute("students", studentService.getAllStudents());
        return "index";
    }

    @GetMapping("/details")
    public String detail(Model model) {
        model.addAttribute("title", "studnet details");
        return "index";
    }

    @GetMapping("Students/add")
    public String add(Model model) {
        model.addAttribute("title", "Add new student");
        // model.addAttribute("submitText", "Add Student");
        return "add";
    }

    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute Student student) {
        if (student.getId() == null) {
            studentService.addStudent(student);
        } else {
            studentService.updateStudent(student.getId(), student);
        }
        return "redirect:/";
    }

    @GetMapping("/Student/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Edit Student");
        model.addAttribute("submitText", "Update Student");
        model.addAttribute("student", studentService.getStudentById(id));
        return "add";
    }

    @GetMapping("/Student/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
        return "redirect:/";
    }

}
