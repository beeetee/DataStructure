import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

// Edge 클래스
// 간선에 대한 정보
class Edge<T> {
	public T vertItem; 		// 정점 데이터
	public T edgeItem;		// 가중치
	public Edge<T> next;	// 다음으로 연결된 것
	public Edge(T vertItem, T item) {
		this.vertItem = vertItem;
		this.edgeItem = item;
		next = null;
	}
}

// Vertex 클래스
// 정점에 대한 정보
class Vertex<T> {
	public T vertItem;			// 정점 데이터
	public Vertex<T> nextVert;	// 연결된 정점
	public Edge<T> nextEdge;	// 연결된 간선
	public boolean start;		// start 지점인지 여부
	public boolean end;			// end 지점인지 여부
	public Vertex(T vertItem) {
		this.vertItem = vertItem;
		nextVert = null;
		nextEdge = null;
	}
}
class Graph<T extends Comparable <T>> {
	private Vertex<T> head; 
	private int size;  // 그래프에 있는 정점의 개수
	private boolean isDirected;   // 방향/무방향을 나타내는 불리언 값
	private boolean hasEdgeValue;  // 간선에 가중치가 있는지 나타내는 불리언 값
	private T start;	// 시작점의 위치 값
	private Vertex<T> startVertex;	// 시작점의 vertex

	private LinkedList<T> end; // 종료 상태를 연결리스트로 연결하여 저장
	// 종료 상태를 vertex타입으로 연결
	private LinkedList<Vertex<T>> endVertex;

	public Graph() { this(false, false); }
	public Graph(boolean isDirected, boolean hasEdgeValue) {
		head = null;
		size = 0;
		this.isDirected = isDirected;
		this.hasEdgeValue = hasEdgeValue;
	}

	// inFile에 주어진 간선을 사용하여 인접리스트 생성 
	public void createGraph(Scanner inFile){
		T fromVert, toVert;

		T item; //weight

		// 시작점의 위치
		start = (T)inFile.nextLine();

		// 마지막 위치
		String tmp = inFile.nextLine();
		end = new LinkedList();
		endVertex = new LinkedList();
		StringTokenizer st = new StringTokenizer(tmp);
		while(st.hasMoreTokens())
			end.add((T)st.nextToken());

		// from-to-item을 차례로 연결하여 그래프 생성
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

	// " " 속의 데이터만 추출하여 item에 저장하여 반환하는 메소드
	private T setUpItem(T item) {
		String s = String.valueOf(item);
		StringBuilder sb = new StringBuilder();
		int index = s.indexOf("\"");
		sb.append(s.charAt(index+1));
		item = (T)sb.toString();
		return item;
	}

	// item을 갖는 정점을 만들어 정점 리스트에 삽입
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
	
	// vertex가 end에 속하는지 여부 반환
	private boolean end(Vertex<T> vertex) {
		for(int i=0;i<end.size();i++)
			if(vertex.vertItem.equals(end.get(i)))
				return true;
		return false;
	}
	
	// vertex가 start 지점인지 여부 반환
	private boolean first(Vertex<T> vertex) {
		if(start.equals(vertex.vertItem))
			return true;
		return false;
	}
	
	// 정점 fromVert에서 인접한 정점 toVert를 갖는 간선 item을 인접 리스트에 삽입 
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
	
	// 정점 노드 중에서 item을갖는 노드를  찾아 그 정점을 반환
	public Vertex<T> findVertex(T item){
		Vertex<T> curr = head;
		while(curr != null){
			if(curr.vertItem.compareTo(item)==0)
				break;
			curr = curr.nextVert;
		}
		return curr;
	}

	// inFile의 데이터의 legal 여부 판단하여 출력
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
	
	// legal 여부 판단하여 반환
	private boolean legal(String s) {
		Vertex<T> tmp = startVertex;
		// tmp에서 연결된 간선들 중
		// s의 각 char의 값을 가지는 가중치가 있는지 여부 저장 
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
		// tmp가 end에 속한다면 true 반환
		if(end(tmp))
			return true;
		return false;
	}
}

public class DFA {
	// 메인 메소드
	public static void main(String[] args) throws FileNotFoundException {
		Scanner inFile = new Scanner(new File("input.txt"));
		Graph g = new Graph(true,true);
		g.createGraph(inFile);
		inFile = new Scanner(new File("string.txt"));
		g.checkLegal(inFile);
	}
}