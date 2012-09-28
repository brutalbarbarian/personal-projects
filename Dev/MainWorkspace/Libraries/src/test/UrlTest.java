package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;

public class UrlTest {
	public static void main (String[] args) throws IOException, ParseException {
		
		URL g = new URL ("http://fanfiction.portkey.org/index.php?act=read&storyid=7464&chapterid=2&agree=1");
		BufferedReader in = new BufferedReader (new InputStreamReader(g.openStream()));
		String inputLine;
		
		StringBuilder buffer = new StringBuilder();
		boolean startFound = false;
		boolean endFound = false;
		boolean hasNextChapter = false;
		String chapterName = null;
		
		while ((inputLine = in.readLine()) != null) {
			if(!startFound) {
				if (inputLine.contains("[Report this story to the admins]")) startFound = true;
				if (inputLine.contains("<select name=\"select5\" onChange=\"MM_jumpMenu('parent',this,0)\" class=\"boxedsmall\" >")) chapterName = findChapterName(g, inputLine);
				
				continue;
			}
			if (!endFound) {
				if(inputLine.contains("<address>")) {
					endFound = true;
					continue;
				}
				buffer.append(process(inputLine));
			} else {
				if (inputLine.contains("next &gt;&gt;")) {
					hasNextChapter = true;
					break;
				}
			}
		}
		in.close();
		System.out.println(buffer.toString().trim());
		System.out.println("hasNextChapter: " + hasNextChapter);
		System.out.println("chapterName: " + chapterName);
	}
	
	private static int findChapterNumber(URL g) {
		String u = g.toString();
		u = u.substring(u.indexOf("chapterid")+10);
		if (u.indexOf('&') != -1) {
			u = u.substring(0, u.indexOf('&'));
		}
		return Integer.parseInt(u);
	}
	
	private static String findChapterName(URL g, String inputLine) {
		int chap = findChapterNumber(g);
		String[] chaps = inputLine.split("</option>");
		String s = chaps[chap-1];
		return s.substring(s.lastIndexOf('>')+1);
	}

	static String process (String input) {
		if (input.contains("</p>")) {
			return removeTags(input) + "\n";
		} else {
			return removeTags(input);
		}
	}
	
	static String removeTags (String input) {
		return input.replaceAll("<[a-zA-Z0-9\"/=?\\!@#$%^&*\\s]*>", "").replaceAll("(&rdquo;|&ldquo;)", "\"").replaceAll("(&hellip;)", "...").trim();
	}
}
