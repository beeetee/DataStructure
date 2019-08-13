import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class MicroFacebook {
	static LinkedList<Person> list;

	public static void main(String[] args) throws FileNotFoundException {
		list = new LinkedList<Person>();
		Scanner input = new Scanner(new File("facebook3.txt"));

		while(true){
			String s = input.next();

			// name과 phoneNumber를 연결리스트에 추가
			if(s.equals("A"))
				list.add(new Person(input.next(), input.next()));

			// name1과 name2를 친구 관계로 추가
			else if(s.equals("F"))
				addFriend(input.next(), input.next());

			// 입력받은 사람과 친구관계인 모든 사람의 이름과 전화번호를 출력한다
			else if(s.equals("P"))
				print(input.next());

			// 입력받은 이름에 해당하는 번호를 입력받은 번호로 바꾼다
			else if(s.equals("C")){
				String name = input.next();
				String newPhoneNumber = input.next();

				Person p = searchPerson(name);

				if(searchPerson(name)!=null)
					p.phoneNumber = newPhoneNumber;
				else
					System.out.println(name+"is not exist.");
			}

			// 입력받은 두 사람의 친구 관계를 해제한다.
			else if(s.equals("U"))
				undo(input.next(), input.next());

			// 입력받은 두 사람이 서로 친구관계인지 알려준다.
			else if(s.equals("R")){
				String name1 = input.next();
				String name2 = input.next();

				if(Friend(name1,name2))
					System.out.println(name1+" and "+name2+" are friends.");
				else
					System.out.println(name1+" and "+name2+" are not friends.");
			}

			// 이후 입력된 사람들을 서로 친구 관계로 연결해준다.
			else if(s.equals("G")){
				String st = input.nextLine();
				String[] names = st.split(" ");
				Person[] person = new Person[names.length];

				int index = 0;
				for(String sr : names)
					if(searchPerson(sr)!=null){
						person[index]=searchPerson(sr);
						index++;
					}

				for(int i=0;i<index;i++)
					for(int k=0;k<index;k++)						
						if(i!=k)
							person[i].friend.add(person[k]);

			}
			// 종료
			else if(s.equalsIgnoreCase("Q"))
				break;
		}
		input.close();
	}

	// 두 사람을 친구 관계로 연결해주는 메소드
	private static void addFriend(String name1, String name2) {
		Person p = searchPerson(name1);		
		Person f = searchPerson(name2);

		if(p!=null && f!=null){
			p.friend.add(f);
			f.friend.add(p);
		}
		if(p==null)
			System.out.println(name1+" is not exist.");
		if(f==null)
			System.out.println(name2+" is not exist.");
	}

	// 매개변수 name의 친구 정보를 출력해주는 메소드
	private static void print(String name) {
		LinkedList<Person> f = searchFriend(name);
		System.out.println(name+"'s friends: ");
		Collections.sort(f);

		for(int i=0;i<f.size();i++)
			System.out.println("\t"+f.get(i).name+",\t"+f.get(i).phoneNumber);
	}

	// 두 사람의 친구 관계를 해제하는 메소드
	private static void undo(String name1, String name2) {
		Person n1 = searchPerson(name1);		
		Person n2 = searchPerson(name2);

		LinkedList<Person> l1 = n1.friend;
		LinkedList<Person> l2 = n2.friend;

		if(Friend(name1,name2)){
			l1.remove(l1.indexOf(n2));
			l2.remove(l2.indexOf(n1));
		}
		else
			System.out.println(name1+" and "+name2+" are not friends.");

	}

	// name1과 name2가 친구 관계인지 여부를 반환해주는 메소드
	private static boolean Friend(String name1, String name2){
		Person p = searchPerson(name1);
		Iterator<Person> it = p.friend.iterator();
		while(it.hasNext()){
			Person pe=it.next();
			if(pe.name.equals(name2))
				return true;
		}
		return false;
	}

	// name의 친구들의 정보가 연결된 연결리스트를 반환해주는 메소드
	private static  LinkedList<Person> searchFriend(String name) {
		Person p = searchPerson(name);
		if(p!=null)
			return p.friend;
		return null;
	}

	// 매개변수 name이 친구라면 그 친구의 정보를 반환해주는 메소드
	private static Person searchPerson(String name) {
		Iterator<Person> it = list.iterator();
		while(it.hasNext()){
			Person p=it.next();
			if(p.name.equals(name))
				return p; 
		}
		return null;
	}
}