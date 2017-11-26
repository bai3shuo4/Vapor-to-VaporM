import cs132.util.*;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.parser.VaporParser;

//import vapor.parser.jar;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.Iterable;

public class LinearScanAllocation{

	private VFunction v;
	private HashMap<String, Interval> interval;

	private final LinkedHashMap<String, Integer> startpoint = new LinkedHashMap<>();
	private final LinkedHashMap<String, Integer> endpoint = new LinkedHashMap<>();



	private LinkedList<String, Integer> active;


	public LinearScanAllocation(HashMap<String, Interval> interval){

		this.interval = interval;

		List<Map.Entry<String, Interval>> list = new ArrayList<Map.Entry<String, Interval>>(interval.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Interval>>() {
			public int compare(Map.Entry<String, Interval> o1, Map.Entry<String, Interval> o2) {
				return (o1.getValue().start - o2.getValue().start);
			}
		});
		for(Map.Entry<String, Interval> tmp : list){
			startpoint.put(tmp.getKey(), tmp.getValue().start);
		}

		list = new ArrayList<Map.Entry<String, Interval>>(interval.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Interval>>() {
			public int compare(Map.Entry<String, Interval> o1, Map.Entry<String, Interval> o2) {
				return (o1.getValue().end - o2.getValue().end);
			}
		});
		for(Map.Entry<String, Interval> tmp : list){
			endpoint.put(tmp.getKey(), tmp.getValue().end);
		}


	}

	public void readmap(){

		for(Map.Entry<String, Integer> tmp : startpoint.entrySet()){
			System.out.print(tmp.getKey() + " ");
			System.out.print(tmp.getValue().toString());
			System.out.println();
		}

		for(Map.Entry<String, Integer> tmp : endpoint.entrySet()){
			System.out.print(tmp.getKey() + " ");
			System.out.print(tmp.getValue().toString());
			System.out.println();
		}
	}



	public void LinearScanRegisterAllocation(){

		active = new LinkedList<>();

		for(Map.Entry<String, Integer> interval : startpoint.entrySet()){
			ExpireOldIntervals(interval.getKey());
			if(interval.size() == R)
				SpillAtInterval(interval.getKey());
			else{
				//register[i] <--- free register
				//i ---> active
				//sort active by end point
			}
		}
	}

	public void ExpireOldIntervals(String name){

		for(Map.Entry<String, Integer> interval : active.entrySet()){
			if(endpoint.get(interval.getKey()) >= startpoint.get(name))
				return;
			//remove j from active;
			//add register[j] to pool of free register
		}
	}

	public void SpillAtInterval(String name){

		String spill = active.getLast();
		if(endpoint.get(spill) > endpoint.get(name)){
			//register[i] <----- register[spill]
			//location[spill] <----- new stack location
			//remove spill from active
			//add i to active, sort by increasing end point
		}
		else{
			//location <----- new stack location
		}
	}







}