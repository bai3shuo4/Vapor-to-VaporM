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

	private final HashMap<String, String> register = new HashMap<String, String>();   // interval_name registername
	private final HashMap<String, String> location = new HashMap<String, String>();

	private final LinkedList<String> t_register = new LinkedList<>();
	private final LinkedList<String> s_register = new LinkedList<>();

	private final LinkedHashMap<String, Integer> startpoint = new LinkedHashMap<>();
	private final LinkedHashMap<String, Integer> endpoint = new LinkedHashMap<>();

	private LinkedList<String> active;      //store_active interval

	int local;
	HashMap<String, String> allocate_map;

	public LinearScanAllocation(HashMap<String, Interval> interval){

		this.interval = interval;
		local = 0;
		allocate_map = new HashMap<>();

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

		for(int i = 0; i < 8; i++){
			t_register.add("$t" + Integer.toString(i));
			s_register.add("$s" + Integer.toString(i));
		}

		t_register.add("$t8");


	}

	public void readMap(){

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

	public void getMap(){

		for(Map.Entry<String, String> tmp : register.entrySet()){
			allocate_map.put(tmp.getKey(),tmp.getValue());
		}
		for(Map.Entry<String, String> tmp : location.entrySet()){
			allocate_map.put(tmp.getKey(),tmp.getValue());
		}
	}

	public String findFreeRegister(){
		String register_name = "error";

		if(!t_register.isEmpty()){
			register_name = t_register.getFirst();
			t_register.removeFirst();
		}

		return register_name;
	}

	public void returnFreeRegister(String register_name){

		t_register.add(0, register_name);    				//put it in the head

	}



	public void LinearScanRegisterAllocation(){

		active = new LinkedList<>();

		for(Map.Entry<String, Integer> interval : startpoint.entrySet()){
			String i = interval.getKey();
			ExpireOldIntervals(i);
			if(active.size() == 9)					//considering only t register
				SpillAtInterval(i);
			else{
				//register[i] <--- free register
				register.put(i, findFreeRegister());
				//i ---> active
				active.add(i);
				//sort active by end point
				sortActive(active);
			}
		}
	}

	public void ExpireOldIntervals(String i){

		for(String j : active){
			if(endpoint.get(j) >= startpoint.get(i))
				return;
			//remove j from active;
			active.remove(j);
			//add register[j] to pool of free register
			returnFreeRegister(register.get(j));
		}
	}

	public void SpillAtInterval(String i){

		String spill = active.getLast();
		if(endpoint.get(spill) > endpoint.get(i)){
			//register[i] <----- register[spill]
			String register_spill = register.get(spill);
			register.put(i, register_spill);
			register.remove(spill);
			//location[spill] <----- new stack location
			location.put(spill, "local[" + Integer.toString(local) + "]");
			local = local + 1;
			//remove spill from active
			active.remove(spill);
			//add i to active, sort by increasing end point
			active.add(i);
			sortActive(active);
		}
		else{
			//location[i] <----- new stack location
			location.put(i, "local[" + Integer.toString(local) + "]");
			local = local + 1;
		}
	}

	public void sortActive(LinkedList<String> active){
		Collections.sort(active, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return (endpoint.get(o1) - endpoint.get(o2));
			}
		});

	}


}