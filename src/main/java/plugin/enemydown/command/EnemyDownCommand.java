package plugin.enemydown.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import plugin.enemydown.Main;
import plugin.enemydown.data.PlayerScore;

public class EnemyDownCommand extends BaseCommand implements Listener {

  private Main main;
  private List<PlayerScore> playerScoreList = new ArrayList<>();

  public EnemyDownCommand(Main main) {
    this.main = main;
  }

  @Override
  public boolean onExecutePlayerCommand(Player player) {
    PlayerScore nowPlayer = getPlayerScore(player);
    nowPlayer.setGameTime(20);
    World world = player.getWorld();
    //プレイヤーの状態を初期化する。（体力と空腹度を最大値にする）
    initPlayerStatus(player);

    Bukkit.getScheduler().runTaskTimer(main, Runnable -> {
      if (nowPlayer.getGameTime() <= 0) {
        Runnable.cancel();
        player.sendTitle("ゲームが終了しました。",
            nowPlayer.getPlayerName() + "合計" + nowPlayer.getScore() + "点!",
            0,30,0);
        nowPlayer.setScore(0);
        List<Entity> nearbyEnemys = player.getNearbyEntities(50, 0, 50);
        for(Entity enemy : nearbyEnemys) {
          switch (enemy.getType()) {
            case ZOMBIE , SKELETON , WITCH -> enemy.remove();
          }
        }
        return;
      }
      world.spawnEntity(getEnemySpawnLocation(player, world), getEnemy());
      nowPlayer.setGameTime(nowPlayer.getGameTime() - 5);
    },0,5 * 20);
    return false;
  }

  @Override
  public boolean onExecuteNPCCommand(CommandSender sender) {
    return true;
  }

  @EventHandler
  public void onEnemyDeath(EntityDeathEvent e) {
    LivingEntity enemy = e.getEntity();
    Player player = enemy.getKiller();
    if (Objects.isNull(player) || playerScoreList.isEmpty()) {
      return;
    }

    for(PlayerScore playerScore  : playerScoreList) {
      if (playerScore.getPlayerName().equals(player.getName())) {
        int point = switch (enemy.getType()) {
          case ZOMBIE -> 10;
          case SKELETON, WITCH -> 20;
          default -> 0;
        };

        playerScore.setScore(playerScore.getScore() + point);
        player.sendMessage("敵を倒した！現在のスコアは" + playerScore.getScore() + "点!");
      }
    }
  }

  /**
   * 現在実行しているプレイヤーのスコア情報を取得する。
   * @param player コマンドを実行したぷれいやー
   * @return 現在実行しているプレイヤーの情報
   */
  private PlayerScore getPlayerScore(Player player) {
    if (playerScoreList.isEmpty()) {
      return addNewPlayer(player);
    } else {
      for(PlayerScore playerScore : playerScoreList) {
        if(!playerScore.getPlayerName().equals(player.getName())) {
          return addNewPlayer(player);
        } else {
          return playerScore;
        }
      }
    }
    return null;
  }

  /**
   * 新規のプレイヤー情報をリストに追加します。
   *
   * @param player 新規王レイヤー
   * @return 新規プレイヤー
   */
  private PlayerScore addNewPlayer(Player player) {
    PlayerScore newPlayer = new PlayerScore();
    newPlayer.setPlayerName(player.getName());
    playerScoreList.add(newPlayer);
    return newPlayer;
  }

  private void initPlayerStatus(Player player) {
    player.setHealth(20);
    player.setFoodLevel(20);
    PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    inventory.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    inventory.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    inventory.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    inventory.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
    inventory.setItemInOffHand(new ItemStack(Material.SHIELD));
  }

  /**
   * 敵の出現エリアを取得します。
   * 出現エリアはX軸とZ軸は自分の位置からプラス、ランダムで-10～9の値が設定されます。
   * Y軸はプレイヤーと同じ位置になります。
   *
   * @param player　コマンドを実行したプレイヤー
   * @param world　コマンドを実行したプレイヤーが所属するワールド
   * @return　敵の出現場所
   */
  private Location getEnemySpawnLocation(Player player, World world) {
    Location playerlocation = player.getLocation();
    int randomX = new SplittableRandom().nextInt(20) - 10;
    int randomZ = new SplittableRandom().nextInt(20) - 10;

    double x = playerlocation.getX() + randomX;
    double y = playerlocation.getY();
    double z = playerlocation.getZ() + randomZ;

    return new Location(world, x, y, z);
  }

  /**
   * ランダムで敵を抽選して、その結果を取得します。
   *
   * @return　敵
   */
  private EntityType getEnemy() {
    List<EntityType> enemyList = List.of(EntityType.ZOMBIE,EntityType.SKELETON,EntityType.WITCH);
    return enemyList.get(new SplittableRandom().nextInt(3));
  }
}
