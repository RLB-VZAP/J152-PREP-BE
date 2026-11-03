
package com.vzap.helpdeskpro.ticket.model;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author kbser
 */
public enum Status 
{
    OPEN,IN_PROGRESS,RESOLVED;
    
    private static final Map<Status, Set<Status>> TRANSITIONS = Map.of(
    OPEN, Set.of(IN_PROGRESS),
     IN_PROGRESS, Set.of(RESOLVED),
     RESOLVED, Set.of()
    );
    
    public boolean canTransitionTo(Status target){
        return TRANSITIONS.get(this).contains(target);
    }
}
