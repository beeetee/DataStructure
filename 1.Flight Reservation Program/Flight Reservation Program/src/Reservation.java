import java.util.Scanner;
class Reservation {

	Scanner in = new Scanner(System.in);
	private char[][] seats;
	private Node names;
	private int row;
	private int col;

	/**
	 * ������
	 * parameter�� �־����� ���� ��� ���Ƿ� 1�� �����Ѵ�.
	 */
	public Reservation() {
		seats = new char[1][1];
	}

	/**
	 * ������
	 * @param rows charŸ�� 2�� �迭�� ��
	 * @param cols charŸ�� 2�� �迭�� ��
	 */
	public Reservation(int rows, int cols) {
		row = rows;
		col = cols;
		seats = new char[row][col];
	}

	/**
	 * �¼��� �����Ѵ�.
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
	 * ����ڰ� q�� �Է��� ������ ��ɾ �����ϰ� �Ѵ�.
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
	 * ������ ����ϸ� �ش� �̸��� �¼��� ��嵵 ���� 
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
	 * �̸� ������ ��带 �����Ѵ�.
	 * @param name ���� �̸�
	 * @param seat ���� �¼�
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
	 * �¼��� ����Ѵ�.
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
	 * ������ �����ϴ� �޼ҵ�
	 */
	public void reserve() {
		printSeat();
		System.out.print("> Enter a customer name and desired seat number: ");

		String name = in.next();
		String seat = in.next();

		int count = 1;	//1���� ������ 1�� �������� ��
		char alpha = 'A';	//char Ÿ�� ���� A�� �����ؼ� 1�� �������� ��


		for(int i=0;i<row;i++){
			// �¼��� �����ϸ� ��忬��,'*'�� �¼� �ٲ���
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
		// �������� �ʴ� �¼��� ������ ��� �ȳ��޼��� ���
		if(alpha > 'D' || count > 5){
			System.out.println("That seat is not exist");
		}
	}

	/**
	 * ���� ����� �ִ� �޼ҵ�
	 */
	public void cancel() {
		System.out.print("> Enter a cutomer name: ");
		String name = in.next();

		Node scan = names;
		String seat = null;

		// name�� ��Ī�Ǵ� seat�� ã�´�.
		while(scan != null) {
			if(scan.name.equalsIgnoreCase(name)) {
				seat = scan.seat;
			}
			scan = scan.next;
		}

		remove(name);// �ش� �̸� ��� ���� ����

		int count = 1;	//1���� ������ 1�� �������� ��
		char alpha = 'A';	//char Ÿ�� ���� A�� �����ؼ� 1�� �������� ��

		// �¼�ǥ ���󺹱�
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
	 * ���� ������ �¼��� �������� �˷��ش�.
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
	 * ���� ��Ȳ�� ����Ѵ�
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
	 * ���α׷��� �����Ų��.
	 */
	public void quit() {
		System.out.println("End of commands!");
	}
}
