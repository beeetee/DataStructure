import java.util.Scanner;
class Reservation {

	Scanner in = new Scanner(System.in);
	private char[][] seats;
	private Node names;
	private int row;
	private int col;

	/**
	 * 생성자
	 * parameter가 주어지지 않은 경우 임의로 1을 지정한다.
	 */
	public Reservation() {
		seats = new char[1][1];
	}

	/**
	 * 생성자
	 * @param rows char타입 2차 배열의 행
	 * @param cols char타입 2차 배열의 열
	 */
	public Reservation(int rows, int cols) {
		row = rows;
		col = cols;
		seats = new char[row][col];
	}

	/**
	 * 좌석을 세팅한다.
	 */
	public void setSeat() {
		for(int i=0;i<seats.length;i++) {
			char ch='A';
			for(int k=0;k<seats[i].length;k++){
				seats[i][k]=ch;
				ch++;
			}
		}
	}

	/**
	 * 사용자가 q를 입력할 때까지 명령어를 선택하게 한다.
	 */
	public void selectInstruction(){
		boolean a=true;
		
		while(a){
			System.out.print("> Enter a command: (r)eserve, c(ancel), f(ind), p(rint), or q(uit): ");
			String enter = in.next();
			
			if(enter.equalsIgnoreCase("r"))
				reserve();
			else if(enter.equalsIgnoreCase("c")) 
				cancel();
			
			else if(enter.equalsIgnoreCase("f")) 
				find();
			
			else if(enter.equalsIgnoreCase("p")) 
				print();
			
			else if(enter.equalsIgnoreCase("q")) {
				quit();
				a=false;
			}
			else 
				System.out.println("that instruction isn't exist");
			
		}
	}
	
	/**
	 * 예약을 취소하면 해당 이름과 좌석의 노드도 삭제 
	 */
	public void remove(String name) {
		if(names == null){}
		else {
			Node prev = null;
			Node current = names;

			while(current != null){
				if(current.name.equalsIgnoreCase(name)) {
					if(prev==null) 
						names=names.next;
					else
						prev.next = current.next;
					System.out.println("Customer name: "+name 
						+ ", reservation canceled");
					break;
				}
				prev = current;
				current = current.next;
			}
			if(current == null) {
				System.out.println("Customer name: "+name +"'s Seat is not exist");
			}
		}
	}

	/**
	 * 이름 순으로 노드를 연결한다.
	 * @param name 고객의 이름
	 * @param seat 예약 좌석
	 */
	public void insertInSortedOrder(String name, String seat) {

		if(names == null) {
			names = new Node(name,seat);
		}

		else{
			Node prev = null;
			Node current = names;

			while(current != null) {
				if(current.name.compareTo(name)>0){
					break;
				}
				else if(current.name.compareTo(name)==0){
					current.name=name;
					current = current.next;
				}
				else {
					prev = current;
					current = current.next;
				}
			}

			Node newNode = new Node(name, seat);
			newNode.next=current;	

			if(prev==null)
				names = newNode;
			else
				prev.next=newNode;
		}
	}

	/**
	 * 좌석을 출력한다.
	 */
	public void printSeat() {
		System.out.println("\nSeat Layout");
		System.out.println("-----------");
		for(int i=0;i<seats.length;i++) {
			System.out.print(i+1+" ");
			for(int k=0;k<seats[i].length;k++){
				System.out.print(seats[i][k]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * 예약을 지원하는 메소드
	 */
	public void reserve() {
		printSeat();
		System.out.print("> Enter a customer name and desired seat number: ");

		String name = in.next();
		String seat = in.next();

		int count = 1;	//1부터 시작해 1씩 증가시켜 비교
		char alpha = 'A';	//char 타입 변수 A로 시작해서 1씩 증가시켜 비교


		for(int i=0;i<row;i++){
			// 좌석이 존재하면 노드연결,'*'로 좌석 바꿔줌
			if(seat.contains(String.valueOf(count))){
				for(int j=0;j<col;j++){
					if(seat.contains(String.valueOf(alpha))) {
						if(seats[count-1][j]!='*'){
							insertInSortedOrder(name, 
										seat);
							seats[count-1][j] = '*';
						}
						else {
							System.out.println("That seat is not available!");
						}
						break;
					}
					alpha++;
				}break;
			}
			count++;
		}
		// 존재하지 않는 좌석을 예약할 경우 안내메세지 출력
		if(alpha > 'D' || count > 5){
			System.out.println("That seat is not exist");
		}
	}

	/**
	 * 예약 취소해 주는 메소드
	 */
	public void cancel() {
		System.out.print("> Enter a cutomer name: ");
		String name = in.next();

		Node scan = names;
		String seat = null;

		// name과 매칭되는 seat을 찾는다.
		while(scan != null) {
			if(scan.name.equalsIgnoreCase(name)) {
				seat = scan.seat;
			}
			scan = scan.next;
		}

		remove(name);// 해당 이름 노드 연결 끊음

		int count = 1;	//1부터 시작해 1씩 증가시켜 비교
		char alpha = 'A';	//char 타입 변수 A로 시작해서 1씩 증가시켜 비교

		// 좌석표 원상복구
		if(seat != null) {
			for(int i=0;i<row;i++){
				if(seat.contains(String.valueOf(count))){
					for(char j=0;j<col;j++){
							if(seat.contains(String.valueOf(alpha))) {
								seats[count-1][j] = alpha;
								break;
						}
						alpha++;
					}
				}
				count++;
			}
		}
	}

	/**
	 * 고객이 예약한 좌석이 무엇인지 알려준다.
	 */
	public void find() {
		Node scan = names;

		System.out.print("> Enter a cutomer name: ");
		String name = in.next();

		while(scan != null) {
			if(scan.name.equalsIgnoreCase(name)) {
				System.out.println("Seat number: "+scan.seat);break;
			}
			scan = scan.next;
		}

		if(scan == null){
			System.out.println("No such customer: "+name);
		}

	}

	/**
	 * 예약 현황을 출력한다
	 */
	public void print() {
		printSeat();

		int count = 0;
		for(int i=0;i<row;i++) {
			for(int j=0;j<col;j++) {
				if(seats[i][j]=='*'){
					count++;
				}
			}
		}

		System.out.println("Reservation information");
		System.out.println("Name\tSeatNumber");
		System.out.println("----\t----------");

		Node scan = names;

		while(scan != null) {
			System.out.println(scan.name + "\t"+scan.seat);
			scan = scan.next;
		}
		
		System.out.println("\nAvailable number of seats: "+((row*col)-count)+"\n");

	}

	/**
	 * 프로그램을 종료시킨다.
	 */
	public void quit() {
		System.out.println("End of commands!");
	}
}
