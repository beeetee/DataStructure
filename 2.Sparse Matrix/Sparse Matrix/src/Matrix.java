//Node ����� ������ �����ϴ� Ŭ����
class Matrix {
	
	private Node[] rows;
	private Node[] cols;	
	
	// ������
	public Matrix(Node[] rows, Node[] cols) {
		this.rows = rows;
		this.cols = cols;
	}
	
	/**
	 * NodeŸ�� ���� �迭 �� �� ��带 ���� �����ִ� �޼ҵ�
	 * @param rows �� ���
	 * @param newNode ������ ���
	 */
	public void linkCols(Node newNode){
		Node prev = null;
		Node scan = rows[newNode.row];

		while(scan != null){
			if(scan.col>newNode.col)
				break;
			else if(scan.col == newNode.col){
				scan.value=newNode.value;
				scan=scan.nextCol;
			}
			else{
				prev = scan;
				scan =scan.nextCol;
			}
		}
		
		newNode.nextCol=scan;
		if(prev == null){
			rows[newNode.row] = newNode;
		}
		else {
			prev.nextCol = newNode;
		}
	}
	
	/**
	 * NodeŸ�� ���� �迭 �� �� ��带 ���� �����ִ� �޼ҵ�
	 * @param cols �� ���
	 * @param newNode ������ ���
	 */
	public void linkRows(Node newNode){
		Node prev = null;
		Node scan = cols[newNode.col];

		while(scan != null){
			if(scan.row>newNode.row)
				break;
			else if(scan.row == newNode.row){
				scan.value=newNode.value;
				scan=scan.nextRow;
			}
			else{
				prev = scan;
				scan =scan.nextRow;
			}
		}

		newNode.nextRow=scan;

		if(prev == null){
			cols[newNode.col] = newNode;
		}
		else {
			prev.nextRow = newNode;
		}
	}

	/**
	 * ��带 �������ִ� �żҵ�
	 * @param row 
	 * @param col
	 */
	public void remove(int row, int col) {
		Node currRow;
		Node currCol;
		Node prevRow = null;
		Node prevCol = null;
		
		currRow = rows[row];
		while(currRow.col!=col){
			prevRow = currRow;
			currRow = currRow.nextCol;
		}

		currCol = cols[col];
		while(currCol.row!=row){
			prevCol = currCol;
			currCol=currCol.nextRow;
		}

		if(prevCol == null)
			cols[col] = cols[col].nextRow;
		else
			prevCol.nextRow=currCol.nextRow;
		
		
		if(prevRow == null)
			rows[row] = currRow.nextCol;
		else
			prevRow.nextCol=currRow.nextCol;
	}
}
