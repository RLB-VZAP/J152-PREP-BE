/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.user.service;

import com.vzap.helpdeskpro.user.repo.UserRepo;
import jakarta.inject.Inject;

/**
 *
 * @author rossb
 */
public class UserServiceImpl implements UserService{
    @Inject
    private UserRepo usreRepo;
}
