import cs132.util.*;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.parser.VaporParser;

//import vapor.parser.jar;

import java.util.*;
import java.io.*;


public class V2VM extends VInstr.Visitor<Throwable>{
			//static boolean find_out;
			//static int out_num;

	public static void main(String [] args) throws Throwable{

		
			Op[] ops = {
			Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
    		Op.PrintIntS, Op.HeapAllocZ, Op.Error,
			};

			boolean allowLocals = true;
			String[] registers = null;
			boolean allowStack = false;

			VaporProgram program = null;
		try{
			
    		program = VaporParser.run(new InputStreamReader(System.in), 1, 1,
                              Arrays.asList(ops),
                              allowLocals, registers, allowStack);
  			}
  		catch (Exception e) {
  			e.printStackTrace();
  			System.out.println("Error");
  			//return;
  		}

  		for(VDataSegment class_name : program.dataSegments){
  			System.out.println("const " + class_name.ident);
  			
  			for(VOperand.Static function_name : class_name.values){
  				System.out.println("	" + function_name.toString());
  			}
  			System.out.println();

  		}

  		for(VFunction function : program.functions){
  			System.out.print(function.ident);

  			 int parameter_length = function.params.length;
  			 if(parameter_length <= 4){
  				System.out.print(" [in 0 ");
  			 }
  			 else{
  			 	System.out.print(" [in " + Integer.toString(parameter_length - 4) + " ");
  			 }

  			 find_out out = new find_out(function);
  			 out.findOutNum();
  			 int out_num = out.out_num;
  			 	if(out_num <= 4){
  			 		System.out.print("out 0 ");
  			 	}
  			 	else{
  			 		out_num = out_num - 4;
  			 		System.out.print("out " + Integer.toString(out_num) + " ");
  			 	}

  			 System.out.println();

         generate_interval generate = new generate_interval(function);
         generate.generate();
         generate.readmap();
  			 
  			 }
  			 

  			//int in = function.stack.in;
  			//int out = function.stack.out;
  			//int local = function.stack.local;

  			//System.out.println("[in" + Integer.toString(in) + " out " + Integer.toString(out) + " local" + Integer.toString(local) + "]");


  			

  		}
		
	

	public void visit(VCall v) throws Throwable {
		
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







