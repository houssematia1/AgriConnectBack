package com.example.usermanagementbackend.service;

import com.example.usermanagementbackend.dto.LivreurDTO;
import com.example.usermanagementbackend.entity.Livreur;
import com.example.usermanagementbackend.entity.User;
import com.example.usermanagementbackend.mapper.LivreurMapper;
import com.example.usermanagementbackend.repository.LivreurRepository;
import com.example.usermanagementbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivreurService {

    @Autowired
    private LivreurRepository livreurRepository;

    @Autowired
    private UserRepository userRepository;

    public List<LivreurDTO> getAllLivreurs() {
        return livreurRepository.findAll().stream()
                .map(LivreurMapper::toDTO)
                .collect(Collectors.toList());
    }


}