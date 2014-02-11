/*
 * 文件: NameSurferDataBase.java
 * -------------------
 * 简介：数据库管理 - 姓名流行趋势查询
 * 		
 * 从文件中读入整个数据，根据姓名检索对应的排名数
 * 据，姓名不区分大小写
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import acm.util.ErrorException;


public class NameSurferDataBase implements NameSurferConstants {
	
	private HashMap<String,NameSurferEntry> database = new HashMap<String,NameSurferEntry>();
	
	/** 创建和初始化数据库 */
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
	
	/** 在数据库中检索指定姓名的数据 */
	public NameSurferEntry findEntry(String name) {
		name = changeFormat(name);           
		return database.get(name);
	}
	
	/** 看数据库中所有的人名第一个字母大写，其他为小写，要求
	 *  查询时不区分大小写，所以每个输入的姓名转换成数据库中
	 *  的格式 */
	private String changeFormat(String before){
		String after = "";
		after += Character.toUpperCase(before.charAt(0));
		after += before.substring(1).toLowerCase();
//		after.concat("cheng");  // 这里concat怎么不能用呢
		return after;
	}
}

