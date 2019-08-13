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
		in.ordinaryChar('/');	// '/'�� �ּ����� �����ϴ� ���ڷ� �������Ƿ�
		//  �̰��� ��� ������ ���ڷ� ����ϱ� ����
		in.ordinaryChar('-');	// a-b�� �ϳ��� ������ �� �� �����Ƿ� �̰��� 
		// ��� ���� ���ڷ� ����ϱ� ����
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
				// + �������� left�� result�� right�� readTerm()�� ���� ����� ���� 
				result= new OperatorNode('+',result,readTerm());break;
			}
			case '-' :  {
				// - �������� left�� result�� right�� readTerm()�� ���� ����� ���� 
				result= new OperatorNode('-',result,readTerm());break;
			}
			default : in.pushBack(); return result; //result ��ȯ
			}
		}
	}

	// Term ::= Factor { ( '*' | '/' | '^' ) Factor }	
	public ExpTreeNode readTerm() throws IOException, SyntaxError{
		ExpTreeNode result = readFactor();

		while (true) {
			in.nextToken();
			switch (in.ttype) {
			// *,/,^ �������� left�� result, right�� readFactor() ���� ��� ����
			case '*' : result= new OperatorNode('*',result,readFactor());break;			
			case '/' : result= new OperatorNode('/',result,readFactor());break;			
			case '^' : result= new OperatorNode('^', result, readFactor());break;
			default : in.pushBack(); return result;	//result ��ȯ
			}
		}
	}

	// Factor ::= Identifier | Number | '(' Expression ')' | '-' Factor
	public ExpTreeNode readFactor() throws IOException, SyntaxError{

		while(true) {
			in.nextToken();
			if(in.ttype==StreamTokenizer.TT_NUMBER) {
				// ����� ���, ConstantNode �����Ͽ� ��ȯ
				double n = in.nval;
				return new ConstantNode(n);
			}
			else if(in.ttype==StreamTokenizer.TT_WORD) {
				// �ܾ��� ���, IdentifierNode �����Ͽ� ��ȯ
				String s = in.sval;
				return new IdentifierNode(s);
			}
			else if(in.ttype=='(') {
				// ��ȣ ������ ǥ���� Ʈ���� ǥ���Ͽ� ��ȯ
				ExpTreeNode result = readExpression();
				in.nextToken();
				if(in.ttype!=')')
					throw new SyntaxError("��ȣ�� ���� ����");
				return result;
			}
			else if(in.ttype=='-') {
				// 0���� '-' ������ �ϴ� Ʈ�� ǥ���Ͽ� ��ȯ
				ExpTreeNode left = new ConstantNode(0.0);
				return new OperatorNode('-', left,readFactor());
			}
		}
	}
	// ǥ�� Ʈ���� ���� ��ȸ�� ���	
	public void print() {
		print(root);
		System.out.println();
	}

	public void print(ExpTreeNode tree) {
		if (tree != null) { 
			// Operator�� ��ȯ���� "( " ��� �� Operator�� ���� ���� ����ϵ��� ��
			if(tree.getType().name().equals("OPERATOR")){
				OperatorNode tmp = (OperatorNode) tree;
				System.out.print("( ");
				print(tmp.getLeft());
			}
			// ����� ���, Constant�� ��ȯ���� ��� ���
			if(tree.getType().name().equals("CONSTANT")){
				ConstantNode tmp = (ConstantNode) tree;
				System.out.print(tmp.getNumber()+" ");
			}			
			// ������ ���, Identifier�� ��ȯ���� �ĺ��� ���
			else if(tree.getType().name().equals("VARIABLE")){
				IdentifierNode tmp = (IdentifierNode) tree;
				System.out.print(tmp.getVar()+" ");
			}
			// �� �� ���� Operator�� ����, �����ڸ� ���
			else{
				OperatorNode tmp = (OperatorNode) tree;
				System.out.print(tmp.getOp()+" ");
			}
			// Operator�� ��ȯ���� Operator�� ������ ��½�Ű�� ��ȣ�� �ݾ� ��
			if(tree.getType().name().equals("OPERATOR")){
				OperatorNode tmp = (OperatorNode) tree;
				print(tmp.getRight());
				System.out.print(") ");
			}
		}
	}

	// �ϼ��� ǥ�� Ʈ���� �̿��Ͽ� �̺�
	public void differential() throws IOException, SyntaxError {
		Differentiation d = new Differentiation(root);
		// differneiation ��ü�� ��� �޾ƿ� ��½�����
		ExpTreeNode differential = d.returnRoot();
		print(differential);
		System.out.println();
	}
	
	// �̺� ����� ����ȭ
	public void simple() throws IOException, SyntaxError{
		SimpleDifferentiation d = new SimpleDifferentiation(root);
		ExpTreeNode differential = d.returnRoot();
		print(differential);
		System.out.println();
	}
}