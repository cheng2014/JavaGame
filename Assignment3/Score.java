/*
 * 文件: Score.java
 * -------------------
 * 简介：计分板 - 打砖块小程序子文件
 */

import acm.graphics.*;

/** 设置积分板*/
public class Score extends GCompound{
	private GLabel label;
	private GRect outline;
	
	public Score(int width, int height, int score){
		label = new GLabel(Integer.toString(score));
		label.setFont("ansSerif-15");
		outline = new GRect(0,0,width,height);
		add(outline);
		double x = outline.getWidth()/2-label.getWidth()/2;
		double y = outline.getHeight()/2+label.getAscent()/2;
		add(label,x,y);
	}
	
	public void setScore(int score){
		label.setLabel(Integer.toString(score));
		double x = outline.getWidth()/2-label.getWidth()/2;
		double y = outline.getHeight()/2+label.getAscent()/2;
		label.setLocation(x, y);
	}
}