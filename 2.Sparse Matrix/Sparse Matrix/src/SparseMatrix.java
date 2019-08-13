class SparseMatrix {
	private Node[] rows;
	private Node[] cols;

	public SparseMatrix(int row, int col) {	// ������
		rows = new Node[row];
		cols = new Node[col];
	}

	/** 
	 * @param row ������ ���� �� ��ġ
	 * @param col ������ ���� �� ��ġ
	 * @param value ������ ��
	 */
	public void set(int row, int col, int value){		

		Node newNode = new Node(row,col,value);
		Matrix matrix = new Matrix(rows, cols);
		
		// rows�� cols�� ����ִٸ� row,col,value���� 
		// �״�� ������ ��带 �����Ѵ�.
		if(rows[row] == null && cols[col] == null) { 
			rows[row] = newNode;
			cols[col] = newNode;
		}

		// cols�� ����ִٸ� newNode�� �ٷ� ����Ű�� �ϰ�
		// rows�� �̹� ����Ǿ��ִٸ�, 
		// �ش� ��ġ�� rows�� col���� ���Ͽ� �־��ش�.
		else if(rows[row]!=null && cols[col]==null){
			cols[col] = newNode;
			matrix.linkCols(newNode);
		}

		// rows�� ����ִٸ� newNode�� �ٷ� ����Ű�� �ϰ�
		// cols�� �̹� ����Ǿ��ִٸ�, 
		// �ش� ��ġ�� cols�� row���� ���Ͽ� �־��ش�.
		else if(rows[row]==null && cols[col]!=null){
			rows[row] = newNode;
			matrix.linkRows(newNode);
		}

		// ��,���� �̹� ����Ǿ� �ִ� ��尡 ������ ��
		// �� �� ���� ���ذ��� �������ش�.
		else{
			matrix.linkCols(newNode);
			matrix.linkRows(newNode);
		}
		// ���� 0�ΰ�� �������ش�.
		if(value==0)
			matrix.remove(row, col);
	}

	/**
	 * ����� ���� �����ϴ� �޼ҵ�
	 * @param matrix ���� ���
	 */
	public void multiplication(SparseMatrix matrix){
		int n;
		int[][] multi = new int[rows.length][matrix.cols.length];
		Node temp1;
		Node temp2;
		for(int i=0;i<rows.length;i++){
			for(int j=0;j<rows.length;j++){
				n=0;
				temp1 = this.rows[i];
				temp2 = matrix.cols[j];
				while(temp1 != null && temp2 != null){
					if(temp1.col>temp2.row)
						temp2=temp2.nextRow;
					else if(temp1.col<temp2.row)
						temp1=temp1.nextCol;
					else{
						n += temp1.value*temp2.value;
						temp1=temp1.nextCol;
						temp2=temp2.nextRow;
					}
				}
				multi[i][j]=n;
			}
		}
		System.out.printf("%-5s","");
		for(int i=0; i<multi[0].length;i++){
			System.out.printf("%-5s","["+i+"] ");
		}
		System.out.println();

		for(int i=0;i<multi.length;i++){
			System.out.printf("%-5s","["+i+"] ");
			for(int j=0;j<multi[i].length;j++){

				System.out.printf("%-5s",multi[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * ����� ������ �迭�� ���·� ������ִ� �޼ҵ�
	 */
	public void print(){
		Node temp;
		System.out.printf("%-5s","");
		// ���� ���� ���
		for(int i=0; i<cols.length;i++){
			System.out.printf("%-5s","["+i+"] ");
		}
		System.out.println();

		for(int i=0; i<rows.length;i++){
			// ���� ���� ���
			System.out.printf("%-5s","["+i+"] ");
			if(rows[i]!=null){
				temp=rows[i];
				// rows�� ����� ��� ���� ��ĵ
				for(int j=0;j<cols.length;j++){
					// ����Ǿ� �ִ� col���̶�� 
					// �� value�� ������ش�.
					if(temp.col==j){
						System.out.printf("%-5s",temp.value);
						if(temp.nextCol != null)
							temp = temp.nextCol;
					}
					// ����Ǿ����� ���� cols��� 0�� ������ش�.
					else
						System.out.printf("%-5s",0);
				}
			}
			// ����Ǿ����� ���� rows��� 0�� ������ش�.
			else
				for(int j=0;j<cols.length;j++){
					System.out.printf("%-5s",0);
				}
			System.out.println();
		}
	}
	/**
	 * ����� ������ ������ִ� �޼ҵ�
	 */
	public void out(){
		Node temp;
		for(int i=0;i<rows.length;i++){
			temp = rows[i];
			System.out.print(i+"��° ���� ��忡 ����� ��: ");
			while(temp != null){
				System.out.print(temp.value+" ");
				temp = temp.nextCol;
			}
			System.out.println();
		}

		System.out.println();

		for(int i=0;i<cols.length;i++){
			temp = cols[i];
			System.out.print(i+"��° ���� ��忡 ����� ��: ");
			while(temp != null){
				System.out.print(temp.value+" ");
				temp = temp.nextRow;
			}
			System.out.println();
		}
	}
}
