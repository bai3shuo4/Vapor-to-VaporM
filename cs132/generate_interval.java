import cs132.util.*;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.parser.VaporParser;

//import vapor.parser.jar;

import java.util.*;
import java.io.*;
import java.lang.*;

public class generate_interval extends VInstr.Visitor<Throwable> {

	private final HashMap<String, Interval> interval = new HashMap<>();
	private VFunction v;
	public generate_interval(VFunction v){
		this.v = v;
	}

	public void generate() throws Throwable {

		for(VVarRef param : v.params){
			Interval tmp = new Interval(param.toString(), param.sourcePos.line);
			interval.put(param.toString(), tmp);
		}

		for(VInstr instruction : v.body){
			instruction.accept(this);
		}

	}

	public void readmap(){
		for(Interval current : interval.values()){
			System.out.print(current.name + " ");
			System.out.print(Integer.toString(current.start) + " ");
			System.out.print(Integer.toString(current.end) + " ");
			System.out.println();
		}
	} 

	public void visit(VCall v) throws Throwable {
		
		for(VOperand operand : v.args){									//parameter		
			if(operand instanceof VVarRef.Local){
				Interval tmp = interval.get(operand.toString());
				tmp.changeEnd(v.sourcePos.line);
			}
		}

		if(v.addr instanceof VAddr.Var){								//function
			Interval tmp = interval.get(v.addr.toString());
			tmp.changeEnd(v.sourcePos.line);
		}


		if(interval.containsKey(v.dest.toString())){					//destination
			Interval tmp = interval.get(v.dest.toString());
			tmp.changeEnd(v.sourcePos.line);
		}
		else{
			interval.put(v.dest.toString(), new Interval(v.dest.toString(), v.sourcePos.line));
		}

	}

	public void visit(VAssign v) throws Throwable{

		if(v.source instanceof VVarRef.Local){
			//interval.put(v.source.toString(), new Interval(v.source.toString() , v.sourcePos.line));
			Interval tmp = interval.get(v.source.toString());
			tmp.changeEnd(v.sourcePos.line);
		}

		if(v.dest instanceof VVarRef.Local){
			if(interval.containsKey(v.dest.toString())){
				Interval tmp = interval.get(v.dest.toString());
				tmp.changeEnd(v.sourcePos.line);
			}
			else{
				interval.put(v.dest.toString(), new Interval(v.dest.toString(), v.sourcePos.line));
			}
		}


	}

	public void visit(VBuiltIn v) throws Throwable{

		for(VOperand operand : v.args){
			if(operand instanceof VVarRef.Local){
				Interval tmp = interval.get(operand.toString());
				tmp.changeEnd(v.sourcePos.line);
			}
		}

		if(v.dest instanceof VVarRef.Local){
			if(interval.containsKey(v.dest.toString())){
				Interval tmp = interval.get(v.dest.toString());
				tmp.changeEnd(v.sourcePos.line);
			}
			else{
				interval.put(v.dest.toString(), new Interval(v.dest.toString(), v.sourcePos.line));
			}
		}
	}

	public void visit(VMemWrite v) throws Throwable{

		if(v.source instanceof VVarRef.Local){
			//interval.put(v.source.toString(), new Interval(v.source.toString() , v.sourcePos.line));
			Interval tmp = interval.get(v.source.toString());
			tmp.changeEnd(v.sourcePos.line);
		}

		//adress to write
		if(v.dest instanceof VMemRef.Global){
			if(((VMemRef.Global)(v.dest)).base instanceof VAddr.Var){
				Interval tmp = interval.get(((VMemRef.Global)(v.dest)).base.toString());
				tmp.changeEnd(v.sourcePos.line);
			}
		}
	}

	public void visit(VMemRead v) throws Throwable{


		//adress to read

		if(v.source instanceof VMemRef.Global){
			if(((VMemRef.Global)(v.source)).base instanceof VAddr.Var){
				Interval tmp = interval.get(((VMemRef.Global)(v.source)).base.toString());
				tmp.changeEnd(v.sourcePos.line);
			}
		}


		if(v.dest instanceof VVarRef.Local){
			if(interval.containsKey(v.dest.toString())){
				Interval tmp = interval.get(v.dest.toString());
				tmp.changeEnd(v.sourcePos.line);
			}
			else{
				interval.put(v.dest.toString(), new Interval(v.dest.toString(), v.sourcePos.line));
			}
		}
	}

	public void visit(VBranch v) throws Throwable{

		if(v.value instanceof VVarRef.Local){
			Interval tmp = interval.get(v.value.toString());
			tmp.changeEnd(v.sourcePos.line);
		}
	}

	public void visit(VGoto v) throws Throwable{ 

	}

	public void visit(VReturn v) throws Throwable{
		if(v.value instanceof VVarRef.Local){
			Interval tmp = interval.get(v.value.toString());
			tmp.changeEnd(v.sourcePos.line);
		}
	}

		public static class Interval{

		int start;
		int end;
		String name;

		public Interval(String name, int start){
			this.name = name;
			this.start = start;
			this.end = start;
		}

		public void changeEnd(int end){
			this.end = end;
		}
	}


}