import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import com.lwan.util.CollectionUtil;


public class SourceCodeCount implements FileVisitor<Path>{
	public SourceCodeCount() {
		count = 0;
	}
	
	long count;
	
	public void run() throws IOException {
		String root = "C:\\Users\\Lu\\Git\\Dev\\MainWorkspace";
		Path p = Paths.get(root);
		
		Files.walkFileTree(p, this);
		System.out.println("Total: " + count + " lines");
	}

	
	public static void main(String[] args) throws IOException {
		new SourceCodeCount().run();
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		return FileVisitResult.CONTINUE;	
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		if (file.toString().endsWith(".java")) {
			long lines = Files.readAllLines(file, Charset.defaultCharset()).size();
			System.out.println(file.getFileName() + ": " + lines + " lines");
			count += lines;
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	
}
