import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class TestSparseMatrix2 {
public static void main(String[] args)throws FileNotFoundException  
{
		int row, col;

		Scanner in = new Scanner(new File("input3.txt"));

		row = in.nextInt();
		col = in.nextInt();

		SparseMatrix matrix = new SparseMatrix(row,col);

		while(in.hasNext()){
			int r = in.nextInt();
			int c = in.nextInt();
			int value = in.nextInt();
			matrix.set(r, c, value);
		}

		System.out.println("input3.txt");
		System.out.println("----------");
		System.out.println("Sparse Matrix");
		matrix.print();
	
		System.out.println("\nNode에 연결된 값에 대한 정보");
		System.out.println("-------------------");
		matrix.out();
		
		in.close();
	}
}
