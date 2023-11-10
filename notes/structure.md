# Mod Structure

## Requirements

- Robust way program scripts/routines via code & addon mods

- Versatile control via commands
  
  - "proto" routines using datapack functions

- Integrate with native AI when desired

## Code ideas

- **Routine Objects**
  
  - Called every tick
  
  - Serializable to NBT
  
  - Standard set of functions:
    
    - `OnStart`: when the routine is added to the entity.
    
    - `OnResume`: After then routine has been desterilized and resumes operation.
    
    - `OnTick`: Called every tick while the routine is active.
    
    - `OnShutdown`: Called when this routine is shutting down for any reason.
  
  - No need for "should continue" function; call shutdown in tick function to stop.

- **Routine Stack**
  
  - An ordered "stack" of routines
  
  - Routines are executed in stack order.
  
  - Routines can access data from previous routines and can shutdown subsequent routines.

- Various interfaces implemented via mixins allow routines to access native AI from mobs.
  
  - Pathfinding, etc.

- Use Baritone to implement "smart AI override?"
