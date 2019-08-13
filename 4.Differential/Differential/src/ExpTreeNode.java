enum NodeType {CONSTANT, VARIABLE, OPERATOR}
abstract class ExpTreeNode {
	public abstract NodeType getType();
}

class ConstantNode extends ExpTreeNode {
	private double number;
	public ConstantNode(double number) {
		this.number = number;
	}
	public NodeType getType() {
		return NodeType.CONSTANT;
	}
	public double getNumber() {
		return number;
	}
}

class IdentifierNode extends ExpTreeNode {
	private String name;
	public IdentifierNode(String name) {
		this.name = name;
	}
	public NodeType getType() {
		return NodeType.VARIABLE;
	}
	public String getVar() {
		return name;
	}
}

class OperatorNode extends ExpTreeNode {
	private char op;
	private ExpTreeNode left;
	private ExpTreeNode right;
	public OperatorNode(char op, ExpTreeNode left, ExpTreeNode right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
	public NodeType getType() {
		return NodeType.OPERATOR;
	}
	public char getOp() {
		return op;
	}
	public ExpTreeNode getLeft() {
		return left;
	}
	public ExpTreeNode getRight() {
		return right;
	}
}