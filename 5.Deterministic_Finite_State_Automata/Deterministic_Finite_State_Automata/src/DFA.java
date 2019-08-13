import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

// Edge Ŭ����
// ������ ���� ����
class Edge<T> {
	public T vertItem; 		// ���� ������
	public T edgeItem;		// ����ġ
	public Edge<T> next;	// �������� ����� ��
	public Edge(T vertItem, T item) {
		this.vertItem = vertItem;
		this.edgeItem = item;
		next = null;
	}
}

// Vertex Ŭ����
// ������ ���� ����
class Vertex<T> {
	public T vertItem;			// ���� ������
	public Vertex<T> nextVert;	// ����� ����
	public Edge<T> nextEdge;	// ����� ����
	public boolean start;		// start �������� ����
	public boolean end;			// end �������� ����
	public Vertex(T vertItem) {
		this.vertItem = vertItem;
		nextVert = null;
		nextEdge = null;
	}
}
class Graph<T extends Comparable <T>> {
	private Vertex<T> head; 
	private int size;  // �׷����� �ִ� ������ ����
	private boolean isDirected;   // ����/�������� ��Ÿ���� �Ҹ��� ��
	private boolean hasEdgeValue;  // ������ ����ġ�� �ִ��� ��Ÿ���� �Ҹ��� ��
	private T start;	// �������� ��ġ ��
	private Vertex<T> startVertex;	// �������� vertex

	private LinkedList<T> end; // ���� ���¸� ���Ḯ��Ʈ�� �����Ͽ� ����
	// ���� ���¸� vertexŸ������ ����
	private LinkedList<Vertex<T>> endVertex;

	public Graph() { this(false, false); }
	public Graph(boolean isDirected, boolean hasEdgeValue) {
		head = null;
		size = 0;
		this.isDirected = isDirected;
		this.hasEdgeValue = hasEdgeValue;
	}

	// inFile�� �־��� ������ ����Ͽ� ��������Ʈ ���� 
	public void createGraph(Scanner inFile){
		T fromVert, toVert;

		T item; //weight

		// �������� ��ġ
		start = (T)inFile.nextLine();

		// ������ ��ġ
		String tmp = inFile.nextLine();
		end = new LinkedList();
		endVertex = new LinkedList();
		StringTokenizer st = new StringTokenizer(tmp);
		while(st.hasMoreTokens())
			end.add((T)st.nextToken());

		// from-to-item�� ���ʷ� �����Ͽ� �׷��� ����
		while(inFile.hasNext()){
			fromVert = (T)inFile.next();
			toVert = (T)inFile.next();

			if(hasEdgeValue){
				item = (T)inFile.nextLine();
				item = setUpItem(item);
				insertVertex(fromVert);
				insertVertex(toVert);
				insertEdge(fromVert,toVert,item);
				if(!isDirected)
					insertEdge(toVert,fromVert,item);
			}
			else{
				insertVertex(fromVert);
				insertVertex(toVert);
				insertEdge(fromVert,toVert,null);
				if(!isDirected)
					insertEdge(toVert,fromVert,null);
			}
		}
	}

	// " " ���� �����͸� �����Ͽ� item�� �����Ͽ� ��ȯ�ϴ� �޼ҵ�
	private T setUpItem(T item) {
		String s = String.valueOf(item);
		StringBuilder sb = new StringBuilder();
		int index = s.indexOf("\"");
		sb.append(s.charAt(index+1));
		item = (T)sb.toString();
		return item;
	}

	// item�� ���� ������ ����� ���� ����Ʈ�� ����
	public void insertVertex(T item){
		Vertex<T> vertex, curr, prev;
		vertex = new Vertex<T> (item);
		if(head==null)
			head = vertex;
		else{
			prev=null;
			curr=head;
			while(curr != null && item.compareTo(curr.vertItem)>0){
				prev = curr;
				curr=curr.nextVert;
			}
			if(curr!=null && item.compareTo(curr.vertItem)==0)
				return;
			if(prev==null)
				head = vertex;
			else
				prev.nextVert=vertex;
			vertex.nextVert = curr;
		}
		size++;
		if(first(vertex)){
			vertex.start = true;
			startVertex = vertex;
		}
		if(end(vertex)){
			vertex.end=true;
			endVertex.add(vertex);
		}
	}
	
	// vertex�� end�� ���ϴ��� ���� ��ȯ
	private boolean end(Vertex<T> vertex) {
		for(int i=0;i<end.size();i++)
			if(vertex.vertItem.equals(end.get(i)))
				return true;
		return false;
	}
	
	// vertex�� start �������� ���� ��ȯ
	private boolean first(Vertex<T> vertex) {
		if(start.equals(vertex.vertItem))
			return true;
		return false;
	}
	
	// ���� fromVert���� ������ ���� toVert�� ���� ���� item�� ���� ����Ʈ�� ���� 
	public void insertEdge(T fromVert, T toVert, T item){
		Edge<T> edge = new Edge<T> (toVert, item);
		Edge<T> prev, curr;
		Vertex<T> vertex = findVertex(fromVert);
		prev = null;
		curr= vertex.nextEdge;
		while(curr != null && toVert.compareTo(curr.vertItem)>0){
			prev = curr;
			curr = curr.next;
		}
		if(prev==null)
			vertex.nextEdge=edge;
		else
			prev.next=edge;
		edge.next=curr;

	}
	
	// ���� ��� �߿��� item������ ��带  ã�� �� ������ ��ȯ
	public Vertex<T> findVertex(T item){
		Vertex<T> curr = head;
		while(curr != null){
			if(curr.vertItem.compareTo(item)==0)
				break;
			curr = curr.nextVert;
		}
		return curr;
	}

	// inFile�� �������� legal ���� �Ǵ��Ͽ� ���
	public void checkLegal(Scanner inFile) {
		int n=1;
		while(inFile.hasNextLine()){
			String s = inFile.nextLine();
			System.out.print((n++) +" The string \""+ s+"\" is ");
			if(legal(s))
				System.out.println("legal.");
			else
				System.out.println("not legal.");
		}
	}
	
	// legal ���� �Ǵ��Ͽ� ��ȯ
	private boolean legal(String s) {
		Vertex<T> tmp = startVertex;
		// tmp���� ����� ������ ��
		// s�� �� char�� ���� ������ ����ġ�� �ִ��� ���� ���� 
		boolean[] check = new boolean[s.length()];

		for(int i=0;i<s.length();i++){
			char c = s.charAt(i);
			Edge<T> e = tmp.nextEdge;
			while(e!=null){
				if(e.edgeItem.equals(String.valueOf(c))){
					check[i]=true;
					tmp = findVertex(e.vertItem);
					break;
				}
				else
					e=e.next;
			}
			if(!check[i])
				return false;
		}
		// tmp�� end�� ���Ѵٸ� true ��ȯ
		if(end(tmp))
			return true;
		return false;
	}
}

public class DFA {
	// ���� �޼ҵ�
	public static void main(String[] args) throws FileNotFoundException {
		Scanner inFile = new Scanner(new File("input.txt"));
		Graph g = new Graph(true,true);
		g.createGraph(inFile);
		inFile = new Scanner(new File("string.txt"));
		g.checkLegal(inFile);
	}
}