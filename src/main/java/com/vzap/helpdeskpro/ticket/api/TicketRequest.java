/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.ticket.api;

/**
 *
 * @author Hp Elitebook
 */

import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketRequest {

    private String title;
    private String description;
    private Category category;
    private Priority priority;
}
