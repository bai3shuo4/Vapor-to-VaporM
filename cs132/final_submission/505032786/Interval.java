import java.util.*;
import java.io.*;
import java.lang.*;

public class Interval{

			int start;
			int end;
			String name;
			boolean between_call;

			public Interval(String name, int start){
				this.name = name;
				this.start = start;
				this.end = start;
				between_call = false;
			}

			public void changeEnd(int end){
				this.end = end;
			}

			public void changeCall(){
				between_call = true;
			}
		}