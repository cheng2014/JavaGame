/*
 * �ļ�: NameSurfer.java
 * -------------------
 * ��飺�����������Ʋ�ѯ
 * 
 * ���Բ������չ��
 * 1�����delete��ť
 * 2��label ���ܷ��ã���Ҫ��������ǩ�����ص�
 * 3�����ڱ仯ʱ���Ե�������
 * 4���� table ��ť���ڻ�����һ�����һ��consoleprogram�����������
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
	
	/** �ؼ����*/
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
