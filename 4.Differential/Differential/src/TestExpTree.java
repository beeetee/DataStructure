import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

public class TestExpTree {
	public static void main(String[] args) throws IOException, SyntaxError {		
		FileInputStream stream = new FileInputStream("input.txt");
		InputStreamReader reader = new InputStreamReader(stream);
		StreamTokenizer in  = new StreamTokenizer(reader);
		
		ExpTree tree = new ExpTree(in);
		while (true) {
			if(in.ttype == StreamTokenizer.TT_EOF)
				break;
			in.nextToken();
			in.pushBack();
			try {
				tree.buildExpTree();	// 표현 트리 생성
			}
			catch (SyntaxError e) {
				System.out.println("\n" + e.getMessage());
				System.exit(1);
			}
			System.out.println("Input in Expression Tree:");
			tree.print();
			
			System.out.println("Differentiated: ");
			tree.differential();			
			
			System.out.println("Simple: ");
			tree.simple();
			
			System.out.println();
		}
	}
}