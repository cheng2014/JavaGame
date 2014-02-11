/*
 * �ļ�: NameSurferEntry.java
 * -------------------
 * ��飺��һ�ֶι��� - �����������Ʋ�ѯ
 * ʵ����Abdullah 0 0 0 0 0 0 0 0 0 1000 863
 */

import java.util.*;

public class NameSurferEntry implements NameSurferConstants {

	private String name;
	private ArrayList<String> ranks = new ArrayList<String>();
	
	public NameSurferEntry(String line) {
		StringTokenizer tokens = new StringTokenizer(line);
		name = tokens.nextToken();
		while(tokens.hasMoreTokens()){
			ranks.add(tokens.nextToken());
		}
	}

	public String getName() {
		return name;
	}

	public int getRank(int decade) {
		return Integer.parseInt(ranks.get(decade));
	}

	/** toString �������б�Ҫ���� */
	public String toString() {
		String result = name + " [ ";
		for(int i=0;i<ranks.size();i++){
			result += (ranks.get(i) + " ");
		}
		return result + "]";
	}
	
}

