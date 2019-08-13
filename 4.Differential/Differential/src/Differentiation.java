import java.io.IOException;

class Differentiation {
	private ExpTreeNode root;
	
	// 미분 결과 저장될 생성자 
	public Differentiation(ExpTreeNode root) throws IOException, SyntaxError{
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
		return new OperatorNode(op,left,right);
	}
	
	// '/' 미분
	// (f/g)' = (f'*g-f*g')/g*g
	private OperatorNode differDivision(OperatorNode tmp) {
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

		// 각각 f'*g 와 f*g'
		OperatorNode multiLeft = new OperatorNode('*',differLeft,right);
		OperatorNode multiRight = new OperatorNode('*',left,differRight);

		// 미분 결과, 분자가 될 노드
		OperatorNode numerator = new OperatorNode('-',multiLeft,multiRight);

		// 미분 결과 반환
		return new OperatorNode('/',numerator,denominator);
	}
	
	// '*' 미분
	// (f*g)' = f'*g + f*g'
	private OperatorNode differMulti(OperatorNode tmp) {
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
		OperatorNode op2 = new OperatorNode('*', differLeft,right);

		return new OperatorNode('+', op1, op2);
	}
	private ExpTreeNode differPower(OperatorNode tmp) {
		ConstantNode c = (ConstantNode)tmp.getRight();
		double cNumber = c.getNumber();
		IdentifierNode v = (IdentifierNode)tmp.getLeft();

		// x가 아닌 변수를 미분할 때, 그 값은 무조건 0이 되므로
		// 마지막에 표현트리를 반환할 때 오른 쪽 노드에 0을 곱하여
		// 반환된 표현트리의 연산 결과가 무조건 0이 되도록 한다.
		ConstantNode right = new ConstantNode(0.0);
		if(v.getVar().equals("x")){
			OperatorNode multi = new OperatorNode('^',v,new ConstantNode(cNumber-1));
			tmp = new OperatorNode('*',c, multi);
			// x를 미분하면 1
			right = new ConstantNode(1.0);
		}
		return new OperatorNode('*',tmp, right);
	}
}