/*
 * 文件: YahtzeeConstants.java
 * -------------------
 * 简介：全局变量 - 快艇骰子游戏子文件
 */

public interface YahtzeeConstants {

	public static final int APPLICATION_WIDTH = 600;
	public static final int APPLICATION_HEIGHT = 350;

	/** 骰子相关 */
	public static final int N_DICE = 5;		//骰子个数
	public static final int DICE_MAX = 6; 	// 骰子最大点数

	/** 玩家相关 */
	public static final int MAX_PLAYERS = 4;

	/** 分类相关 */
	public static final int N_CATEGORIES = 17;			//所有类数
	public static final int N_SCORING_CATEGORIES = 13;  //可以得分的类数
	public static final int ONES = 1;
	public static final int TWOS = 2;
	public static final int THREES = 3;
	public static final int FOURS = 4;
	public static final int FIVES = 5;
	public static final int SIXES = 6;
	public static final int UPPER_SCORE = 7;
	public static final int UPPER_BONUS = 8;
	public static final int THREE_OF_A_KIND = 9;
	public static final int FOUR_OF_A_KIND = 10;
	public static final int FULL_HOUSE = 11;
	public static final int SMALL_STRAIGHT = 12;
	public static final int LARGE_STRAIGHT = 13;
	public static final int YAHTZEE = 14;
	public static final int CHANCE = 15;
	public static final int LOWER_SCORE = 16;
	public static final int TOTAL = 17;
  
}
