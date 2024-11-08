package plugin.enemydown;

import org.bukkit.Bukkit;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.enemydown.command.EnemyDownCommand;

public final class main extends JavaPlugin{

  @Override
  public void onEnable() {
    EnemyDownCommand enemyDownCommand = new EnemyDownCommand();
    Bukkit.getPluginManager().registerEvents(enemyDownCommand, this);
    getCommand("enemyDown").setExecutor(enemyDownCommand);
  }
}


