/*
 * �ļ�: Breakout.java
 * -------------------
 * ��飺��ש��С����
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.event.*;

public class Breakout extends GraphicsProgram{

	/** ��Ϸ����С*/
	private static final int WIDTH = 400;
	private static final int HEIGHT = 600;
	
	/** ��Ϸ����ȫ�ֱ��� */
	private int playTurn = 0;
	private static final int NTURNS = 3;
	private static final int PAUSE_TIME = 10; //����Сһ���Ƿ�����Ǹ���ʱ��������ת���bug
	private static final int SCORE_WIDTH = 100;
	private static final int SCORE_HEIGHT = 50;
	private Score score = new Score(SCORE_WIDTH, SCORE_HEIGHT, 0);  //�Ʒְ�

	/** ��Ϸ��Ч*/
	AudioClip gameBeginClip = MediaTools.loadAudioClip("begin.au");
	AudioClip bounceClip = MediaTools.loadAudioClip("bounce3.au");
	AudioClip nextTurnClip = MediaTools.loadAudioClip("nextTurn.au");
	AudioClip youWinClip = MediaTools.loadAudioClip("youwin.au");
	AudioClip gameOverClip = MediaTools.loadAudioClip("gameover.au");

	/** ������� */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;
	private static final int PADDLE_Y_OFFSET = 30;
	private static final int PADDLE_VX = 2; //���̿����ٶ�
	private GRect paddle;

	/** ש����� */
	//��������⣬�������ȫ�ֱ������ŵ��ڲ� �Ȼ�������OK֮��ʹ��getWidth()����ȡ
	private static final int NBRICKS_PER_ROW = 10;
	private static final int NBRICK_ROWS = 10;
	private static final int BRICK_SEP = 4;
//	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW; 	//���ʱ���1��Ϊʲô��ͼ��С�ˣ� 
	private static final int BRICK_HEIGHT = 8;
	private static final int BRICK_Y_OFFSET = 70;
	private static final int BRICK_NUM = NBRICKS_PER_ROW * NBRICK_ROWS;
	private GRect brick;
	private int hitBricks = 0; // �ɹ�ײ��ש��ĸ���
	
	
	/** ����� */
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
		showMessage("������꣬��Ϸ��ʼ");
	}
	
	/** �������� */
	private void setBackGround(){
		setSize(WIDTH, HEIGHT);
		setBackground(Color.GRAY);
	}
	
	
	/** ����ש�� */
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
	
	//����1����Ȼ����δ����� y ���� i
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
	
	/** �������� */
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
	
	/** ������ʾ��Ϣ */
	private void showMessage(String string){
		message = new GLabel(string);
		message.setFont(FONTSTYLE);
		add(message,getWidth()/2-message.getWidth()/2,getHeight()/2-message.getAscent()/2);
	}
	
	
	/** ��Ϸ��ʼ */
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
				showMessage("��ϲ����Ӯ��");
				break;
			}
			if(playTurn == NTURNS){
				gameOverClip.play();
				showMessage("���Ѿ�û�л�����");
				break;
			}
			nextTurnClip.play();
		}
		
	}
	
	/** ����ײ�� */
	private void createBall(){
		ball = new GOval(BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		ball.setFillColor(Color.PINK);
		double x = paddle.getX()+paddle.getWidth()/2-BALL_RADIUS;
		double y = paddle.getY()-BALL_RADIUS*2;
		add(ball,x,y);
	}

	/** ��ȡײ����ٶ� */
	private void getBallVelocity(){
		ball_vx = BALL_VX;
		if(rgen.nextBoolean(0.5)) ball_vx *= -1;
		ball_vy = -rgen.nextDouble(BALL_VY_MIN, BALL_VY_MAX);
	}
	
	/** ײ������ */
	private void moveBall(){
		do{
			ball.move(ball_vx, ball_vy);
			pause(PAUSE_TIME);
		}while(checkIfContinue());
	}
	
	/** ײ���Ƿ�����ƶ� 
	 * 1��ש����ս�������
	 * 2�����������ײ���������
	 * 3���Ƿ�ײ���ϰ���ı䷽��*/
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
	
	/** ײ���ϰ���ı䷽��
	 * 1����������ǽ��
	 * 2������ש��
	 * 2���������� */
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
			
			//�÷ֳ���20�ͼ���
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
	
	/** ��ȡ�������Ķ��� */
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


