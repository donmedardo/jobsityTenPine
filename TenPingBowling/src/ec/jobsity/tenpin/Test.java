package ec.jobsity.tenpin;

import java.util.List;

public class Test {

	public static void main(String[] args) {
		Play a = new Play();
		List<Intent>intent = a.loadConferencesFromfile("play.txt");
		a.getScore(intent);
		/*for (Intent intent2 : intent) {
			System.out.println(intent2.getName());
		}*/

	}

}
