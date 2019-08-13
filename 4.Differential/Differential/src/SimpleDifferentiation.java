import java.io.IOException;

class SimpleDifferentiation {
	private ExpTreeNode root;

	// 미분 결과 저장될 생성자 
	public SimpleDifferentiation(ExpTreeNode root) throws IOException, SyntaxError{
		this.root = differential(root);
	}

	// ExpTreeNode 타입의 root를 반환
	public ExpTreeNode returnRoot(){
		return root;
	}

	// 미분을 수행
	private ExpTreeNode differential(ExpTreeNode tree) {
		if(tree !=null){
			if(tree.getType().name().equals("OPERATOR")){
				OperatorNode tmp = (OperatorNode) tree;
				if(tmp.getOp()=='*'){
					// 곱셈 미분 공식 호출
					tree = differMulti(tmp);
				}
				else if(tmp.getOp()=='/'){
					// 나누기 미분 공식 호출
					tree = differDivision(tmp);
				}
				else if(tmp.getOp()=='+' || tmp.getOp()=='-'){
					// + 혹은 - 미분 공식 호출
					tree = differCal(tmp);
				}
				else if(tmp.getOp()=='^'){
					// n제곱승 미분 공식 호출
					tree = differPower(tmp);
				}
			}
		}
		// 미분 완료된 tree 반환
		return tree;
	}

	// '+', '-' 미분
	// (f+g)' = f'+g', (f-g)' = f'-g'
	private ExpTreeNode differCal(OperatorNode tmp) {
		char op = tmp.getOp();
		String leftType = tmp.getLeft().getType().name();
		String rightType = tmp.getRight().getType().name();

		ExpTreeNode left = tmp.getLeft();
		ExpTreeNode right = tmp.getRight();

		// left
		if(leftType.equals("CONSTANT")){	
			// 상수미분하면 0이므로 값이 0.0인 ConstantNode 생성
			left = new ConstantNode(0.0);
		}
		else if(leftType.equals("VARIABLE")){
			IdentifierNode t = (IdentifierNode)left;
			// 변수일 경우, 변수가 'x'면 값이 1인 ConstantNode 생성
			if(t.getVar().equals("x")){
				left = new ConstantNode(1.0);
			}
			// 그 외 변수는 0.0인 ConstantNode 생성
			else{
				left = new ConstantNode(0.0);
			}
		}
		// OperatorNode라면 해당 operator에 맞게 다른 메소드 호출 
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
		// 왼쪽, 오른쪽 미분한 결과를 기존의 Operator로 연결한 OperatorNode 반환
		tmp = new OperatorNode(op,left,right);
		return check(tmp, tmp.getOp(), tmp.getLeft(), tmp.getRight());
	}

	// '/' 미분
	// (f/g)' = (f'*g-f*g')/g*g
	private ExpTreeNode differDivision(OperatorNode tmp) {
		String leftType = tmp.getLeft().getType().name();
		String rightType = tmp.getRight().getType().name();

		// 각각 f, g
		ExpTreeNode left = tmp.getLeft();
		ExpTreeNode right = tmp.getRight();

		// f'과  g'의 결과과 저장될 노드
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

		// 미분 결과, 분모가 될 노드 
		OperatorNode denominator = new OperatorNode('*',right,right);
		ExpTreeNode de = check(denominator, denominator.getOp(),denominator.getLeft(),denominator.getRight());
		
		// f'*g
		OperatorNode multiLeft = new OperatorNode('*',differLeft,right);
		ExpTreeNode mL = check(multiLeft,multiLeft.getOp(),multiLeft.getLeft(),multiLeft.getRight());
		// f*g'
		OperatorNode multiRight = new OperatorNode('*',left,differRight);
		ExpTreeNode mR = check(multiRight,multiRight.getOp(),multiRight.getLeft(),multiRight.getRight());

		// 미분 결과, 분자가 될 노드
		OperatorNode numerator = new OperatorNode('-',mL,mR);

		// 미분 결과 반환
		tmp = new OperatorNode('/',numerator,de);
		return check(tmp, tmp.getOp(), tmp.getLeft(), tmp.getRight());
	}

	// '*' 미분
	// (f*g)' = f'*g + f*g'
	private ExpTreeNode differMulti(OperatorNode tmp) {
		String leftType = tmp.getLeft().getType().name();
		String rightType = tmp.getRight().getType().name();

		ExpTreeNode left = tmp.getLeft();
		ExpTreeNode right = tmp.getRight();

		// f'과 g'이 저장되
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
			// x를 미분하면 1
			right = new ConstantNode(1.0);
		}
		tmp = new OperatorNode('*',tmp, right);
		return check(tmp, tmp.getOp(), tmp.getLeft(), tmp.getRight());
	}

	private ExpTreeNode check(ExpTreeNode tree, char op, ExpTreeNode left, ExpTreeNode right) {
		String leftType = left.getType().name();
		String rightType = right.getType().name();

		// 연산 결과 return
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
			// 둘 중 한 노드의 값이 0.0이면 다른 노드 반환
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
			// 둘 다 0.0이면 0.0의 값 갖는 노드 반환
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
			// right가 0.0 값을 가지면 left 반환
			if(leftType.equals("VARIABLE") && rightType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode) right;
				if(c.getNumber()==0)
					return left;
			}
			// 둘다 변수일떄, 같은 변수라면 0.0값을 가지는 노드 반환
			if(leftType.equals("VARIABLE") && rightType.equals("VARIABLE")){
				IdentifierNode l = (IdentifierNode) left;
				IdentifierNode r = (IdentifierNode) right;
				if(l.getVar().equals(r.getVar()))
					return new ConstantNode(0.0);
			}
		}
		else if(op=='*'){
			// 둘 중 하나의 값이 1.0이면 1.0이 아닌 노드를 반환
			// 둘 중 하나라도 값이 0.0이면 0.0값의 노드 반환
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
				// 두 변수가 같다면 1.0 값을 갖는 노드 반환
				if(l.getVar().equals(r.getVar()))
					return new ConstantNode(1.0);
			}
			if(!leftType.equals("CONSTANT") && rightType.equals("CONSTANT")){
				ConstantNode c = (ConstantNode)right;
				// 1.0으로 나누면 변화없기 때문에 left 그대로 반환
				if(c.getNumber()==1.0)
					return left;
			}
		}
		else if (op=='^'){
			if(leftType.equals("VARIABLE") && rightType.equals("CONSTANT")){
				IdentifierNode v = (IdentifierNode) left;
				ConstantNode c = (ConstantNode) right;
				// c의 값이 1.0이면 v를 그대로 반환
				if(v.getVar().equals("x") && c.getNumber()==1.0)
					return new IdentifierNode(v.getVar());
				// c의 값이 0.0이면, 1.0값을 갖는 노드 반환
				if(v.getVar().equals("x") && c.getNumber()==0.0){
					return new ConstantNode(1.0);
				}
			}
		}
		return tree;
	}
}