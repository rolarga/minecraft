package ch.bukkit.playground.instant;

import ch.bukkit.playground.TestBase;
import ch.bukkit.playground.helper.TestZombie;
import ch.bukkit.playground.instant.model.BattleType;
import ch.bukkit.playground.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class InstantListenerTest extends TestBase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        loadInstantBattle();
    }


    @After
    public void tearDown() throws Exception {
        Bukkit.getServer().getPluginManager().clearPlugins();
    }

    @Test
    public void testPlayerRespawn() throws Exception {
        Location specLocation = new Location(world, 2, 2, 2);

        // player should be teleported to his origin location when he dies and there is no spectator location
        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(player, new Location(world, 0, 0, 0), false);
        Bukkit.getServer().getPluginManager().callEvent(playerRespawnEvent);

        assert playerRespawnEvent.getRespawnLocation().equals(originLocation);

        // player should be teleported to spectator location when he dies - and originSpec list should contain his origin location
        battleHandler.getBattleConfiguration().setPosSpectator(specLocation);
        PlayerRespawnEvent playerRespawnEvent2 = new PlayerRespawnEvent(vip, new Location(world, 0, 0, 0), false);
        Bukkit.getServer().getPluginManager().callEvent(playerRespawnEvent2);

        assert playerRespawnEvent2.getRespawnLocation().equals(specLocation);
        assert battleHandler.getBattleData().getOriginSpectatorLocations().get(vip).equals(originLocation);
    }

    @Test
    public void testPlayerMove() throws Exception {
        PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(player, new Location(world, 0, 0, 0), new Location(world, 10, 10, 10));
        Bukkit.getServer().getPluginManager().callEvent(playerMoveEvent);

        assert playerMoveEvent.getTo() == playerMoveEvent.getFrom();
    }

    @Test
    public void testPlayerTeleport() throws Exception {
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, new Location(world, 0, 0, 0), new Location(world, 10, 10, 10));
        Bukkit.getServer().getPluginManager().callEvent(event);

        assert event.isCancelled();

        event = new PlayerTeleportEvent(op, new Location(world, 0, 0, 0), new Location(world, 10, 10, 10));
        Bukkit.getServer().getPluginManager().callEvent(event);

        assert !event.isCancelled();

    }

    @Test
    public void testPlayerFlight() throws Exception {
        PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(player, true);
        Bukkit.getServer().getPluginManager().callEvent(event);

        assert event.isCancelled();

        event = new PlayerToggleFlightEvent(op, true);
        Bukkit.getServer().getPluginManager().callEvent(event);

        assert !event.isCancelled();
    }

    @Test
    public void testEntityDeathEvent() throws Exception {
        List<ItemStack> stacks = new LinkedList<ItemStack>();
        stacks.add(new ItemStack(1));
        stacks.add(new ItemStack(2));
        EntityDeathEvent event = new EntityDeathEvent(player, stacks);
        event.setDroppedExp(100);

        Bukkit.getServer().getPluginManager().callEvent(event);

        assert event.getDrops().size() == stacks.size();
        assert event.getDroppedExp() == 100;

        EntityDeathEvent event2 = new EntityDeathEvent(vip, stacks);
        event2.setDroppedExp(100);

        Bukkit.getServer().getPluginManager().callEvent(event2);

        assert event2.getDrops().size() == 0;
        assert event2.getDroppedExp() == 0;
    }

    @Test
    public void testPlayerQuit() throws Exception {
        assert battleHandler.getBattleData().getActivePlayers().containsKey(player);

        PlayerQuitEvent event = new PlayerQuitEvent(player, "I want to leave now.");
        Bukkit.getServer().getPluginManager().callEvent(event);

        assert !battleHandler.getBattleData().getActivePlayers().containsKey(player);
    }

    @Test
    public void testEntityDamageByEntity() throws Exception {
        // COOP test
        battleHandler.getBattleConfiguration().setBattleType(BattleType.COOP);
        EntityDamageByEntityEvent player2Player = new EntityDamageByEntityEvent(player, player2, EntityDamageEvent.DamageCause.CONTACT, 100);
        Bukkit.getServer().getPluginManager().callEvent(player2Player);

        assert player2Player.isCancelled();
        assert player2Player.getDamage() == 0;

        // mob 2 player dmg
        Entity entity = new TestZombie();
        EntityDamageByEntityEvent player2Mob = new EntityDamageByEntityEvent(player, entity, EntityDamageEvent.DamageCause.CONTACT, 100);
        Bukkit.getServer().getPluginManager().callEvent(player2Mob);

        assert !player2Mob.isCancelled();
        assert player2Mob.getDamage() == 100;

        // PVP test
        battleHandler.getBattleConfiguration().setBattleType(BattleType.PVP);
        player2Player = new EntityDamageByEntityEvent(player, player2, EntityDamageEvent.DamageCause.CONTACT, 100);
        Bukkit.getServer().getPluginManager().callEvent(player2Player);

        assert !player2Player.isCancelled();
        assert player2Player.getDamage() == 100;

        // PVPGROUP test
        battleHandler.getBattleData().setGroups(PlayerUtil.getEqualDistributedGroupByLevel(battleHandler.getBattleConfiguration().getGroupAmount(), battleHandler.getBattleData().getActivePlayers().keySet()));

        // not in same group
        battleHandler.getBattleConfiguration().setBattleType(BattleType.GROUPPVP);
        player2Player = new EntityDamageByEntityEvent(player, player2, EntityDamageEvent.DamageCause.CONTACT, 100);
        Bukkit.getServer().getPluginManager().callEvent(player2Player);

        assert !player2Player.isCancelled();
        assert player2Player.getDamage() == 100;

        // in same group
        battleHandler.getBattleConfiguration().setBattleType(BattleType.GROUPPVP);
        player2Player = new EntityDamageByEntityEvent(player, vip, EntityDamageEvent.DamageCause.CONTACT, 100);
        Bukkit.getServer().getPluginManager().callEvent(player2Player);

        assert player2Player.isCancelled();
        assert player2Player.getDamage() == 0;
    }
}