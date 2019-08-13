//Node 연결과 삭제를 수행하는 클래스
class Matrix {
	
	private Node[] rows;
	private Node[] cols;	
	
	// 생셩자
	public Matrix(Node[] rows, Node[] cols) {
		this.rows = rows;
		this.cols = cols;
	}
	
	/**
	 * Node타입 이차 배열 중 행 노드를 연결 시켜주는 메소드
	 * @param rows 행 노드
	 * @param newNode 연결할 노드
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
	 * Node타입 이차 배열 중 열 노드를 연결 시켜주는 메소드
	 * @param cols 열 노드
	 * @param newNode 연결할 노드
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
	 * 노드를 삭제해주는 매소드
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
