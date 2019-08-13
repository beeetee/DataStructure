class Node {
	public int value;
	public int row;
	public int col;
	public Node nextRow;
	public Node nextCol;
	
	public Node(int row, int col, int value) {
		this.value = value;
		this.row = row;
		this.col = col;
		nextRow = null;
		nextCol = null;
	}
	
	public Node() {
		nextRow = null;
		nextCol = null;
	}	
}
