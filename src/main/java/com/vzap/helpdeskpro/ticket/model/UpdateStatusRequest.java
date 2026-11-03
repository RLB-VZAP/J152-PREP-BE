/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.ticket.model;

import com.vzap.helpdeskpro.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author BLVCK AURA
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateStatusRequest {
    private User actor;
    private Status target;
}
