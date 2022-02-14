package tech.simonwalker.blockinfection.BlockBFS;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import tech.simonwalker.blockinfection.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

public abstract class BlockBFS {
    /**
     * A route for the search algorithm to take.
     *
     * @param tickDelay Delay (in ticks) to postpone
     *                  discovering a route. Specifying 0
     *                  will result in instant discovery.
     */
    public record Route(Vector vector, int tickDelay) { }

    protected final Plugin plugin;
    private final HashSet<Vector> discovered = new HashSet<>();
    @Getter private final World world;

    // maps possible routes to search with the scheduled time to search them
    private final HashMap<Vector, Integer> frontier = new HashMap<>();

    private int time = 0;
    private BukkitTask taskTimer;

    public BlockBFS(Plugin plugin, Location origin) {
        this.plugin = plugin;
        this.world = origin.getWorld();
        frontier.put(origin.toVector(), 0);
    }

    public void start() {
        if (taskTimer != null) throw new RuntimeException("The search has already started.");

        taskTimer = new BukkitRunnable() {
            @Override
            public void run() {
                privateOnTick();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    /**
     * Stops the spread prematurely.
     * (Stops normally when no more blocks can be
     * accessed)
     *
     * This instance should be thrown out after it finishes
     * the spread. Create a new instance to start a new
     * spread.
     */
    public void cancel() {
        taskTimer.cancel();
        onFinish(time);
    }

    public Iterator<Vector> getDiscovered() {
        return discovered.iterator();
    }

    public int amountDiscovered() {
        return discovered.size();
    }

    private void privateOnTick() {
        time++;

        boolean searchAgain;

        /*
        Nodes are discovered in a Breadth First Search from the frontier set
        and added to the discovered set. As nodes are discovered onNodeDiscovered
        is called and the frontier is expanded with expandNode.

        Some routes in the frontier have delays attached to them to slow down the
        rate of discovery.
         */
        do {
            searchAgain = false;
            List<Route> expansion = new ArrayList<>();
            var frontierIterator = frontier.entrySet().iterator();

            if (!frontierIterator.hasNext()) {
                cancel();
                return;
            }

            while (frontierIterator.hasNext()) {
                var next = frontierIterator.next();
                var node = next.getKey();
                var scheduledTime = next.getValue();

                if (scheduledTime > time) continue;

                // we have discovered a new node

                frontierIterator.remove();
                discovered.add(node);
                onNodeDiscovered(node);

                // expand the node

                var routes = expandNode(node);

                for (Route route : routes) {
                    // nodes that have already been discovered can not be
                    // rediscovered.
                    if (discovered.contains(route.vector())) continue;

                    expansion.add(route);
                }
            }

            for (Route route : expansion) {
                frontier.put(route.vector, time + route.tickDelay);

                // if expanding a node yields routes that must
                // be expanded immediately => searchAgain = true;
                if (route.tickDelay() < 1) {
                    searchAgain = true;
                }
            }
        } while (searchAgain);

        // all expansions completed for this tick

        onTick(time);
    }

    /**
     * Feel free to override this method. (Empty implementation in super)
     * Called every tick while running.
     *
     * @param time The time (in ticks) since start()ed.
     */
    protected void onTick(int time) {}

    /**
     * Feel free to override this method. (Empty implementation in super)
     *
     * @param time The total time (in ticks) while running
     */
    protected void onFinish(int time) {
        int destruction = amountDiscovered();

        String pattern = "mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String duration = simpleDateFormat.format(new Date((long) time / 20 * 1000));

        Utils.msg(Bukkit.getPlayer("simondoesstuff"),
                "§aConsumed §e%s§a blocks in §e%sm§a.",
                destruction, duration
        );
    }

    /**
     * Feel free to override this method. (Empty implementation in super)
     */
    protected void onNodeDiscovered(Vector node) {}

    protected abstract Route[] expandNode(Vector node);
}
