/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author rossb
 */
public class HashUtil {
    public static String hashString(String value){
        return BCrypt.hashpw(value, BCrypt.gensalt(10));
    }
    
    public static boolean verifyHash(String value, String hashedValue){
        return BCrypt.checkpw(value, hashedValue);
    }
}
