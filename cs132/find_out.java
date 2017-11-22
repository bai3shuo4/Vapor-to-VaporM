import cs132.util.*;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.parser.VaporParser;

//import vapor.parser.jar;

import java.util.*;
import java.io.*;
public class find_out extends VInstr.Visitor<Throwable>{
	int out_num;
	VFunction v;

	public find_out(VFunction v){
		this.v = v;
		out_num = 0;
	}

	public void findOutNum() throws Throwable{
		for(VInstr instruction : v.body){
  			 	instruction.accept(this);
  			}
	}
	public void visit(VCall v) throws Throwable {
			out_num = Math.max(out_num, v.args.length);
			return;	
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