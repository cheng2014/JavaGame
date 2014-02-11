/*
 * 文件: Yahtzee.java
 * -------------------
 * 简介：快艇骰子游戏
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	private int nPlayers;
	private String[] playerNames;

	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	
	private int[] dice; // 骰子点数
	private int[][] categoriesScores; // 分类得分  
	private int[][] selectedCategories; // 该类是否被选中
	private int category; //被选中的分类
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		initGame();
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
		endGame();
	}

	/** 游戏初始化 */
	private void initGame(){
		IODialog dialog = getDialog();
		
		nPlayers = dialog.readInt("Enter number of players");
		while(true){
			if(nPlayers <= MAX_PLAYERS) break;
			nPlayers = dialog.readInt("You can only enter up to " + MAX_PLAYERS +" number of players. Enter number of players");
		}

		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i-1] = dialog.readLine("Enter name for player " + i);
		}
		
		dice = new int[N_DICE];
		categoriesScores = new int[nPlayers+1][N_CATEGORIES+1];     //数组均加上1，虽然浪费了一点空间，但是换来了方便, 这一点很方便！
		selectedCategories = new int[nPlayers+1][N_CATEGORIES+1];
	}
	
	/** 游戏开始 */
	private void playGame() {
		for(int i=1;i<=N_SCORING_CATEGORIES;i++){
			for(int j=1;j<=nPlayers;j++){
				palyOneRound(j);
			}
		}
	}
	
	/**　游戏结束后统计总得分情况并判定胜负*/
	private void endGame(){
		calculateAllScores();
		
		int winners = 1;
		int highscore = categoriesScores[1][TOTAL];
		for(int i=1;i<=nPlayers;i++){
			if(categoriesScores[i][16]>highscore){
				highscore = categoriesScores[i][16];
				winners = i; 
			}
		}
		display.printMessage(playerNames[winners-1] + "你赢得了最终的胜利！恭喜！");
	}
	
	private void calculateAllScores(){
		for(int i=1;i<=nPlayers;i++){
			for(int j=ONES;j<=SIXES;j++){
				categoriesScores[i][UPPER_SCORE] += categoriesScores[i][j];
			}
			
			if(categoriesScores[i][UPPER_SCORE]>63){
				categoriesScores[i][UPPER_BONUS] = 35;
			}
			
			for(int j=THREE_OF_A_KIND;j<=CHANCE;j++){
				categoriesScores[i][LOWER_SCORE] += categoriesScores[i][j];
			}
			
			categoriesScores[i][TOTAL] = categoriesScores[i][UPPER_SCORE]+categoriesScores[i][UPPER_BONUS]+categoriesScores[i][LOWER_SCORE];
			
			display.updateScorecard(UPPER_SCORE, i, categoriesScores[i][UPPER_SCORE]);
			display.updateScorecard(UPPER_BONUS, i, categoriesScores[i][UPPER_BONUS]);
			display.updateScorecard(LOWER_SCORE, i, categoriesScores[i][LOWER_SCORE]);
			display.updateScorecard(TOTAL, i, categoriesScores[i][TOTAL]);
		}
	}
	
	/** 每个玩家每一轮游戏需要进行的操作 
	 * 1: 扔骰子  
	 * 2: 选中想替换的骰子重新扔两次
	 * 3: 选中分类
	 * 4: 计算得分并显示*/
	private void palyOneRound(int thePlay){
		display.printMessage(playerNames[thePlay-1] + "轮到你扔骰子了");
		display.waitForPlayerToClickRoll(thePlay);
		rollDice();
		display.displayDice(dice);
		
		for(int times=0;times<2;times++){
			display.printMessage(playerNames[thePlay-1] + "你还可以再扔" + (2-times) + "次，请选择要仍的骰子吧");
			display.waitForPlayerToSelectDice();
		
			rollAgain(dice);
			display.displayDice(dice);
		}
		
		display.printMessage(playerNames[thePlay-1] + "你的机会用完了，请选择要放到哪一栏");
		
//		setDice(0,2);
//		setDice(1,5);
//		setDice(2,4);
//		setDice(3,3);
//		setDice(4,3);
		
		while(true){
			category = display.waitForPlayerToSelectCategory();
			if(checkCategoryLegal(thePlay)) break;
		}

		calculteCategoryScore(thePlay);
		display.updateScorecard(category, thePlay, categoriesScores[thePlay][category]);
	}
	
	private void rollDice(){
		for(int i=0;i<N_DICE;i++){
			dice[i] = rgen.nextInt(1, 6);
		}
	}
	
	private void rollAgain(int[] dice){
		for(int i=0;i<N_DICE;i++){
			if(display.isDieSelected(i)){
				dice[i] = rgen.nextInt(1, 6);
			}
		}
	}
	
	//用于自己调测用
//	private void setDice(int i,int j){
//		dice[i] = j;
//	}
//	
	/** 判断是否可以选择该类 
	 *1：是否被选过
	 *2：是否为需要最后计算的那几个分类*/
	private boolean checkCategoryLegal(int thePlay){
		if(selectedCategories[thePlay][category] == 1){
			display.printMessage("这一栏已经被选过了，请重新选择");
			return false;
		}
		if(category == UPPER_SCORE || category == UPPER_BONUS
				|| category == LOWER_SCORE
				|| category == TOTAL){
			display.printMessage("不能选择这一栏，请重新选择");
			return false;
		}
		return true;
	}
	
	/** 计算骰子对选定分类的分数  
	 * 1： 所仍的骰子针对该类是否合法
	 * 2： 得分计算
	 * (1): 如果为SIXES-ONES 求筛子中出现的对应点数的盒
	 * (2): 如果是CHANCE 直接求和
	 * (3): 如果是THREE_OF_A_KIND，且满足所有骰子针对该类的条件，得分为所有骰子和，否则得分0
	 * (4): 如果是FOUR_OF_A_KIND ，且满足所有骰子针对该类的条件，得分为所有骰子和，否则得分0
	 * (5): 如果是FULL_HOUSE     ，且满足所有骰子针对该类的条件，得分25，否则得分0
	 * (6): 如果是SMALL_STRAIGHT ，且满足所有骰子针对该类的条件，得分30，否则得分0
	 * (7): 如果是LARGE_STRAIGHT ，且满足所有骰子针对该类的条件，得分40，否则得分0
	 * (8): 如果是YAHTZEE        ，且满足所有骰子针对该类的条件，得分50，否则得分0
	 * */
	private void calculteCategoryScore(int thePlay){
		boolean check = checkIfDicesValid();
		selectedCategories[thePlay][category] = 1;
		
		if((category <= SIXES && category >= ONES)){
			for(int i=0;i<N_DICE;i++){
				if(category == dice[i])
					categoriesScores[thePlay][category] += dice[i];
			}
		}
		
		if(category == CHANCE){
			categoriesScores[thePlay][category] = sum();
		}

		if(check){
			if(category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND){
				categoriesScores[thePlay][category] = sum();
			}else if(category == FULL_HOUSE){
				categoriesScores[thePlay][category] = 25;
			}else if(category == SMALL_STRAIGHT){
				categoriesScores[thePlay][category] = 30;
			}else if(category == LARGE_STRAIGHT){
				categoriesScores[thePlay][category] = 40;
			}else if(category == YAHTZEE){         // 这个bug很难受 最后一个条件也要判断啊 我说怎么这么多得分为50呢
				categoriesScores[thePlay][category] = 50;
			}
		}else{
			categoriesScores[thePlay][category] = 0;
		}
	}
	
	/** 计算骰子对该类是否合法
	 * 1：ONES-SIXES 还有 CHANCE 都合法
	 * 2：THREE_OF_A_KIND 5个骰子必须有一个数字出现3次
	 * 3: FOUR_OF_A_KIND 5个骰子必须有一个数字出现4次
	 * 4: FULL_HOUSE 5个骰子必须有一个数字出现3次并且另一个数字出现4次
	 * 5: SMALL_STRAIGHT 1-2-3-4   或者 2-3-4-5   或者 3-4-5-6
	 * 6: LARGE_STRAIGHT 1-2-3-4-5 或者 2-3-4-5-6
	 * 7: YAHTZEE 5个骰子必须有一个数字出现5次
	 * */
	private boolean checkIfDicesValid(){
		boolean check = false;
		
		if((category >= ONES && category <= SIXES) || category == CHANCE){
			check = true;
		}
		
		int[] dicetimes = new int[DICE_MAX]; // 每个骰子出现的次数
		for(int i=0;i<N_DICE;i++){
			dicetimes[dice[i]-1]++;
		}
		
		if(category == THREE_OF_A_KIND){
			for(int i=0;i<DICE_MAX;i++){
				if(dicetimes[i] >= 3){ // 判定条件只要大于3都成立啊 而非必须等于3
					check = true;
				}
			}
		}else if(category == FOUR_OF_A_KIND){
			for(int i=0;i<DICE_MAX;i++){
				if(dicetimes[i] >= 4){
					check = true;
					break;
				}
			}		
		}else if(category == FULL_HOUSE){
			for(int i=0;i<DICE_MAX;i++){
				if(dicetimes[i] == 3){
					for(int j=0;j<DICE_MAX;j++){
						if(dicetimes[j] == 2){
							check = true;
							break;					
						}
					}
				}
			}	
		}else if(category == SMALL_STRAIGHT){  //这里注意取了个巧 因为只有三种情况
			for(int i=0;i<3;i++){
				int temp = 1;
				for(int j=0;j<4;j++){
					temp *= dicetimes[i+j];
				}
				if(temp != 0){   //这个bug除了很久啊 不是等于1而是不等于零
					check = true;
					break;				
				}
			}
			
		}else if(category == LARGE_STRAIGHT){
			if(dicetimes[0] == 0){
				check = true;
				for(int i=1;i<DICE_MAX;i++){
					if(dicetimes[i] != 1){
						check = false;
						break;
					}
				}
			}
			if(dicetimes[5] == 0){
				check = true;
				for(int i=0;i<DICE_MAX-1;i++){
					if(dicetimes[i] != 1){
						check = false;
						break;
					}
				}
			}
		}else if(category == YAHTZEE){
			for(int i=0;i<DICE_MAX;i++){
				if(dicetimes[i] == 5){
					check = true;
					break;
				}
			}
		}
		
		return check;
	}
	
	private int sum(){
		int score = 0;
		for(int i=0;i<N_DICE;i++){
			score += dice[i];
		}
		return score;
	}
	
}
