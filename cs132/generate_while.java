import cs132.util.*;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.parser.VaporParser;

//import vapor.parser.jar;

import java.util.*;
import java.io.*;

public class generate_while {

	private VFunction v;
	private HashMap<String, Interval> interval;

	public generate_while(HashMap<String, Interval> interval, VFunction v){
		this.v = v;
		this.interval = interval;
	}

	public void run_generatewhile(){

		HashMap<Integer, while_range> while_map = new HashMap<>();
		for(VCodeLabel tmp : v.labels){
			String label = tmp.ident;
			if(label.length() <= 5 || !(label.substring(0,5).equals("while")))
				continue;
			int index = label.charAt(5);
			if(while_map.containsKey(index)){
				while_map.get(index).changeEnd(tmp.sourcePos.line);
			}
			else{
				while_map.put(index, new while_range(tmp.sourcePos.line, tmp.sourcePos.line));
			}
		}

		LinkedList<while_range> range = new LinkedList<>();
		for(Map.Entry<Integer, while_range> tmp : while_map.entrySet()){
			while_range current = tmp.getValue();
			range.add(current);
			//Iterator<while_range> iterator = range.iterator();
			//boolean add = false;
			// while(iterator.hasNext()){
			// 	while_range wr_tmp = iterator.next();

			// 	// if(wr_tmp.start > current.start && wr_tmp.end < current.end){
			// 	// 	iterator.remove();
			// 	// 	if(!add){
			// 	// 		range.add(current);
			// 	// 		add = true;
			// 	// 	}
			// 	// }

			// 	// else if(wr_tmp.start < current.start && wr_tmp.end > current.end){
			// 	// 	add = true;
			// 	// }

			// }

			// if(!add){
			// 	range.add(current);
			// }
		}

		// for(while_range tmp : range){
		// 	System.out.println(Integer.toString(tmp.start) + " " + Integer.toString(tmp.end));
		// }


		for(Map.Entry<String, Interval> tmp : interval.entrySet()){
			Interval current = tmp.getValue();

			for(while_range wr : range){
				if(current.end < wr.end && current.end > wr.start && current.start < wr.start){
					current.changeEnd(wr.end - 1);
				}
			}
		}

	}

	public static class while_range{
		int start;
		int end;

		public while_range(int start, int end){
			this.start = start;
			this.end = end;
		}

		public void changeEnd(int end){
			this.end = end;
		}
	}
}