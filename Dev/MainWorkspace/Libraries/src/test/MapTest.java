package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.lwan.util.containers.TrieMap;

public class MapTest {


	
	public static void main(String[] args) throws Exception {		
		TrieMap<Integer> map = new TrieMap<>();
		map.beginBulkUpdate();
//		HashMap<String, Integer> map = new HashMap<>();
		
		long start = System.currentTimeMillis();
		File f = new File("C:\\Users\\Lu\\Documents\\final");
		int count = 0;
		for (File c : f.listFiles()) {
			BufferedReader br = new BufferedReader(new FileReader(c));
			while (br.ready()) {
				String next = br.readLine();
				map.put(next, count++);
			}
			br.close();
//			if (count > 10000) {
//				break;
//			}
		}
		
		long end = System.currentTimeMillis();
		map.endBulkUpdate();
		
		long compressed = System.currentTimeMillis();
		
		compressed = compressed - end;
		end = end - start;
		
		System.out.println("end: " + end);
		System.out.println("compressed: " + compressed);
		System.out.println("total: " + (compressed + end));
		
//		System.out.println(count);

//		start = System.currentTimeMillis();
//		for (String text : test) {
//			 map.get(text);
////			System.out.println(result);
//		}
		
		end = System.currentTimeMillis();
		
		end = end - start;
		System.out.println("done: " + end);
		
		
		
		Thread.sleep(1000 * 20);
		
//		TrieMap<String> pt = new TrieMap<>();
//		
//
////		
//		for (int i = 0; i < strs.length; i++) {
//			pt.put(strs[i], strs[i]);
//		}
		
//		CollectionUtil.printV(pt.values(), "\n");
		
//		System.out.println(pt.getNearest("b"));
	}
	
}
