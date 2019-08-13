import java.io.IOException;
import java.io.StreamTokenizer;

class SyntaxError extends Exception {
	public SyntaxError() {
		super("Error in Statement");
	}
	public SyntaxError(String msg) {
		super(msg);
	}
}

class ExpTree {
	private ExpTreeNode root;
	private StreamTokenizer in; 
	public ExpTree(StreamTokenizer in) {
		this.in = in;
		root = null;
		in.ordinaryChar('/');	// '/'는 주석문을 구성하는 문자로 여겨지므로
		//  이것을 벗어나 나누기 문자로 사용하기 위함
		in.ordinaryChar('-');	// a-b는 하나의 변수로 볼 수 있으므로 이것을 
		// 벗어나 빼기 문자로 사용하기 위함
	}
	public void buildExpTree() throws IOException, SyntaxError {
		root = readExpression();
	}

	// Expression ::= Term { ( '+' | '-' ) Term }	
	public ExpTreeNode readExpression() throws IOException, SyntaxError{
		ExpTreeNode result = readTerm();

		while (true) {
			in.nextToken();
			switch (in.ttype) {
			case '+' : {
				// + 기준으로 left에 result를 right에 readTerm()의 실행 결과를 연결 
				result= new OperatorNode('+',result,readTerm());break;
			}
			case '-' :  {
				// - 기준으로 left에 result를 right에 readTerm()의 실행 결과를 연결 
				result= new OperatorNode('-',result,readTerm());break;
			}
			default : in.pushBack(); return result; //result 반환
			}
		}
	}

	// Term ::= Factor { ( '*' | '/' | '^' ) Factor }	
	public ExpTreeNode readTerm() throws IOException, SyntaxError{
		ExpTreeNode result = readFactor();

		while (true) {
			in.nextToken();
			switch (in.ttype) {
			// *,/,^ 기준으로 left에 result, right에 readFactor() 실행 결과 연결
			case '*' : result= new OperatorNode('*',result,readFactor());break;			
			case '/' : result= new OperatorNode('/',result,readFactor());break;			
			case '^' : result= new OperatorNode('^', result, readFactor());break;
			default : in.pushBack(); return result;	//result 반환
			}
		}
	}

	// Factor ::= Identifier | Number | '(' Expression ')' | '-' Factor
	public ExpTreeNode readFactor() throws IOException, SyntaxError{

		while(true) {
			in.nextToken();
			if(in.ttype==StreamTokenizer.TT_NUMBER) {
				// 상수일 경우, ConstantNode 생성하여 반환
				double n = in.nval;
				return new ConstantNode(n);
			}
			else if(in.ttype==StreamTokenizer.TT_WORD) {
				// 단어일 경우, IdentifierNode 생성하여 반환
				String s = in.sval;
				return new IdentifierNode(s);
			}
			else if(in.ttype=='(') {
				// 괄호 다음의 표현식 트리로 표현하여 반환
				ExpTreeNode result = readExpression();
				in.nextToken();
				if(in.ttype!=')')
					throw new SyntaxError("괄호를 닫지 않음");
				return result;
			}
			else if(in.ttype=='-') {
				// 0에서 '-' 연산을 하는 트리 표현하여 반환
				ExpTreeNode left = new ConstantNode(0.0);
				return new OperatorNode('-', left,readFactor());
			}
		}
	}
	// 표현 트리를 중위 순회로 출력	
	public void print() {
		print(root);
		System.out.println();
	}

	public void print(ExpTreeNode tree) {
		if (tree != null) { 
			// Operator로 변환시켜 "( " 출력 후 Operator의 왼쪽 먼저 출력하도록 함
			if(tree.getType().name().equals("OPERATOR")){
				OperatorNode tmp = (OperatorNode) tree;
				System.out.print("( ");
				print(tmp.getLeft());
			}
			// 상수일 경우, Constant로 변환시켜 상수 출력
			if(tree.getType().name().equals("CONSTANT")){
				ConstantNode tmp = (ConstantNode) tree;
				System.out.print(tmp.getNumber()+" ");
			}			
			// 변수일 경우, Identifier로 변환시켜 식별자 출력
			else if(tree.getType().name().equals("VARIABLE")){
				IdentifierNode tmp = (IdentifierNode) tree;
				System.out.print(tmp.getVar()+" ");
			}
			// 그 외 경우는 Operator일 때로, 연산자를 출력
			else{
				OperatorNode tmp = (OperatorNode) tree;
				System.out.print(tmp.getOp()+" ");
			}
			// Operator로 변환시켜 Operator의 오른쪽 출력시키고 괄호를 닫아 줌
			if(tree.getType().name().equals("OPERATOR")){
				OperatorNode tmp = (OperatorNode) tree;
				print(tmp.getRight());
				System.out.print(") ");
			}
		}
	}

	// 완성된 표현 트리를 이용하여 미분
	public void differential() throws IOException, SyntaxError {
		Differentiation d = new Differentiation(root);
		// differneiation 객체의 노드 받아와 출력시켜줌
		ExpTreeNode differential = d.returnRoot();
		print(differential);
		System.out.println();
	}
	
	// 미분 결과를 간략화
	public void simple() throws IOException, SyntaxError{
		SimpleDifferentiation d = new SimpleDifferentiation(root);
		ExpTreeNode differential = d.returnRoot();
		print(differential);
		System.out.println();
	}
}