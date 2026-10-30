/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vzap.helpdeskpro.ticket.repo;

import com.vzap.helpdeskpro.config.DBConfig;
import com.vzap.helpdeskpro.ticket.model.Category;
import com.vzap.helpdeskpro.ticket.model.Priority;
import com.vzap.helpdeskpro.ticket.model.Status;
import com.vzap.helpdeskpro.ticket.model.Tickets;
import jakarta.ejb.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author kbser
 */

//LOG.log(Level, e) for debugging
//        (System.Logger.Level.ERROR)

@Singleton
public class TicketRepoImpl implements TicketRepo 
{
    
    private static final System.Logger LOG = System.getLogger(TicketRepoImpl.class.getName());

    @Override
    public Optional<Tickets> createTicket(Tickets tickets) 
    {
        String query = "INSERT INTO ticket(ticketId, createdBy, assignedTo, title, description, category, priority, status, createdAt, resolvedAt) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setString(1, tickets.getId().toString());
            ps.setString(2, tickets.getCreated_by());
            ps.setString(3, tickets.getAssigned_to());
            ps.setString(4, tickets.getTitle());
            ps.setString(5, tickets.getDescription());
            ps.setString(6, tickets.getCategory().name());
            ps.setString(7, tickets.getPriority().name());
            ps.setString(8, tickets.getStatus().name());
            ps.setTimestamp(9, tickets.getCreated_at());
            ps.setTimestamp(10, tickets.getResolved_at());
            
            if(ps.executeUpdate() > 0)
            {
                return Optional.of(tickets);
            }
        } 
        catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, e);
        }
        return Optional.empty();   
    }

    @Override
    public Optional<Tickets> findById(UUID ticketId) 
    {
        String query = "SELECT * FROM ticket WHERE ticketId = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setString(1, ticketId.toString());

            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) {
                    return Optional.of(
                            Tickets.builder()
                                    .created_by(rs.getString("createdBy"))
                                    .assigned_to(rs.getString("assignedTo"))
                                    .title(rs.getString("title"))
                                    .description(rs.getString("description"))
                                    .category(Category.valueOf(rs.getString("category")))
                                    .priority(Priority.valueOf(rs.getString("priority")))
                                    .status(Status.valueOf(rs.getString("status")))
                                    .created_at(rs.getTimestamp("createdAt"))
                                    .resolved_at(rs.getTimestamp("resolvedAt"))
                                    .build()
                    );
                }
            }

        } catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Tickets> findByCategory(Category category) 
    {
        List<Tickets> categories = new ArrayList<>();

        String sql = "SELECT * FROM ticket WHERE category = ? ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) 
        {
            ps.setString(1, category.name());
            
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) 
                {
                    categories.add( 
                            Tickets.builder()
                                    .created_by(rs.getString("createdBy"))
                                    .assigned_to(rs.getString("assignedTo"))
                                    .title(rs.getString("title"))
                                    .description(rs.getString("description"))
                                    .category(Category.valueOf(rs.getString("category")))
                                    .priority(Priority.valueOf(rs.getString("priority")))
                                    .status(Status.valueOf(rs.getString("status")))
                                    .created_at(rs.getTimestamp("createdAt"))
                                    .resolved_at(rs.getTimestamp("resolvedAt"))
                                    .build()         
                   );
                }
            }
            catch (SQLException e) 
            {
                e.printStackTrace();
            }

        } 
         catch (SQLException e) 
        {
            LOG.log(System.Logger.Level.ERROR, e);
        }

        return categories;
    }

    @Override
    public List<Tickets> findByPriority(Priority priority) 
    {
        List<Tickets> priorities = new ArrayList<>();

        String sql = "SELECT * FROM ticket WHERE priority = ? ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) 
        {
            ps.setString(1, priority.name());
            
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) 
                {
                    priorities.add( 
                            Tickets.builder()
                                    .created_by(rs.getString("createdBy"))
                                    .assigned_to(rs.getString("assignedTo"))
                                    .title(rs.getString("title"))
                                    .description(rs.getString("description"))
                                    .category(Category.valueOf(rs.getString("category")))
                                    .priority(Priority.valueOf(rs.getString("priority")))
                                    .status(Status.valueOf(rs.getString("status")))
                                    .created_at(rs.getTimestamp("createdAt"))
                                    .resolved_at(rs.getTimestamp("resolvedAt"))
                                    .build()         
                   );
                }
            }
            catch (SQLException e) 
            {
                e.printStackTrace();
            }

        } 
         catch (SQLException e) 
        {
            LOG.log(System.Logger.Level.ERROR, e);
        }

        return priorities;
    }

    @Override
    public List<Tickets> findByStatus(Status status) 
    {
        List<Tickets> statuses = new ArrayList<>();

        String sql = "SELECT * FROM ticket WHERE status = ? ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) 
        {
            ps.setString(1, status.name());
            
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) 
                {
                    statuses.add( 
                            Tickets.builder()
                                    .created_by(rs.getString("createdBy"))
                                    .assigned_to(rs.getString("assignedTo"))
                                    .title(rs.getString("title"))
                                    .description(rs.getString("description"))
                                    .category(Category.valueOf(rs.getString("category")))
                                    .priority(Priority.valueOf(rs.getString("priority")))
                                    .status(Status.valueOf(rs.getString("status")))
                                    .created_at(rs.getTimestamp("createdAt"))
                                    .resolved_at(rs.getTimestamp("resolvedAt"))
                                    .build()         
                   );
                }
            }
            catch (SQLException e) 
            {
                e.printStackTrace();
            }

        } 
         catch (SQLException e) 
        {
            LOG.log(System.Logger.Level.ERROR, e);
        }

        return statuses;
    }

    @Override
    public List<Tickets> findAll() 
    {
        List<Tickets> ticks = new ArrayList<>();
        
        String sql = "SELECT * FROM ticket ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) 
        {
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) 
                {
                    ticks.add( 
                            Tickets.builder()
                                    .created_by(rs.getString("createdBy"))
                                    .assigned_to(rs.getString("assignedTo"))
                                    .title(rs.getString("title"))
                                    .description(rs.getString("description"))
                                    .category(Category.valueOf(rs.getString("category")))
                                    .priority(Priority.valueOf(rs.getString("priority")))
                                    .status(Status.valueOf(rs.getString("status")))
                                    .created_at(rs.getTimestamp("createdAt"))
                                    .resolved_at(rs.getTimestamp("resolvedAt"))
                                    .build()         
                   );
                }
            }
            catch (SQLException e) 
            {
                e.printStackTrace();
            }

        } 
         catch (SQLException e) 
        {
            LOG.log(System.Logger.Level.ERROR, e);
        }

        return ticks;
    }

    @Override
    public Optional<Tickets> updateTicket(Tickets tickets) 
    {
        String query = "UPDATE ticket SET ticketId = ?, createdBy = ?, assignedTo = ?, title = ?, description = ?, category = ?, priority = ?, status = ?, createdAt + ?, resolvedAt = ? WHERE ticketId = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setString(1, tickets.getId().toString());
            ps.setString(2, tickets.getCreated_by());
            ps.setString(3, tickets.getAssigned_to());
            ps.setString(4, tickets.getTitle());
            ps.setString(5, tickets.getDescription());
            ps.setString(6, tickets.getCategory().name());
            ps.setString(7, tickets.getPriority().name());
            ps.setString(8, tickets.getStatus().name());
            ps.setTimestamp(9, tickets.getCreated_at());
            ps.setTimestamp(10, tickets.getResolved_at());
            
            if(ps.executeUpdate() > 0)
            {
                return Optional.of(tickets);
            }
        } 
        catch (SQLException e) {
            LOG.log(System.Logger.Level.ERROR, e);
        }
        return Optional.empty(); 
    }

    @Override
    public Optional<Tickets> deleteTicket(Tickets tickets) 
    {
        String query = "DELETE FROM ticket WHERE ticketId = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setString(1, tickets.getId().toString());
            
            if(ps.executeUpdate() > 0)
            {
                return Optional.of(tickets);
            }
        } 
        catch (SQLException e) 
        {
            LOG.log(System.Logger.Level.ERROR, e);
        }
        return Optional.empty();
    }
    
    
    // Method added to receive the tickets by the creator
    public List<Tickets> findByCreator(String creator) 
    {
        List<Tickets> ticks = new ArrayList<>();
        
        String sql = "SELECT * FROM ticket WHERE createdBy = ? ORDER BY createdAt";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) 
        {
             ps.setString(1, creator);
            try(ResultSet rs = ps.executeQuery())
            {
                while (rs.next()) 
                {
                    ticks.add( 
                            Tickets.builder()
                                    .created_by(rs.getString("createdBy"))
                                    .assigned_to(rs.getString("assignedTo"))
                                    .title(rs.getString("title"))
                                    .description(rs.getString("description"))
                                    .category(Category.valueOf(rs.getString("category")))
                                    .priority(Priority.valueOf(rs.getString("priority")))
                                    .status(Status.valueOf(rs.getString("status")))
                                    .created_at(rs.getTimestamp("createdAt"))
                                    .resolved_at(rs.getTimestamp("resolvedAt"))
                                    .build()         
                   );
                }
            }
            catch (SQLException e) 
            {
                e.printStackTrace();
            }

        } 
         catch (SQLException e) 
        {
            LOG.log(System.Logger.Level.ERROR, e);
        }

        return ticks;
    }
}
