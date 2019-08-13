import java.io.IOException;

class Differentiation {
	private ExpTreeNode root;
	
	// �̺� ��� ����� ������ 
	public Differentiation(ExpTreeNode root) throws IOException, SyntaxError{
		this.root = differential(root);
	}

	// ExpTreeNode Ÿ���� root�� ��ȯ
	public ExpTreeNode returnRoot(){
		return root;
	}

	// �̺��� ����
	private ExpTreeNode differential(ExpTreeNode tree) {
		if(tree !=null){
			if(tree.getType().name().equals("OPERATOR")){
				OperatorNode tmp = (OperatorNode) tree;
				if(tmp.getOp()=='*'){
					// ���� �̺� ���� ȣ��
					tree = differMulti(tmp);
				}
				else if(tmp.getOp()=='/'){
					// ������ �̺� ���� ȣ��
					tree = differDivision(tmp);
				}
				else if(tmp.getOp()=='+' || tmp.getOp()=='-'){
					// + Ȥ�� - �̺� ���� ȣ��
					tree = differCal(tmp);
				}
				else if(tmp.getOp()=='^'){
					// n������ �̺� ���� ȣ��
					tree = differPower(tmp);
				}
			}
		}
		// �̺� �Ϸ�� tree ��ȯ
		return tree;
	}
	
	// '+', '-' �̺�
	// (f+g)' = f'+g', (f-g)' = f'-g'
	private ExpTreeNode differCal(OperatorNode tmp) {
		char op = tmp.getOp();
		String leftType = tmp.getLeft().getType().name();
		String rightType = tmp.getRight().getType().name();

		ExpTreeNode left = tmp.getLeft();
		ExpTreeNode right = tmp.getRight();

		// left
		if(leftType.equals("CONSTANT")){	
			// ����̺��ϸ� 0�̹Ƿ� ���� 0.0�� ConstantNode ����
			left = new ConstantNode(0.0);
		}
		else if(leftType.equals("VARIABLE")){
			IdentifierNode t = (IdentifierNode)left;
			// ������ ���, ������ 'x'�� ���� 1�� ConstantNode ����
			if(t.getVar().equals("x")){
				left = new ConstantNode(1.0);
			}
			// �� �� ������ 0.0�� ConstantNode ����
			else{
				left = new ConstantNode(0.0);
			}
		}
		// OperatorNode��� �ش� operator�� �°� �ٸ� �޼ҵ� ȣ�� 
		else{
			OperatorNode t = (OperatorNode)left;
			if(t.getOp()=='*')
				left = differMulti(t);
			else if(t.getOp()=='/')
				left = differDivision(t);
			else if(t.getOp()=='^')
				left = differPower(t);
			else
				left = differCal(t);
		}

		//right
		if(rightType.equals("CONSTANT")){
			right = new ConstantNode(0.0);
		}
		else if(rightType.equals("VARIABLE")){
			IdentifierNode t = (IdentifierNode)right;
			if(t.getVar().equals("x")){
				right = new ConstantNode(1.0);
			}
			else{
				right = new ConstantNode(0.0);
			}
		}
		else{
			OperatorNode t = (OperatorNode)right;
			if(t.getOp()=='*')
				right = differMulti(t);
			else if(t.getOp()=='/')
				right = differDivision(t);
			else if(t.getOp()=='^')
				right = differPower(t);
			else
				right = differCal(t);
		}
		// ����, ������ �̺��� ����� ������ Operator�� ������ OperatorNode ��ȯ
		return new OperatorNode(op,left,right);
	}
	
	// '/' �̺�
	// (f/g)' = (f'*g-f*g')/g*g
	private OperatorNode differDivision(OperatorNode tmp) {
		String leftType = tmp.getLeft().getType().name();
		String rightType = tmp.getRight().getType().name();

		// ���� f, g
		ExpTreeNode left = tmp.getLeft();
		ExpTreeNode right = tmp.getRight();

		// f'��  g'�� ����� ����� ���
		ExpTreeNode differLeft = left;
		ExpTreeNode differRight = right;

		// left
		if(leftType.equals("CONSTANT")){
			differLeft = new ConstantNode(0.0);
		}
		else if(leftType.equals("VARIABLE")){
			IdentifierNode v = (IdentifierNode)left;
			if(v.getVar().equals("x"))
				differLeft = new ConstantNode(1.0);
			else
				differLeft = new ConstantNode(0.0);
		}
		else{
			OperatorNode t = (OperatorNode)left;
			if(t.getOp()=='*')
				differLeft = differMulti(t);
			else if(t.getOp()=='/')
				differLeft = differDivision(t);
			else if(t.getOp()=='^')
				differLeft = differPower(t);
			else
				differLeft = differCal(t);
		}

		//right
		if(rightType.equals("CONSTANT")){
			differRight = new ConstantNode(0.0);
		}
		else if(rightType.equals("VARIABLE")){
			IdentifierNode v = (IdentifierNode)right;
			if(v.getVar().equals("x"))
				differRight = new ConstantNode(1.0);
			else
				differRight = new ConstantNode(0.0);
		}
		else{
			OperatorNode t = (OperatorNode)right;
			if(t.getOp()=='*')
				differRight = differMulti(t);
			else if(t.getOp()=='/')
				differRight = differDivision(t);
			else if(t.getOp()=='^')
				differRight = differPower(t);
			else
				differRight = differCal(t);
		}
		
		// �̺� ���, �и� �� ��� 
		OperatorNode denominator = new OperatorNode('*',right,right);

		// ���� f'*g �� f*g'
		OperatorNode multiLeft = new OperatorNode('*',differLeft,right);
		OperatorNode multiRight = new OperatorNode('*',left,differRight);

		// �̺� ���, ���ڰ� �� ���
		OperatorNode numerator = new OperatorNode('-',multiLeft,multiRight);

		// �̺� ��� ��ȯ
		return new OperatorNode('/',numerator,denominator);
	}
	
	// '*' �̺�
	// (f*g)' = f'*g + f*g'
	private OperatorNode differMulti(OperatorNode tmp) {
		String leftType = tmp.getLeft().getType().name();
		String rightType = tmp.getRight().getType().name();

		ExpTreeNode left = tmp.getLeft();
		ExpTreeNode right = tmp.getRight();

		// f'�� g'�� �����
		ExpTreeNode differLeft = left;
		ExpTreeNode differRight = right;

		// left
		if(leftType.equals("CONSTANT")){
			differLeft = new ConstantNode(0.0);
		}
		else if(leftType.equals("VARIABLE")){
			IdentifierNode v = (IdentifierNode)left;
			if(v.getVar().equals("x"))
				differLeft = new ConstantNode(1.0);
			else
				differLeft = new ConstantNode(0.0);
		}
		else{
			OperatorNode t = (OperatorNode)left;
			if(t.getOp()=='*')
				differLeft = differMulti(t);
			else if(t.getOp()=='/')
				differLeft = differDivision(t);
			else if(t.getOp()=='^')
				differLeft = differPower(t);
			else
				differLeft = differCal(t);
		}

		//right
		if(rightType.equals("CONSTANT")){
			differRight = new ConstantNode(0.0);
		}
		else if(rightType.equals("VARIABLE")){
			IdentifierNode v = (IdentifierNode)right;
			if(v.getVar().equals("x"))
				differRight = new ConstantNode(1.0);
			else
				differRight = new ConstantNode(0.0);
		}
		else{
			OperatorNode t = (OperatorNode)right;
			if(t.getOp()=='*')
				differRight = differMulti(t);
			else if(t.getOp()=='/')
				differRight = differDivision(t);
			else if(t.getOp()=='^')
				differRight = differPower(t);
			else
				differRight = differCal(t);
		}

		OperatorNode op1 = new OperatorNode('*', left,differRight);
		OperatorNode op2 = new OperatorNode('*', differLeft,right);

		return new OperatorNode('+', op1, op2);
	}
	private ExpTreeNode differPower(OperatorNode tmp) {
		ConstantNode c = (ConstantNode)tmp.getRight();
		double cNumber = c.getNumber();
		IdentifierNode v = (IdentifierNode)tmp.getLeft();

		// x�� �ƴ� ������ �̺��� ��, �� ���� ������ 0�� �ǹǷ�
		// �������� ǥ��Ʈ���� ��ȯ�� �� ���� �� ��忡 0�� ���Ͽ�
		// ��ȯ�� ǥ��Ʈ���� ���� ����� ������ 0�� �ǵ��� �Ѵ�.
		ConstantNode right = new ConstantNode(0.0);
		if(v.getVar().equals("x")){
			OperatorNode multi = new OperatorNode('^',v,new ConstantNode(cNumber-1));
			tmp = new OperatorNode('*',c, multi);
			// x�� �̺��ϸ� 1
			right = new ConstantNode(1.0);
		}
		return new OperatorNode('*',tmp, right);
	}
}