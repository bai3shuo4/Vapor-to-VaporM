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
  static HashMap<String, String> allocate_map;
  static int local;
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

      V2VM v2vm = new V2VM();
  		for(VFunction function : program.functions){
      			 System.out.print("func " + function.ident);

      			 int parameter_length = function.params.length;
      			 if(parameter_length <= 4){
      				System.out.print(" [in 0, ");
      			 }
      			 else{
      			 	System.out.print(" [in " + Integer.toString(parameter_length - 4) + ", ");
      			 }

      			 find_out out = new find_out(function);
      			 out.findOutNum();
      			 int out_num = out.out_num;
      			 	if(out_num <= 4){
      			 		System.out.print("out 0, ");
      			 	}
      			 	else{
      			 		out_num = out_num - 4;
      			 		System.out.print("out " + Integer.toString(out_num) + ", ");
      			 	}

             generate_interval generate = new generate_interval(function);
             generate.generate();

             generate_betweencall betweencall = new generate_betweencall(generate.interval, function);
             betweencall.run_generate_betweencall();

             generate_while g_while = new generate_while(generate.interval, function);
             g_while.run_generatewhile();

             ///////////////////

             ///////////////////
             //generate.readmap();

             LinearScanAllocation lsa = new LinearScanAllocation(generate.interval);
             lsa.LinearScanRegisterAllocation();
             lsa.getMap();
             //lsa.readMap();
             allocate_map = new HashMap<>(lsa.allocate_map);
             local = lsa.local;
             // for(Map.Entry<String, String> tmp : allocate_map.entrySet()){
             //    System.out.print(tmp.getKey() + " ");
             //    System.out.print(tmp.getValue());
             //    System.out.println();
             //  }

             System.out.print("local " + Integer.toString(lsa.local) + "]");
             System.out.println();
             //add a[]
             //add local[]
             for(int i = 0; i < lsa.local; i++){
                if(i > 7)
                    break;
                System.out.println("local[" + Integer.toString(i) + "] = $s" + Integer.toString(i));

             }

             int count = 0;
             for(VVarRef tmp : function.params){
                if(count < 4)
                  System.out.println(allocate_map.get(tmp.toString()) + " = $a" + Integer.toString(count++));
                else{
                  int index = count-4;
                  System.out.println(allocate_map.get(tmp.toString()) + " = in[" + Integer.toString(index) + "]");
                  count++;
                }
             }




             LinkedList<String> label_instruction = new LinkedList<>();
             LinkedList<Integer> label_line = new LinkedList<>();

             for(VCodeLabel tmp : function.labels){
                label_instruction.add(tmp.ident + ":");
                label_line.add(tmp.sourcePos.line);
             }

             for(VInstr instruction : function.body){
                int current_line = instruction.sourcePos.line;
                while(!label_line.isEmpty() && current_line > label_line.getFirst()){
                  System.out.println(label_instruction.getFirst());
                  label_line.pop();
                  label_instruction.pop();
                }
                instruction.accept(v2vm);
             }


             
    			 
    			//int in = function.stack.in;
    			//int out = function.stack.out;
    			//int local = function.stack.local;

    			//System.out.println("[in" + Integer.toString(in) + " out " + Integer.toString(out) + " local" + Integer.toString(local) + "]");


    			

    		}
      }
		
	

	public void visit(VCall v) throws Throwable {
    int count = 0;
      for(VOperand params : v.args){
          String name = params.toString();
          if(allocate_map.get(name) != null){
            if(count <= 3)
              System.out.println("$a" + Integer.toString(count++) + " = " + allocate_map.get(name));
            else{
              int index = count-4;
              System.out.println("out[" + Integer.toString(index) + "] = " + allocate_map.get(name));
              count++;
            }
          }

          else{
            if(count <= 3)
              System.out.println("$a" + Integer.toString(count++) + " = " + name);
            else{
              int index = count-4;
              System.out.println("out[" + Integer.toString(index) + "] = " + name);
              count++;
            }
          }
      }

      String call_addr = v.addr.toString();
      if(allocate_map.get(call_addr) != null){
          System.out.println("call " + allocate_map.get(call_addr));
      }
      else{
        System.out.println("call " + call_addr);
      }

      System.out.println(allocate_map.get(v.dest.toString()) + " = $v0");


		
	}

	public void visit(VAssign v) throws Throwable{
      String source = v.source.toString();
      if(allocate_map.get(source) == null){
          System.out.println(allocate_map.get(v.dest.toString()) + " = " + source);
      }

      else{
          System.out.println(allocate_map.get(v.dest.toString()) + " = " + allocate_map.get(source));
      }
	}

	public void visit(VBuiltIn v) throws Throwable{

      if(v.dest != null)
          System.out.print(allocate_map.get(v.dest.toString()) + " = ");

      System.out.print(v.op.name + "(");

      for(VOperand tmp : v.args){
          String source = tmp.toString();
          if(allocate_map.get(source) == null){
              System.out.print(source + " ");
          }

          else{
              System.out.print(allocate_map.get(source) + " ");
          }
      }

      System.out.print(")");
      System.out.println();
	}

	public void visit(VMemWrite v) throws Throwable{
      String source = v.source.toString();
      if(allocate_map.get(source) != null){
          source = allocate_map.get(source);
      }

      if(v.dest instanceof VMemRef.Global){
          VMemRef.Global tmp = (VMemRef.Global)(v.dest);
          System.out.println("[" + allocate_map.get(tmp.base.toString()) + "+" + tmp.byteOffset + "] = " + source);

      }
	}

	public void visit(VMemRead v) throws Throwable{
      if(v.source instanceof VMemRef.Global){
          VMemRef.Global tmp = (VMemRef.Global)(v.source);
          System.out.println(allocate_map.get(v.dest.toString()) + " = [" + allocate_map.get(tmp.base.toString()) + "+" + tmp.byteOffset + "]");

      }
	}

	public void visit(VBranch v) throws Throwable{
      String value = v.value.toString();

      if(allocate_map.get(value) != null){
          value = allocate_map.get(value);
      }

      if(v.positive){
          System.out.print("if ");
      }
      else{
          System.out.print("if0 ");
      }

      System.out.print(value + " goto " + v.target.toString());
      System.out.println();

	}

	public void visit(VGoto v) throws Throwable{ 
      System.out.println("goto " + v.target.toString());
	}

	public void visit(VReturn v) throws Throwable{

      if(v.value != null){
          String source = v.value.toString();
          if(allocate_map.get(source) == null){
              System.out.println("$v0 = " + source);
          }

          else{
              System.out.println("$v0 = " + allocate_map.get(source));
          }
      }

      for(int i = 0; i < local; i++){
                if(i > 7)
                    break;
                System.out.println("$s" + Integer.toString(i) + " = local[" + Integer.toString(i) + "]");

             }

      System.out.println("ret ");

      

      //System.out.println();
	}
}







