public class ReservationSeatProgram {

	public static void main(String[] args){		
		int row = 5;
		int col = 4;

		Reservation reservation = new Reservation(row, col);
		reservation.setSeat();
		reservation.selectInstruction();
	}
}
