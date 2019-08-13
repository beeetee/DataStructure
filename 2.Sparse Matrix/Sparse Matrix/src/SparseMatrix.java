class SparseMatrix {
	private Node[] rows;
	private Node[] cols;

	public SparseMatrix(int row, int col) {	// 생성자
		rows = new Node[row];
		cols = new Node[col];
	}

	/** 
	 * @param row 삽입할 값의 행 위치
	 * @param col 삽입할 값의 열 위치
	 * @param value 삽입할 값
	 */
	public void set(int row, int col, int value){		

		Node newNode = new Node(row,col,value);
		Matrix matrix = new Matrix(rows, cols);
		
		// rows나 cols가 비어있다면 row,col,value값을 
		// 그대로 가지는 노드를 생성한다.
		if(rows[row] == null && cols[col] == null) { 
			rows[row] = newNode;
			cols[col] = newNode;
		}

		// cols가 비어있다면 newNode를 바로 가리키게 하고
		// rows가 이미 연결되어있다면, 
		// 해당 위치의 rows의 col값을 비교하여 넣어준다.
		else if(rows[row]!=null && cols[col]==null){
			cols[col] = newNode;
			matrix.linkCols(newNode);
		}

		// rows가 비어있다면 newNode를 바로 가리키게 하고
		// cols가 이미 연결되어있다면, 
		// 해당 위치의 cols의 row값을 비교하여 넣어준다.
		else if(rows[row]==null && cols[col]!=null){
			rows[row] = newNode;
			matrix.linkRows(newNode);
		}

		// 행,열에 이미 연결되어 있는 노드가 존재할 때
		// 둘 다 값을 비교해가며 연결해준다.
		else{
			matrix.linkCols(newNode);
			matrix.linkRows(newNode);
		}
		// 값이 0인경우 삭제해준다.
		if(value==0)
			matrix.remove(row, col);
	}

	/**
	 * 행렬의 곱을 수행하는 메소드
	 * @param matrix 곱할 행렬
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
	 * 노드의 연결을 배열의 형태로 출력해주는 메소드
	 */
	public void print(){
		Node temp;
		System.out.printf("%-5s","");
		// 열의 정보 출력
		for(int i=0; i<cols.length;i++){
			System.out.printf("%-5s","["+i+"] ");
		}
		System.out.println();

		for(int i=0; i<rows.length;i++){
			// 행의 정보 출력
			System.out.printf("%-5s","["+i+"] ");
			if(rows[i]!=null){
				temp=rows[i];
				// rows에 연결된 노드 먼저 스캔
				for(int j=0;j<cols.length;j++){
					// 연결되어 있는 col값이라면 
					// 그 value를 출력해준다.
					if(temp.col==j){
						System.out.printf("%-5s",temp.value);
						if(temp.nextCol != null)
							temp = temp.nextCol;
					}
					// 연결되어있지 않은 cols라면 0을 출력해준다.
					else
						System.out.printf("%-5s",0);
				}
			}
			// 연결되어있지 않은 rows라면 0을 출력해준다.
			else
				for(int j=0;j<cols.length;j++){
					System.out.printf("%-5s",0);
				}
			System.out.println();
		}
	}
	/**
	 * 노드의 연결을 출력해주는 메소드
	 */
	public void out(){
		Node temp;
		for(int i=0;i<rows.length;i++){
			temp = rows[i];
			System.out.print(i+"번째 행의 노드에 연결된 값: ");
			while(temp != null){
				System.out.print(temp.value+" ");
				temp = temp.nextCol;
			}
			System.out.println();
		}

		System.out.println();

		for(int i=0;i<cols.length;i++){
			temp = cols[i];
			System.out.print(i+"번째 열의 노드에 연결된 값: ");
			while(temp != null){
				System.out.print(temp.value+" ");
				temp = temp.nextRow;
			}
			System.out.println();
		}
	}
}
