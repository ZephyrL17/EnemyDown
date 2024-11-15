package plugin.enemydown.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * EnemyDownのゲームを実行する際のスコアを扱うクラスのオブジェクト
 * プレイヤー名、合計点数、日時の情報などを持つ
 */

@Getter
@Setter
public class PlayerScore {

  private String playerName;
  private int score;
}
