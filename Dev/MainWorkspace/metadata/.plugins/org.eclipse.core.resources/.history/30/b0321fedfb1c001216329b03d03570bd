package test;

import java.io.IOException;

import javax.swing.JFrame;

import javazoom.jl.decoder.JavaLayerException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

import com.lwan.util.sound.Mp3File;

public class ID3 {
	public static void main (String[] args) throws IOException, TagException, JavaLayerException {
		MP3File file = new MP3File("song.mp3");
		ID3v1 id1 = file.getID3v1Tag();
		System.out.println(id1.getArtist());
		System.out.println(id1.getAlbumTitle());
		System.out.println(id1.getSongTitle());
		System.out.println(id1.getLeadArtist());
		
		System.out.println("----");
		AbstractID3v2 id2 = file.getID3v2Tag();
		System.out.println(id2.getAlbumTitle());
		System.out.println(id2.getFrameCount());
		
		System.out.println("----");
		
		Mp3File m = new Mp3File("song.mp3");
		m.createClip().play();
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
