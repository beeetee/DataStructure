import java.io.IOException;

class SimpleDifferentiation {
	private ExpTreeNode root;

	// �̺� ��� ����� ������ 
	public SimpleDifferentiation(ExpTreeNode root) throws IOException, SyntaxError{
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
		tmp = new OperatorNode(op,left,right);
		return check(tmp, tmp.getOp(), tmp.getLeft(), tmp.getRight());
	}

	// '/' �̺�
	// (f/g)' = (f'*g-f*g')/g*g
	private ExpTreeNode differDivision(OperatorNode tmp) {
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
		ExpTreeNode de = check(denominator, denominator.getOp(),denominator.getLeft(),denominator.getRight());
		
		// f'*g
		OperatorNode multiLeft = new OperatorNode('*',differLeft,right);
		ExpTreeNode mL = check(multiLeft,multiLeft.getOp(),multiLeft.getLeft(),multiLeft.getRight());
		// f*g'
		OperatorNode multiRight = new OperatorNode('*',left,differRight);
		ExpTreeNode mR = check(multiRight,multiRight.getOp(),multiRight.getLeft(),multiRight.getRight());

		// �̺� ���, ���ڰ� �� ���
		OperatorNode numerator = new OperatorNode('-',mL,mR);

		// �̺� ��� ��ȯ
		tmp = new OperatorNode('/',numerator,de);
		return check(tmp, tmp.getOp(), tmp.getLeft(), tmp.getRight());
	}

	// '*' �̺�
	// (f*g)' = f'*g + f*g'
	private ExpTreeNode differMulti(OperatorNode tmp) {
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
		ExpTreeNode o1 = check(op1, op1.getOp(), op1.getLeft(), op1.getRight());
		OperatorNode op2 = new OperatorNode('*', differLeft,right);
		ExpTreeNode o2 = check(op2, op2.getOp(), op2.getLeft(), op2.getRight());
		
		tmp = new OperatorNode('+', o1, o2);
		return check(tmp, tmp.getOp(), tmp.getLeft(), tmp.getRight());
	}
	private ExpTreeNode differPower(OperatorNode tmp) {
		ConstantNode c = (ConstantNode)tmp.getRight();
		double cNumber = c.getNumber();
		IdentifierNode v = (IdentifierNode)tmp.getLeft();

		ConstantNode right = new ConstantNode(0.0);
		if(v.getVar().equals("x")){
			OperatorNode multi = new OperatorNode('^',v,new ConstantNode(cNumber-1));
			tmp = new OperatorNode('*',c, multi);
			// x�� �̺��ϸ� 1
			right = new ConstantNode(1.0);
		}
		tmp = new OperatorNode('*',tmp, right);
		return check(tmp, tmp.getOp(), tmp.getLeft(), tmp.getRight());
	}

	private ExpTreeNode check(ExpTreeNode tree, char op, ExpTreeNode left, ExpTreeNode right) {
		String leftType = left.getType().name();
		String rightType = right.getType().name();

		// ���� ��� return
		if(leftType.equals("CONSTANT") && rightType.equals("CONSTANT")){
			ConstantNode cl = (ConstantNode) left;
			ConstantNode cr = (ConstantNode) right;
			if(op=='+')
				return new ConstantNode(cl.getNumber() + cr.getNumber());
			else if(op=='-')
				return new ConstantNode(cl.getNumber() - cr.getNumber());
			else if(op=='*')
				return new ConstantNode(cl.getNumber() * cr.getNumber());
			else if(op=='/')
				return new ConstantNode(cl.getNumber() / cr.getNumber());		
		}
		
		if(op=='+'){
			// �� �� �� ����� ���� 0.0�̸� �ٸ� ��� ��ȯ
			if(leftType.equals("CONSTANT") && !rightType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode) left;
				if(c.getNumber()==0.0)
					return right;
			}
			if(rightType.equals("CONSTANT") && !leftType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode) right;
				if(c.getNumber()==0.0)
					return left;
			}
			// �� �� 0.0�̸� 0.0�� �� ���� ��� ��ȯ
			if(leftType.equals("CONSTANT") && rightType.equals("CONSTANT")){
				ConstantNode c1 = (ConstantNode) left;
				ConstantNode c2 = (ConstantNode) right;
				if(c1.getNumber()==0.0 && c2.getNumber()==0.0)
					return new ConstantNode(0.0);
				else if(c1.getNumber()==0.0)
					return new ConstantNode(c2.getNumber());
				else if(c2.getNumber()==0.0)
					return new ConstantNode(c1.getNumber());
			}
		}
		else if(op=='-'){
			// right�� 0.0 ���� ������ left ��ȯ
			if(leftType.equals("VARIABLE") && rightType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode) right;
				if(c.getNumber()==0)
					return left;
			}
			// �Ѵ� �����ϋ�, ���� ������� 0.0���� ������ ��� ��ȯ
			if(leftType.equals("VARIABLE") && rightType.equals("VARIABLE")){
				IdentifierNode l = (IdentifierNode) left;
				IdentifierNode r = (IdentifierNode) right;
				if(l.getVar().equals(r.getVar()))
					return new ConstantNode(0.0);
			}
		}
		else if(op=='*'){
			// �� �� �ϳ��� ���� 1.0�̸� 1.0�� �ƴ� ��带 ��ȯ
			// �� �� �ϳ��� ���� 0.0�̸� 0.0���� ��� ��ȯ
			if(!leftType.equals("CONSTANT") && rightType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode) right;
				if(c.getNumber()==1.0)
					return left;
				if(c.getNumber()==0.0)
					return new ConstantNode(0.0);
			}
			if(leftType.equals("CONSTANT") && !rightType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode) left;
				if(c.getNumber()==1.0)
					return right;
				if(c.getNumber()==0.0)
					return new ConstantNode(0.0);
			}
			if(leftType.equals("CONSTANT") && rightType.equals("CONSTANT")){
				ConstantNode c1 = (ConstantNode) left;
				ConstantNode c2 = (ConstantNode) right;
				if(c1.getNumber()==0.0 || c2.getNumber()==0.0)
					return new ConstantNode(0.0);
				else if(c1.getNumber()==1.0 && c2.getNumber()==1.0)
					return new ConstantNode(1.0);
			}
		}
		else if(op=='/'){
			if(leftType.equals("VARIABLE") && rightType.equals("VARIABLE")){
				IdentifierNode l = (IdentifierNode)left;
				IdentifierNode r = (IdentifierNode) right;	
				// �� ������ ���ٸ� 1.0 ���� ���� ��� ��ȯ
				if(l.getVar().equals(r.getVar()))
					return new ConstantNode(1.0);
			}
			if(!leftType.equals("CONSTANT") && rightType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode)right;
				// 1.0���� ������ ��ȭ���� ������ left �״�� ��ȯ
				if(c.getNumber()==1.0)
					return left;
			}
		}
		else if (op=='^'){
			if(leftType.equals("VARIABLE") && rightType.equals("CONSTANT")){
				IdentifierNode v = (IdentifierNode) left;
				ConstantNode c = (ConstantNode) right;
				// c�� ���� 1.0�̸� v�� �״�� ��ȯ
				if(v.getVar().equals("x") && c.getNumber()==1.0)
					return new IdentifierNode(v.getVar());
				// c�� ���� 0.0�̸�, 1.0���� ���� ��� ��ȯ
				if(v.getVar().equals("x") && c.getNumber()==0.0){
					return new ConstantNode(1.0);
				}
			}
		}
		return tree;
	}
}