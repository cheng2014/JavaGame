/*
 * 文件: Breakout.java
 * -------------------
 * 简介：打砖块小程序
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.event.*;

public class Breakout extends GraphicsProgram{

	/** 游戏面板大小*/
	private static final int WIDTH = 400;
	private static final int HEIGHT = 600;
	
	/** 游戏控制全局变量 */
	private int playTurn = 0;
	private static final int NTURNS = 3;
	private static final int PAUSE_TIME = 10; //设置小一点是否会解除那个有时候碰不到转快的bug
	private static final int SCORE_WIDTH = 100;
	private static final int SCORE_HEIGHT = 50;
	private Score score = new Score(SCORE_WIDTH, SCORE_HEIGHT, 0);  //计分板

	/** 游戏声效*/
	AudioClip gameBeginClip = MediaTools.loadAudioClip("begin.au");
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce3.au");
	AudioClip nextTurnClip = MediaTools.loadAudioClip("nextTurn.au");
	AudioClip youWinClip = MediaTools.loadAudioClip("youwin.au");
	AudioClip gameOverClip = MediaTools.loadAudioClip("gameover.au");

	/** 挡板相关 */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;
	private static final int PADDLE_Y_OFFSET = 30;
	private static final int PADDLE_VX = 2; //键盘控制速度
	private GRect paddle;

	/** 砖块相关 */
	//避免出问题，最好所有全局变量都放到内部 等画布设置OK之后使用getWidth()来获取
	private static final int NBRICKS_PER_ROW = 10;
	private static final int NBRICK_ROWS = 10;
	private static final int BRICK_SEP = 4;
//	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW; 	//疑问保留1：为什么视图变小了？ 
	private static final int BRICK_HEIGHT = 8;
	private static final int BRICK_Y_OFFSET = 70;
	private static final int BRICK_NUM = NBRICKS_PER_ROW * NBRICK_ROWS;
	private GRect brick;
	private int hitBricks = 0; // 成功撞击砖块的个数
	
	
	/** 球相关 */
	private static final int BALL_RADIUS = 10;
	private static final double BALL_VX = 2;
	private static final double BALL_VY_MAX = 4;
	private static final double BALL_VY_MIN = 2;
	private GOval ball;
	private double ball_vx;
	private double ball_vy;

	private static final String FONTSTYLE = "SansSerif-35";
	private GLabel message;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	public void run(){
		createGameInterface();
		gameBegin();
	}

	private void createGameInterface(){
		setBackGround();
		createBricks();
		createPaddle();
		showMessage("单击鼠标，游戏开始");
	}
	
	/** 设置桌布 */
	private void setBackGround(){
		setSize(WIDTH, HEIGHT);
		setBackground(Color.GRAY);
	}
	
	
	/** 创建砖块 */
	private void createBricks(){
		int BRICK_WIDTH = (getWidth() - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;
		for(int i=0;i<NBRICKS_PER_ROW;i++){
			int y = i*(BRICK_HEIGHT+BRICK_SEP)+BRICK_Y_OFFSET;
			for(int j=0;j<NBRICK_ROWS;j++){
				int x = j*(BRICK_WIDTH+BRICK_SEP);
				brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
				brick.setFilled(true);
				setBrickColor(i);
				add(brick);
			}
		}	
	}
	
	//错误1：居然把入参传成了 y 而非 i
	private void setBrickColor(int row){
		if(row < 2){
			brick.setFillColor(Color.RED);
		}else if(row < 4){
			brick.setFillColor(Color.ORANGE);
		}else if(row < 6){
			brick.setFillColor(Color.YELLOW);
		}else if(row < 8){
			brick.setFillColor(Color.GREEN);
		}else{
			brick.setFillColor(Color.CYAN);
		}
	}
	
	/** 创建挡板 */
	private void createPaddle(){
		int x = getWidth()/2-PADDLE_WIDTH/2;
		int y = getHeight()-PADDLE_Y_OFFSET-PADDLE_HEIGHT;
		paddle = new GRect(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		
		addMouseListeners();
		addKeyListeners();
	}
	
	public void mouseDragged(MouseEvent e){
		if(e.getX() <= 0 || e.getX()+paddle.getWidth() >= getWidth()){
			return;
		}
		
		double vx = e.getX() - paddle.getX();
		paddle.move(vx, 0);
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();

		if(key == KeyEvent.VK_LEFT){
			while(paddle.getX()<=0) return;
			paddle.move(-PADDLE_VX, 0);
		}
		if(key == KeyEvent.VK_RIGHT){
			if((paddle.getX()+paddle.getWidth())>=getWidth()) return;
			paddle.move(PADDLE_VX, 0);
		}		
	}
	
	/** 设置提示信息 */
	private void showMessage(String string){
		message = new GLabel(string);
		message.setFont(FONTSTYLE);
		add(message,getWidth()/2-message.getWidth()/2,getHeight()/2-message.getAscent()/2);
	}
	
	
	/** 游戏开始 */
	private void gameBegin(){
		waitForClick();
		
		gameBeginClip.play();
		remove(message);
		score = new Score(50,30,hitBricks);
		add(score,0,0);
		
		while(true){
			createBall();
			getBallVelocity();
			moveBall();
			remove(ball);
			if(hitBricks == BRICK_NUM){
				removeAll();
				youWinClip.play();
				setBackground(Color.RED);
				showMessage("恭喜，你赢了");
				break;
			}
			if(playTurn == NTURNS){
				gameOverClip.play();
				showMessage("你已经没有机会了");
				break;
			}
			nextTurnClip.play();
		}
		
	}
	
	/** 创建撞球 */
	private void createBall(){
		ball = new GOval(BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setFillColor(Color.PINK);
		double x = paddle.getX()+paddle.getWidth()/2-BALL_RADIUS;
		double y = paddle.getY()-BALL_RADIUS*2;
		add(ball,x,y);
	}

	/** 获取撞球初速度 */
	private void getBallVelocity(){
		ball_vx = BALL_VX;
		if(rgen.nextBoolean(0.5)) ball_vx *= -1;
		ball_vy = -rgen.nextDouble(BALL_VY_MIN, BALL_VY_MAX);
	}
	
	/** 撞球运行 */
	private void moveBall(){
		do{
			ball.move(ball_vx, ball_vy);
			pause(PAUSE_TIME);
		}while(checkIfContinue());
	}
	
	/** 撞球是否继续移动 
	 * 1：砖块清空结束运行
	 * 2：碰到桌布底部结束运行
	 * 3：是否撞到障碍物改变方向*/
	private boolean checkIfContinue(){
		if(hitBricks == BRICK_NUM){
			return false;
		}
		
		if(ball.getY()+ball.getHeight()>=getHeight()){
			playTurn++;
			return false;
		}
		
		checkForCollision();
		return true;
	}
	
	/** 撞到障碍物改变方向
	 * 1：碰到三面墙壁
	 * 2：碰到砖块
	 * 2：碰到挡板 */
	private void checkForCollision(){
		if(ball.getX()<=0 || ball.getX()+ball.getWidth()>=getWidth()){
			ball_vx *= -1;
		}

		if(ball.getY()<=0){
			ball_vy *= -1;
		}
		
		GObject collider  = hitWhat();
		if(collider  == null || collider == score){
			return;
		}else if(collider  == paddle){
			ball_vy *= -1;
		}else{
			hitBricks++;
			
			//得分超过20就加速
			if(hitBricks%20==0 && hitBricks!=0){
				ball_vx *= 1.25;
				ball_vy *= 1.25;
				nextTurnClip.play();
			}else{
				bounceClip.play();				
			}
			score.setScore(hitBricks);
			
			ball_vy *= -1;
			remove(collider);
		}
 
		return;
	}
	
	/** 获取球碰到的东西 */
	private GObject hitWhat(){
		GObject collider;
		double x;
		double y;
		
		x = ball.getX();
		y = ball.getY();
		collider = getElementAt(x,y);
		if(collider != null){
			return collider;
		}
		
		x = ball.getX()+ball.getWidth();
		y = ball.getY()+ball.getHeight();
		collider = getElementAt(x,y);
		if(collider != null){
			return collider;
		}
		
		x = ball.getX();
		y = ball.getY()+ball.getHeight();
		collider = getElementAt(x,y);
		if(collider != null){
			return collider;
		}
		
		x = ball.getX()+ball.getWidth();
		y = ball.getY();
		collider = getElementAt(x,y);
		if(collider != null){
			return collider;
		}
		
		return null;
	}

}


