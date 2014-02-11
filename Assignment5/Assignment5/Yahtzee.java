/*
 * �ļ�: Yahtzee.java
 * -------------------
 * ��飺��ͧ������Ϸ
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	private int nPlayers;
	private String[] playerNames;

	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
	
	private int[] dice; // ���ӵ���
	private int[][] categoriesScores; // ����÷�  
	private int[][] selectedCategories; // �����Ƿ�ѡ��
	private int category; //��ѡ�еķ���
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		initGame();
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
		endGame();
	}

	/** ��Ϸ��ʼ�� */
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
		categoriesScores = new int[nPlayers+1][N_CATEGORIES+1];     //���������1����Ȼ�˷���һ��ռ䣬���ǻ����˷���, ��һ��ܷ��㣡
		selectedCategories = new int[nPlayers+1][N_CATEGORIES+1];
	}
	
	/** ��Ϸ��ʼ */
	private void playGame() {
		for(int i=1;i<=N_SCORING_CATEGORIES;i++){
			for(int j=1;j<=nPlayers;j++){
				palyOneRound(j);
			}
		}
	}
	
	/**����Ϸ������ͳ���ܵ÷�������ж�ʤ��*/
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
		display.printMessage(playerNames[winners-1] + "��Ӯ�������յ�ʤ������ϲ��");
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
	
	/** ÿ�����ÿһ����Ϸ��Ҫ���еĲ��� 
	 * 1: ������  
	 * 2: ѡ�����滻����������������
	 * 3: ѡ�з���
	 * 4: ����÷ֲ���ʾ*/
	private void palyOneRound(int thePlay){
		display.printMessage(playerNames[thePlay-1] + "�ֵ�����������");
		display.waitForPlayerToClickRoll(thePlay);
		rollDice();
		display.displayDice(dice);
		
		for(int times=0;times<2;times++){
			display.printMessage(playerNames[thePlay-1] + "�㻹��������" + (2-times) + "�Σ���ѡ��Ҫ�Ե����Ӱ�");
			display.waitForPlayerToSelectDice();
		
			rollAgain(dice);
			display.displayDice(dice);
		}
		
		display.printMessage(playerNames[thePlay-1] + "��Ļ��������ˣ���ѡ��Ҫ�ŵ���һ��");
		
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
	
	//�����Լ�������
//	private void setDice(int i,int j){
//		dice[i] = j;
//	}
//	
	/** �ж��Ƿ����ѡ����� 
	 *1���Ƿ�ѡ��
	 *2���Ƿ�Ϊ��Ҫ��������Ǽ�������*/
	private boolean checkCategoryLegal(int thePlay){
		if(selectedCategories[thePlay][category] == 1){
			display.printMessage("��һ���Ѿ���ѡ���ˣ�������ѡ��");
			return false;
		}
		if(category == UPPER_SCORE || category == UPPER_BONUS
				|| category == LOWER_SCORE
				|| category == TOTAL){
			display.printMessage("����ѡ����һ����������ѡ��");
			return false;
		}
		return true;
	}
	
	/** �������Ӷ�ѡ������ķ���  
	 * 1�� ���Ե�������Ը����Ƿ�Ϸ�
	 * 2�� �÷ּ���
	 * (1): ���ΪSIXES-ONES ��ɸ���г��ֵĶ�Ӧ�����ĺ�
	 * (2): �����CHANCE ֱ�����
	 * (3): �����THREE_OF_A_KIND������������������Ը�����������÷�Ϊ�������Ӻͣ�����÷�0
	 * (4): �����FOUR_OF_A_KIND ������������������Ը�����������÷�Ϊ�������Ӻͣ�����÷�0
	 * (5): �����FULL_HOUSE     ������������������Ը�����������÷�25������÷�0
	 * (6): �����SMALL_STRAIGHT ������������������Ը�����������÷�30������÷�0
	 * (7): �����LARGE_STRAIGHT ������������������Ը�����������÷�40������÷�0
	 * (8): �����YAHTZEE        ������������������Ը�����������÷�50������÷�0
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
			}else if(category == YAHTZEE){         // ���bug������ ���һ������ҲҪ�жϰ� ��˵��ô��ô��÷�Ϊ50��
				categoriesScores[thePlay][category] = 50;
			}
		}else{
			categoriesScores[thePlay][category] = 0;
		}
	}
	
	/** �������ӶԸ����Ƿ�Ϸ�
	 * 1��ONES-SIXES ���� CHANCE ���Ϸ�
	 * 2��THREE_OF_A_KIND 5�����ӱ�����һ�����ֳ���3��
	 * 3: FOUR_OF_A_KIND 5�����ӱ�����һ�����ֳ���4��
	 * 4: FULL_HOUSE 5�����ӱ�����һ�����ֳ���3�β�����һ�����ֳ���4��
	 * 5: SMALL_STRAIGHT 1-2-3-4   ���� 2-3-4-5   ���� 3-4-5-6
	 * 6: LARGE_STRAIGHT 1-2-3-4-5 ���� 2-3-4-5-6
	 * 7: YAHTZEE 5�����ӱ�����һ�����ֳ���5��
	 * */
	private boolean checkIfDicesValid(){
		boolean check = false;
		
		if((category >= ONES && category <= SIXES) || category == CHANCE){
			check = true;
		}
		
		int[] dicetimes = new int[DICE_MAX]; // ÿ�����ӳ��ֵĴ���
		for(int i=0;i<N_DICE;i++){
			dicetimes[dice[i]-1]++;
		}
		
		if(category == THREE_OF_A_KIND){
			for(int i=0;i<DICE_MAX;i++){
				if(dicetimes[i] >= 3){ // �ж�����ֻҪ����3�������� ���Ǳ������3
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
		}else if(category == SMALL_STRAIGHT){  //����ע��ȡ�˸��� ��Ϊֻ���������
			for(int i=0;i<3;i++){
				int temp = 1;
				for(int j=0;j<4;j++){
					temp *= dicetimes[i+j];
				}
				if(temp != 0){   //���bug���˺ܾð� ���ǵ���1���ǲ�������
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
