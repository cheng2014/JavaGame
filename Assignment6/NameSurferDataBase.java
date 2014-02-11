/*
 * �ļ�: NameSurferDataBase.java
 * -------------------
 * ��飺���ݿ���� - �����������Ʋ�ѯ
 * 		
 * ���ļ��ж����������ݣ���������������Ӧ��������
 * �ݣ����������ִ�Сд
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import acm.util.ErrorException;


public class NameSurferDataBase implements NameSurferConstants {
	
	private HashMap<String,NameSurferEntry> database = new HashMap<String,NameSurferEntry>();
	
	/** �����ͳ�ʼ�����ݿ� */
	public NameSurferDataBase(String filename) {
		try{
			BufferedReader rd = new BufferedReader(new FileReader(filename));
			while(true){
				String line = rd.readLine();
				if(line == null) break;
				NameSurferEntry entry = new NameSurferEntry(line);
				database.put(entry.getName(),entry);
			}
			rd.close();
		}catch(IOException ex){
			throw new ErrorException(ex);
		}
	}
	
	/** �����ݿ��м���ָ������������ */
	public NameSurferEntry findEntry(String name) {
		name = changeFormat(name);           
		return database.get(name);
	}
	
	/** �����ݿ������е�������һ����ĸ��д������ΪСд��Ҫ��
	 *  ��ѯʱ�����ִ�Сд������ÿ�����������ת�������ݿ���
	 *  �ĸ�ʽ */
	private String changeFormat(String before){
		String after = "";
		after += Character.toUpperCase(before.charAt(0));
		after += before.substring(1).toLowerCase();
//		after.concat("cheng");  // ����concat��ô��������
		return after;
	}
}

