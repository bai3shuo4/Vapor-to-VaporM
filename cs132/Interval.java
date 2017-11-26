import java.util.*;
import java.io.*;
import java.lang.*;

public class Interval{

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