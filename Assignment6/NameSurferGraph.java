/*
 * �ļ�: NameSurferGraph.java
 * -------------------
 * ��飺�������� - �����������Ʋ�ѯ
 */

import acm.graphics.*;
import java.awt.Color;
import java.awt.event.*;
import java.util.*;

public class NameSurferGraph extends GCanvas
	implements NameSurferConstants, ComponentListener {
	
	private HashMap<String,NameSurferEntry> selectEntry = new HashMap<String,NameSurferEntry>();   //Ŀǰ���������Ҫ��ʾ���ֶ�����
	private Color color;

	public NameSurferGraph() {
		addComponentListener(this);
	}
	
	/** update�����Ƴ����������е��������ػ�
	 * 	��clear / addEntry ���������Լ��ı�������Сʱ����Ҫ���ø÷���
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
	
	/**  ��ʾһ�����ֶ� */
	public void addEntry(NameSurferEntry entry) {
		if(entry != null){							// ����Ҫע���ж��Ƿ�Ϊ�յ����
			selectEntry.put(entry.getName(),entry);
			update();	
		}
	}

	/** �������汳�������������ͱ�ǩ*/
	public void createBackground(){
		createLines();
		createLabels();
	}
	
	/** ����11������2������*/
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
	
	/** ������ѡ��������ֶβ��Բ�ͬ����ɫ������*/
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
			if(y0 == 0)	y0 = getHeight() - GRAPH_MARGIN_SIZE;   // ����1��� 1000��� 0��ʾû�н���ǰ1000����������
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
//			Color color = rgen.nextColor();  // �����ɫ��һֱ�仯 ���ÿ� �������������һЩ
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
	
	/** Ӧ��ComponentListener�ӿ� */
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { update(); } //�����������䶯ʱ�����update()����
	public void componentShown(ComponentEvent e) { }

}
