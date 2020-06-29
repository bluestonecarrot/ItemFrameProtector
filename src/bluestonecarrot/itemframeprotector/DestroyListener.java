package bluestonecarrot.itemframeprotector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import static org.bukkit.event.hanging.HangingBreakEvent.RemoveCause.PHYSICS;

/**
 * The main class that listens for when an item frame is destroyed, and cancels the event if it's done by a boat
 * @author bluestonecarrot
 */
public class DestroyListener implements Listener {

    /**
     * This function is called when an event happens on an itemframe that should cause it to break
     * As far as I know, this is the only event that trying to break a boat with an item frame triggers
     * @param event The event passed by the api
     */
    @EventHandler
    public void HangingBreakEventListener(HangingBreakEvent event) {
        // when an itemframe is broken by a boat, the cause is put as "PHYSICS".
        // why is it like this? i have no idea
        if (event.getCause() != PHYSICS) {
            // if the cause is not physics, it was definitely not broken by a boat
            return;
        }

        Hanging hanging = event.getEntity();

        // if the hanging thing is not an itemframe, return
        if (! (hanging instanceof ItemFrame)) {
            return;
        }

        // because removing the block behind also has physics as a cause, we now check if there is still
        // a block behind the itemframe. if there still is, afaik, it means that a boat broke the item frame
        ItemFrame itemFrame = (ItemFrame) hanging; // first get the actual itemframe entity

        if (supportedByBlock(itemFrame)) {
            // well if it's supported by a block and there's a physics event the cause is probably boat glitch
            event.setCancelled(true);
        }
    }

    /**
     * Checks whether an item frame has a block supporting it
     * @param frame The ItemFrame object to check
     * @return True if supported by block, false if not
     */
    public boolean supportedByBlock(ItemFrame frame) {
        // get the world the itemframe is in to check there
        World world = frame.getWorld();
        // get the itemframe location
        Location loc = frame.getLocation();

        // the location of the block that should be supporting the item frame
        Location support = null;
        // now loop through the attached face possibilities and get the block it should be on x, y, and z
        switch (frame.getAttachedFace()) {
            case NORTH:
                support = new Location(world, loc.getX(), loc.getY(), loc.getZ() - 1);
                break;
            case SOUTH:
                support = new Location(world, loc.getX(), loc.getY(), loc.getZ() + 1);
                break;
            case WEST:
                support = new Location(world, loc.getX() - 1, loc.getY(), loc.getZ());
                break;
            case EAST:
                support = new Location(world, loc.getX() + 1, loc.getY(), loc.getZ());
                break;
            case UP:
                support = new Location(world, loc.getX(), loc.getY() + 1, loc.getZ());
                break;
            case DOWN:
                support = new Location(world, loc.getX(), loc.getY() - 1, loc.getZ());
                break;
            default: // should not reach here, some weird stuff is going on if it does
                // hacks were probably used if it gets here, so assume malicious intent and just keep the frame
                return true;
        }

        // if the block behind it is air, water, or lava, it's safe to assume it's no longer supported
        return !(support.getBlock().getType() == Material.AIR ||
                support.getBlock().getType() == Material.WATER ||
                support.getBlock().getType() == Material.LAVA);
    }

}
