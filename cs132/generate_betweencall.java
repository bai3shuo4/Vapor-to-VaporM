import cs132.util.*;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.parser.VaporParser;

//import vapor.parser.jar;

import java.util.*;
import java.io.*;
import java.lang.*;

public class generate_betweencall extends VInstr.Visitor<Throwable> {

	private HashMap<String, Interval> interval;
	private VFunction v;

	public generate_betweencall(HashMap<String, Interval> interval, VFunction v){
		this.interval = interval;
		this.v = v;
	}

	public void run_generate_betweencall() throws Throwable{
		for(VInstr instruction : v.body){
			instruction.accept(this);
		}
	}

	public void visit(VCall vcall) throws Throwable{
		int current_line = vcall.sourcePos.line;

		for(Map.Entry<String, Interval> tmp : interval.entrySet()){
			if((tmp.getValue()).start < current_line && (tmp.getValue()).end > current_line){
				tmp.getValue().changeCall();
			}
		}

	}

	public void visit(VAssign v) throws Throwable{

	}

	public void visit(VBuiltIn v) throws Throwable{

	}

	public void visit(VMemWrite v) throws Throwable{

	}

	public void visit(VMemRead v) throws Throwable{

	}

	public void visit(VBranch v) throws Throwable{

	}

	public void visit(VGoto v) throws Throwable{ 

	}

	public void visit(VReturn v) throws Throwable{

	}


}