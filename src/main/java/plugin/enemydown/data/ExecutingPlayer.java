package plugin.enemydown.data;

import lombok.Getter;
import lombok.Setter;

/**
 * EnemyDownのゲームを実行する際のプレイヤー情報を扱うクラスのオブジェクト
 * プレイヤー名、合計点数、日時の情報などを持つ
 */

@Getter
@Setter
public class ExecutingPlayer {

  private String playerName;
  private int score;
  private int gameTime;

  public ExecutingPlayer(String playerName) {
    this.playerName = playerName;
  }
}
