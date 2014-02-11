/*
 * 文件: NameSurferGraph.java
 * -------------------
 * 简介：画布管理 - 姓名流行趋势查询
 */

import acm.graphics.*;
import java.awt.Color;
import java.awt.event.*;
import java.util.*;

public class NameSurferGraph extends GCanvas
	implements NameSurferConstants, ComponentListener {
	
	private HashMap<String,NameSurferEntry> selectEntry = new HashMap<String,NameSurferEntry>();   //目前已输入的需要显示的字段姓名
	private Color color;

	public NameSurferGraph() {
		addComponentListener(this);
	}
	
	/** update程序移除画布上所有的数据再重画
	 * 	当clear / addEntry 方法调用以及改变桌布大小时均需要调用该方法
	 * */
	public void update() {
		removeAll();
		createBackground();
		createEntry();
	}

	public void clear() {
		selectEntry.clear();
		update();
	}
	
	/**  显示一个新字段 */
	public void addEntry(NameSurferEntry entry) {
		if(entry != null){							// 这里要注意判定是否为空的情况
			selectEntry.put(entry.getName(),entry);
			update();	
		}
	}

	/** 画出桌面背景，包括线条和标签*/
	public void createBackground(){
		createLines();
		createLabels();
	}
	
	/** 包括11条竖线2条横线*/
	public void createLines(){
		double space = getWidth()/NDECADES;
		double x0 = 0;
		double x1 = 0;
		double y0 = 0;
		double y1 = getHeight();
		for(int i=0;i<NDECADES;i++){
			x0 = i*space;
			x1 = x0;
			GLine line = new GLine(x0,y0,x1,y1);
			add(line);
		}
		
		x0 = 0;
		x1 = getWidth();
		y1 = y0 = GRAPH_MARGIN_SIZE;
		GLine line = new GLine(x0,y0,x1,y1);
		add(line);
		
		y1 = y0 = getHeight() - GRAPH_MARGIN_SIZE; 
		line = new GLine(x0,y0,x1,y1);
		add(line);
	}
	
	public void createLabels(){
		double space = getWidth()/NDECADES;
		double y = getHeight(); 
		for(int i=0;i<NDECADES;i++){
			double x = space * i;
			int decade = START_DECADE + 10*i;
			add(new GLabel(Integer.toString(decade)),x,y);
		}
	}
	
	/** 遍历已选择的所有字段并以不同的颜色画出来*/
	public void createEntry(){
		Iterator<String> iterator = selectEntry.keySet().iterator();
		int i = 0;
		while(iterator.hasNext()){
			controlColor(i++);
			addEntryLines(selectEntry.get(iterator.next()));
		}
	}
	
	private void addEntryLines(NameSurferEntry entry){
		double sp = getWidth()/NDECADES;
		double height = getHeight() - 2*GRAPH_MARGIN_SIZE;
		for(int i=0;i<NDECADES-1;i++){
			double x0 = i*sp;
			double y0 = height - entry.getRank(i)*height/MAX_RANK;
			double x1 = (i+1)*sp;
			double y1 = height - entry.getRank(i+1)*height/MAX_RANK;
			if(y0 == 0)	y0 = getHeight() - GRAPH_MARGIN_SIZE;   // 排名1最高 1000最低 0表示没有进入前1000所以最下面
			if(y1 == 0) y1 = getHeight() - GRAPH_MARGIN_SIZE;
			GLine line = new GLine(x0,y0,x1,y1);
			line.setColor(color);
			add(line);
			
			String labelText = entry.getName() + " ";
			if(entry.getRank(i) == 0){
				labelText += "*"; 
			}else{
				labelText += entry.getRank(i); 
			}
			GLabel label = new GLabel(labelText);
			label.setColor(color);
			add(label,x0,y0+10);
//			Color color = rgen.nextColor();  // 随机颜色会一直变化 不好看 给他放置数组简单一些
		}
	}
	
	private void controlColor(int i){
		if(i % 4 == 0){
			color = Color.BLACK;
		}else if(i % 4 == 1){
			color = Color.RED;
		}else if(i % 4 == 2){
			color = Color.BLUE;
		}else{
			color = Color.MAGENTA;
		}
	}
	
	/** 应用ComponentListener接口 */
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { update(); } //当窗口随鼠标变动时会调用update()方法
	public void componentShown(ComponentEvent e) { }

}
