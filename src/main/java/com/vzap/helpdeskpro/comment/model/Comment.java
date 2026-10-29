/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.comment.model;

/**
 *
 * @author 123iz
 */

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

/**
 * The API accepts only the comment text from the client. The ticket ID comes
 * from the URL, while the author ID and creation time are assigned server-side.
 */

public class Comment {
    private UUID commentId;
    private UUID ticketId;
    private UUID authorId;
    private String content;
    private LocalDateTime createdAt;
}
