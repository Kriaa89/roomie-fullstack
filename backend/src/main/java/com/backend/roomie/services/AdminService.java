package com.backend.roomie.services;

import com.backend.roomie.models.Admin;
import com.backend.roomie.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    // find all admins
    public List<Admin> findAll() {
        return (List<Admin>) adminRepository.findAll();
    }
    // find admin by id
    public Admin findById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }
    // find by email
    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }
    // save admin
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
    // delete admin
    public void delete(Long id) {
        adminRepository.deleteById(id);
    }
}
