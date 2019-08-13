import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class TestSparseMatrix {
	public static void main(String[] args)throws FileNotFoundException  {
		int row, col;
		// ù ��° ���
		Scanner in = new Scanner(new File("input1.txt"));

		row = in.nextInt();
		col = in.nextInt();

		SparseMatrix matrixRow1 = new SparseMatrix(row,col);
		SparseMatrix matrixCol1 = new SparseMatrix(col,row);

		while(in.hasNext()){
			int r = in.nextInt();
			int c = in.nextInt();
			int value = in.nextInt();
			// �Է¹��� ������ ����
			matrixRow1.set(r, c, value);
			matrixCol1.set(c, r, value);
		}

		// �迭 ���·� ���
		System.out.println("input1.txt");
		System.out.println("----------");
		System.out.println("Sparse Matrix by Rows");
		matrixRow1.print();

		System.out.println("\nSparse Matrix by Colummns");
		matrixCol1.print();
				
		// 2���� ���
		in = new Scanner(new File("input2.txt"));

		row = in.nextInt();
		col = in.nextInt();

		SparseMatrix matrixRow2 = new SparseMatrix(row,col);
		SparseMatrix matrixCol2 = new SparseMatrix(col,row);

		while(in.hasNext()){
			int r = in.nextInt();
			int c = in.nextInt();
			int value = in.nextInt();
			// �Է¹��� ������ ����
			matrixRow2.set(r, c, value);
			matrixCol2.set(c, r, value);
		}

		// �迭�� ���·� ���
		System.out.println("\ninput2.txt");
		System.out.println("----------");
		System.out.println("Sparse Matrix by Rows");
		matrixRow2.print();

		System.out.println("\nSparse Matrix by Colummns");
		matrixCol2.print();
		
		// ù����, �ι�° ����� ���� ����� ���
		System.out.println("\nMultiplication");
		System.out.println("--------------");
		System.out.println("Sparse Matrix by Rows");
		matrixRow1.multiplication(matrixRow2);
		System.out.println("Sparse Matrix by Colummns");
		matrixCol2.multiplication(matrixCol1);

		in.close();
	}
}
