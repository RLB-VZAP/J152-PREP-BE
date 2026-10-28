/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.commons;

import jakarta.ws.rs.core.Response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author rossb
 * @param <T>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ServiceResponse<T> {
    private T value;
    private String message;
    private Status status;
    private Exception e;
    
    public static <T> ServiceResponse<T> success(T value, String message){
        return ServiceResponse.<T>builder()
                .value(value)
                .status(Status.OK)
                .message(message)
                .build();
    }
    
    public static <T> ServiceResponse<T> error(Status status){
        return error(status, null, null);
    }
    
    public static <T> ServiceResponse<T> error(Status status, String message){
        return error(status, message, null);
    }
    
    public static <T> ServiceResponse<T> error(Status status, String message, Exception e){
        return ServiceResponse.<T>builder()
                .message(message)
                .status(status)
                .e(e)
                .build();
    }
    
}
