import java.util.LinkedList;

class Person implements Comparable<Person>{

	String name;
	String phoneNumber;
	LinkedList<Person> friend;
	
	Person(String n, String p){
		name = n;
		phoneNumber = p;
		friend = new LinkedList<>();
	}
	
	@Override
	public int compareTo(Person p){
		if(this.name.compareTo(p.name)>0)
			return 1;
		else if(this.name.compareTo(p.name)<0)
			return -1;
		else 
			return 0;
	}	
}