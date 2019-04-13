package ec.jobsity.tenpin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Play {

	private List<String> players;

	public List<String> getPlayers() {
		return players;
	}

	public void setPlayers(List<String> players) {
		this.players = players;
	}

	public List<Intent> loadConferencesFromfile(String fileName) {
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		List<Intent> intents = new ArrayList<Intent>();
		try {
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains("//") || strLine.isEmpty())
					continue;
				String name = strLine.substring(0, strLine.lastIndexOf(" "));
				String value = strLine.substring(strLine.lastIndexOf(" ") + 1);
				Intent intent = new Intent(name, value);
				intents.add(intent);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		players = new ArrayList<>();
		players.addAll(
				intents.stream().map(e -> e.getName()).filter(distinctByKey(p -> p)).collect(Collectors.toList()));

		return intents;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public void getScore(List<Intent> intents) {
		drawHeader();
		for (String player : players) {
			List<Intent> play = intents.stream().filter(p -> p.getName().equals(player)).collect(Collectors.toList());
			StringBuilder pinfalls = new StringBuilder();
			System.out.println(player);
			System.out.println(drawPinFalls(play));
		}

	}

	private String drawPinFalls(List<Intent> play) {
		StringBuilder pinfalls = new StringBuilder();
		pinfalls.append("pinfalls").append("\t");
		Map<Integer, List<String>> valoresPorTurno = getPlayForPerson(play);
		StringBuilder values = new StringBuilder();
		for (Entry<Integer, List<String>> entry : valoresPorTurno.entrySet()) {
			for (String string : entry.getValue()) {
				values.append(string.equals("10") ? "x" : string).append(" ");
			}
			values.append("\t");
		}
		pinfalls.append(values);
		return pinfalls.toString();
	}

	private Map<Integer, List<String>> getPlayForPerson(List<Intent> play) {
		Map<Integer, List<String>> valoresPorTurno = new HashMap<>();
		List<String> valores = new ArrayList<String>();

		int count = 0;
		int numFrame = 0;
		for (Intent intent : play) {
			//System.out.println(intent.getValue());
			count++;
			valores.add(intent.getValue());
			if ( numFrame!=9&& isNumeric(intent.getValue())) {
				if (Integer.parseInt(intent.getValue()) == 10 || count == 2) {
					numFrame++;
					valoresPorTurno.put(numFrame, valores);
					valores = new ArrayList<String>();
					count = 0;
					continue;
				}
			} else if(numFrame!=9&& !isNumeric(intent.getValue())) {
				if (count == 2) {
					numFrame++;
					valoresPorTurno.put(numFrame, valores);
					valores = new ArrayList<String>();
					count = 0;
					continue;
				}
			}
		}
		numFrame++;
		valoresPorTurno.put(numFrame, valores);
		return valoresPorTurno;
	}

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); 
	}

	private void drawHeader() {
		StringBuilder heaader = new StringBuilder();
		heaader.append("frame").append("\t\t");
		int framecount = 0;
		while (framecount < 10) {
			framecount++;
			heaader.append(framecount).append("\t");
		}
		System.out.println(heaader);
	}

	public void rulers() {

	}
}
