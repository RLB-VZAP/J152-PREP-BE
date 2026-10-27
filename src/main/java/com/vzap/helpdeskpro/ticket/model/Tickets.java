
package com.vzap.helpdeskpro.ticket.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Karabo Sereme
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Tickets 
{
    private UUID id;
    private String created_by;
    private String assigned_to;
    private String title; 
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private Timestamp created_at;
    private Timestamp resolved_at;
}
