/*
 * 文件: NameSurfer.java
 * -------------------
 * 简介：姓名流行趋势查询
 * 
 * 可以补充的扩展：
 * 1：添加delete按钮
 * 2：label 智能放置，不要和其他标签或线重叠
 * 3：窗口变化时可以调整字体
 * 4：加 table 按钮，在画布另一侧添加一个consoleprogram用于输出数据
 */

import acm.program.*;
import java.awt.event.*;
import javax.swing.*;

public class NameSurfer extends Program implements NameSurferConstants {

	private JTextField namefield;
	private JButton button_graph;
	private JButton button_clear;
	
	private NameSurferGraph graph;
	private NameSurferDataBase database;
	private NameSurferEntry entry;
	
	public void init() {
		createCtronllers();
		
		database = new NameSurferDataBase(NAMES_DATA_FILE);
		graph = new NameSurferGraph();
		add(graph);
	}
	
	/** 控件添加*/
	private void createCtronllers(){
		namefield = new JTextField(TESTFIELD_NUM);
		button_graph = new JButton("Graph");
		button_clear = new JButton("Clear");

		add(new JLabel("Name "),SOUTH);
		add(namefield,SOUTH);
		add(button_graph,SOUTH);
		add(button_clear,SOUTH);
		
		namefield.addActionListener(this);
		addActionListeners();
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == namefield || obj == button_graph){
			entry = database.findEntry(namefield.getText());
			graph.addEntry(entry);
		}
		if(obj == button_clear){
			graph.clear();
		}
	}
}
