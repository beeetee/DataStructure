class Node {

	public String seat;
	public String name;
	public Node next;

	
	public Node(String name, String seat) {	// ������
		this.name = name;
		this.seat = seat;
		next = null;
	}
	
	public Node() {
		next = null;
	}
}
