package ec.jobsity.tenpin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
			System.out.println(player);
			System.out.println(drawPinFalls(play));
			System.out.println(drawScore((play)));
		}

	}

	private String drawPinFalls(List<Intent> play) {
		StringBuilder pinfalls = new StringBuilder();
		pinfalls.append("pinfalls").append("\t");
		Map<Integer, List<Integer>> valoresPorTurno = getPlayForPerson(play);
		StringBuilder values = new StringBuilder();
		for (Entry<Integer, List<Integer>> entry : valoresPorTurno.entrySet()) {
			for (Integer string : entry.getValue()) {
				values.append(string==10 ? "x" : string).append(" ");
			}
			values.append("\t");
		}
		pinfalls.append(values);
		return pinfalls.toString();
	}
	
	private String drawScore(List<Intent> play) {
		StringBuilder pinfalls = new StringBuilder();
		pinfalls.append("score").append("\t\t");
		Map<Integer, List<Integer>> valoresPorTurno = getPlayForPerson(play);
		Map<String,Integer> scoreResult= score(valoresPorTurno);
		for (Entry<String,Integer >score : scoreResult.entrySet()) {
			pinfalls.append(score.getValue()).append("\t");
		}
		
		return pinfalls.toString();
	}
	
	

	private Map<Integer, List<Integer>> getPlayForPerson(List<Intent> play) {
		Map<Integer, List<Integer>> valoresPorTurno = new HashMap<>();
		List<Integer> valores = new ArrayList<Integer>();

		int count = 0;
		int numFrame = 0;
		for (Intent intent : play) {
			count++;
			valores.add(isNumeric(intent.getValue())?  Integer.parseInt(intent.getValue()):0);
			if ( numFrame!=9&& isNumeric(intent.getValue())) {
				if (Integer.parseInt(intent.getValue()) == 10 || count == 2) {
					numFrame++;
					valoresPorTurno.put(numFrame, valores);
					valores = new ArrayList<Integer>();
					count = 0;
					continue;
				}
			} else if(numFrame!=9&& !isNumeric(intent.getValue())) {
				if (count == 2) {
					numFrame++;
					valoresPorTurno.put(numFrame, valores);
					valores = new ArrayList<Integer>();
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
	
	public Map<String,Integer> score(Map<Integer, List<Integer>> tiros) {
		Map<String,Integer> scoreRes= new HashMap<String, Integer>(); 
		int sumaTotal =0;
		for (Entry<Integer, List<Integer>> entry : tiros.entrySet()) {
			List<Integer> valores =entry.getValue();
			Integer clave =entry.getKey();
			int suma =0;
			for(Integer valor :valores) {
				suma = suma +valor;
			}
			
			if(valores.size()==1&&tiros.get(clave+1).size()==1) {
				sumaTotal = sumaTotal+megaChusa(valores,tiros.get(clave+1),tiros.get(clave+2));
			}
			
			else if(valores.size()==1&&tiros.get(clave+1).size()!=1) {
				sumaTotal = sumaTotal+chusa(valores,tiros.get(clave+1));
			}else if(suma ==10) {
				sumaTotal = sumaTotal+semiPlena(valores,tiros.get(clave+1));
			}else if(suma !=10) {
				sumaTotal = sumaTotal+none(valores);
				
			}
			scoreRes.put(entry.getKey().toString(), sumaTotal);
		}
		return scoreRes;
	}

	public int megaChusa(List<Integer> first,List<Integer>second,List<Integer>third) {
		int sum=0;
		
		if(first!=null && !first.isEmpty()) {
			sum = sum+first.get(0);
		}
		if(second!=null && !second.isEmpty()) {
			sum = sum+second.get(0);
		}
		if(third!=null && !third.isEmpty()) {
			sum =  sum+third.get(0)+(third.size()>1? third.get(1):0) ;
		}
		
		
		return  sum;
	}
	
	public int chusa(List<Integer> first,List<Integer>second) {
		
		int sum=0;
		
		if(first!=null && !first.isEmpty()) {
			sum = sum+first.get(0);
		}
		if(second!=null && !second.isEmpty()) {
			sum = sum+second.get(0)+ (second.size()>1? second.get(1):0) ;
		}
		
		
		
		return  sum;
	}
	
	public int semiPlena(List<Integer> first,List<Integer>second) {
		return first.get(0)+ (first.size()>1?first.get(1):0)+second.get(0);
	}
	
	public int none(List<Integer> first) {
		return first.get(0)+ (first.size()>1?first.get(1):0);
	}
}
