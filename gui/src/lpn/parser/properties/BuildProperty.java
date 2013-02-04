package lpn.parser.properties;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import lpn.parser.LhpnFile;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;


//import lpn.parser.properties.PropertyLexer;
//import antlrPackage.PropertyParser;
//import antlrPackage.PropertyParser.program_return;


public class BuildProperty {
	//public static JFrame frame;
	static int numPlaces=0;
	static int numTransitions=0;
	static int numFailTransitions=0;
	static int numFailPlaces=0;
	static int numStartPlaces=0;
	static int numEndPlaces=0;
	static String pFirst = "p0";
	static boolean loop = false;
	
	static List list = new List();
	public void buildProperty(String propFileName) throws IOException, RecognitionException {

		//String propertyId = JOptionPane.showInputDialog(frame, "Enter the SVA property name:", "Model ID", JOptionPane.PLAIN_MESSAGE);
		//System.out.println(propertyId);
		//if (propertyId!=null){
		//String property = JOptionPane.showInputDialog(frame, "Enter the SVA property:", "Model", JOptionPane.PLAIN_MESSAGE);
		//CharStream charStream = new ANTLRStringStream(" wait(omega > 2.2, 20);\r\n" + 
		//"assert(abc, 20); ");
		numPlaces=0;
		numTransitions=0;
		numFailTransitions=0;
		numFailPlaces=0;
		numStartPlaces=0;
		numEndPlaces=0;

		LhpnFile lpn = new LhpnFile();
		lpn.load(propFileName);
		
		String lpnFileString= propFileName.substring(0, propFileName.length()-4);
		String lpnFileName = lpnFileString.concat("lpn");
		File lpnFile = new File(lpnFileName);
		lpnFile.createNewFile();
		System.out.println("No of places : "+numPlaces);
		BufferedReader input = new BufferedReader(new FileReader(propFileName));

		String line = input.readLine();

		//StringBuffer sb2 = new StringBuffer(line);
		StringBuffer sb = new StringBuffer(line);
		//LhpnFile lpn = new LhpnFile();

		while(line!=null){
		
			line=input.readLine();
				sb.append(line);
				
		}

		String  property = sb.toString();
		System.out.println("property: "+property+"\n");
		CharStream charStream = new ANTLRStringStream(property);
		PropertyLexer lexer = new PropertyLexer(charStream);
		TokenStream tokenStream =  new CommonTokenStream(lexer);
		PropertyParser parser = new PropertyParser(tokenStream);
		PropertyParser.program_return program = parser.program();
		System.out.println("tree: "+((Tree)program.tree).toStringTree()+"\n");

		CommonTree r0 = ((CommonTree)program.tree);
		//System.out.println("parent :"+program.start.getText());
		int number = r0.getChildCount();
		System.out.println("NUMBER : "+number+"\n");
		printTree(r0, number);
		generateFile(r0, lpn,lpnFileName);
	}

	public void generateFile(CommonTree r0, LhpnFile lpn, String lpnFileName){
		LhpnFile lpnFinal = new LhpnFile();
		

		File lpnFile = new File(".lpn");
		try {
			lpnFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			lpnFinal = generateLPN(r0, lpn, false);
			if(loop){
			lpnFinal.addTransition("t" + numTransitions);
			numTransitions++;
			lpnFinal.addMovement("p"+(numPlaces-1),"t" +(numTransitions-1));
			lpnFinal.addMovement("t" +(numTransitions-1), pFirst); 
			loop=false;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lpnFinal.save(lpnFileName);

	}
	public static void printTree(CommonTree t, int number) {

		if ( t != null ) {
			StringBuffer sb = new StringBuffer(number);
			for ( int i = 0; i < number; i++ )
				sb = sb.append("   ");
			for ( int i = 0; i < t.getChildCount(); i++ ) {
				System.out.println(sb.toString() + t.getChild(i).toString());
				printTree((CommonTree)t.getChild(i), number+1);
			}
		}
	} 

	public static LhpnFile generateLPN(CommonTree t, LhpnFile lpn2, boolean recursiveCall) throws IOException {
		String enablingCond="";
		String delay="";
		String varType = " ";
		String varName = "";

		LhpnFile lpnObj= lpn2;
		if ( t != null ) {
			int childCount=0;
			if(recursiveCall){
				childCount=1;
			}
			else{
				childCount= t.getChildCount();
			}

			System.out.println("child count is : "+t.getChildCount());
			for(int i=0;i<childCount;i++){
				System.out.println("child is : "+t.getChild(i));	
			} 
			for(int i=0;i<childCount;i++){

				CommonTree switchCaseTree= new CommonTree();

				if(recursiveCall){
					System.out.println("Start of switch statement in recursive call:"+t);
					switchCaseTree=t;
				}
				else {
					System.out.println("Start of switch statement not in recursive call:"+t.getChild(i));
					switchCaseTree=(CommonTree)t.getChild(i);
				}
				switch(switchCaseTree.getType())
				{
				
				case lpn.parser.properties.PropertyLexer.ALWAYS : 
					if(numPlaces==0) pFirst="p0";
					else pFirst = "p"+(numPlaces-1);
					loop=true;
				break;
				case lpn.parser.properties.PropertyLexer.BOOLEAN : 
					varType = "boolean";
					varName = generateExpression((CommonTree)switchCaseTree.getChild(0));
					lpnObj.addInput(varName, varType);
					break;
				case lpn.parser.properties.PropertyLexer.REAL : 
					varType = "real";
					 varName = generateExpression((CommonTree)switchCaseTree.getChild(0));
					 lpnObj.addInput(varName, varType);
					break;
				case lpn.parser.properties.PropertyLexer.INTEGER : 
					 varType = "int";
					 varName = generateExpression((CommonTree)switchCaseTree.getChild(0));
					 lpnObj.addInput(varName, varType);
					break;
				case lpn.parser.properties.PropertyLexer.ASSERT :
					System.out.println("Assert statement ");
					enablingCond= generateExpression((CommonTree)switchCaseTree.getChild(0));
					delay= generateExpression((CommonTree)switchCaseTree.getChild(1));
					if(numPlaces==0){
						lpnObj.addPlace("p"+numPlaces, true);
						numPlaces++;
					}
					lpnObj.addTransition("t" + numTransitions);
					lpnObj.addEnabling("t" +numTransitions, enablingCond);
					lpnObj.changeDelay("t" +numTransitions, delay);
					numTransitions++;
					lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
					lpnObj.addTransition("tFail" + numFailTransitions);
					lpnObj.getTransition("tFail" + numFailTransitions).setFail(true);
					lpnObj.addEnabling("tFail" +numFailTransitions, "~"+enablingCond);

					numFailTransitions++;
					lpnObj.addMovement("p"+(numPlaces-1), "tFail" +(numFailTransitions-1));
					lpnObj.addPlace("pFail"+numFailPlaces, false);
					numFailPlaces++;
					lpnObj.addMovement( "tFail" +(numFailTransitions-1),"pFail"+(numFailPlaces-1));
					lpnObj.addPlace("p"+numPlaces, false);
					numPlaces++;
					lpnObj.addMovement( "t" +(numTransitions-1),"p"+(numPlaces-1));
					break;
				case lpn.parser.properties.PropertyLexer.ASSERT_STABLE :
					
				break;	
				case lpn.parser.properties.PropertyLexer.WAIT_STABLE :
					System.out.println("child is :"+(CommonTree)switchCaseTree.getChild(0));
					enablingCond= generateExpression((CommonTree)switchCaseTree.getChild(0));
					delay= generateExpression((CommonTree)switchCaseTree.getChild(1));
					if(numPlaces==0){
						lpnObj.addPlace("p"+numPlaces, true);
						numPlaces++;
					}
					lpnObj.addTransition("t" + numTransitions);
					lpnObj.addEnabling("t" +numTransitions, enablingCond);
					lpnObj.changeDelay("t" +numTransitions, delay);
					numTransitions++;
					lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
					lpnObj.addPlace("p"+numPlaces, false);
					numPlaces++;
					lpnObj.addMovement( "t" +(numTransitions-1),"p"+(numPlaces-1));
					lpnObj.getTransition("t" +(numTransitions-1)).setPersistent(true);
					break;
				case lpn.parser.properties.PropertyLexer.ASSERT_UNTIL :
					String string1 = generateExpression((CommonTree)switchCaseTree.getChild(0));
					String string2 = generateExpression((CommonTree)switchCaseTree.getChild(1));
					enablingCond= string2;
					if(numPlaces==0){
						lpnObj.addPlace("p"+numPlaces, true);
						numPlaces++;
					}
					lpnObj.addTransition("t" + numTransitions);
					lpnObj.addEnabling("t" +numTransitions, enablingCond);
					
					numTransitions++;
					lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
					lpnObj.addTransition("tFail" + numFailTransitions);
					lpnObj.getTransition("tFail" + numFailTransitions).setFail(true);
					numFailTransitions++;
					enablingCond = "~("+string1+") & "+"~("+string2+")";
					lpnObj.addEnabling("tFail" +(numFailTransitions-1), enablingCond);

					
					lpnObj.addMovement("p"+(numPlaces-1), "tFail" +(numFailTransitions-1));
					lpnObj.addPlace("pFail"+numFailPlaces, false);
					numFailPlaces++;
					lpnObj.addMovement( "tFail" +(numFailTransitions-1),"pFail"+(numFailPlaces-1));
					lpnObj.addPlace("p"+numPlaces, false);
					numPlaces++;
					lpnObj.addMovement( "t" +(numTransitions-1),"p"+(numPlaces-1));
					
				break;
				case lpn.parser.properties.PropertyLexer.ID : 
					System.out.println("Property name ");

					break;
				case lpn.parser.properties.PropertyLexer.INT :
					break;
				case lpn.parser.properties.PropertyLexer.NOT :
					break;
				case lpn.parser.properties.PropertyLexer.PLUS :
					break;
				case lpn.parser.properties.PropertyLexer.MINUS :
					break;
				case lpn.parser.properties.PropertyLexer.MULT :
					break;
				case lpn.parser.properties.PropertyLexer.DIV :
					break;
				case lpn.parser.properties.PropertyLexer.MOD :
					break;
				case lpn.parser.properties.PropertyLexer.EQUAL :
					break;
				case lpn.parser.properties.PropertyLexer.NOT_EQUAL :
					break;
				case lpn.parser.properties.PropertyLexer.GET :
					
					break;
				case lpn.parser.properties.PropertyLexer.LET :
					break;
				case lpn.parser.properties.PropertyLexer.GETEQ :
					break;
				case lpn.parser.properties.PropertyLexer.LETEQ :
					break;

				case lpn.parser.properties.PropertyLexer.AND :
					break;
				case lpn.parser.properties.PropertyLexer.OR :
					break;
				case lpn.parser.properties.PropertyLexer.SAMEAS :
					break;
				case lpn.parser.properties.PropertyLexer.WAIT :
					System.out.println("wait statement: ");
					int count = switchCaseTree.getChildCount();
					if (count==1){
						if(switchCaseTree.getChild(0).toString().matches("posedge")){
						enablingCond= generateExpression((CommonTree)switchCaseTree.getChild(0));
						
						if(numPlaces==0){
							lpnObj.addPlace("p"+numPlaces, true);
							numPlaces++;
						}
						lpnObj.addTransition("t" + numTransitions);
						lpnObj.addEnabling("t" +numTransitions, "~"+enablingCond);
						numTransitions++;
						lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
						
						lpnObj.addPlace("p"+numPlaces, false);
						numPlaces++;
						lpnObj.addMovement( "t" +(numTransitions-1),"p"+(numPlaces-1));
						lpnObj.addTransition("t" + numTransitions);
						lpnObj.addEnabling("t" +numTransitions, enablingCond);
						numTransitions++;
						lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
						
						lpnObj.addPlace("p"+numPlaces, false);
						numPlaces++;
						lpnObj.addMovement( "t" +(numTransitions-1),"p"+(numPlaces-1));
					}
						else{
							enablingCond= generateExpression((CommonTree)switchCaseTree.getChild(0));
							
							if(numPlaces==0){
								lpnObj.addPlace("p"+numPlaces, true);
								numPlaces++;
							}
							lpnObj.addTransition("t" + numTransitions);
							lpnObj.addEnabling("t" +numTransitions, enablingCond);
							numTransitions++;
							lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
							
							lpnObj.addPlace("p"+numPlaces, false);
							numPlaces++;
							lpnObj.addMovement( "t" +(numTransitions-1),"p"+(numPlaces-1));
							
						}
					}
					else if(count==2){
						enablingCond= generateExpression((CommonTree)switchCaseTree.getChild(0));
						delay= generateExpression((CommonTree)switchCaseTree.getChild(1));

						if(numPlaces==0){
							lpnObj.addPlace("p"+numPlaces, true);
							numPlaces++;
						}
						lpnObj.addTransition("t" + numTransitions);
						lpnObj.addEnabling("t" +numTransitions, enablingCond);
						numTransitions++;
						lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
						lpnObj.addTransition("tFail" + numFailTransitions);
						lpnObj.getTransition("tFail" + numFailTransitions).setFail(true);
						lpnObj.addEnabling("tFail" +numFailTransitions, "~("+enablingCond+")");
						lpnObj.changeDelay("tFail" +numFailTransitions, delay);
						
						numFailTransitions++;
						lpnObj.addMovement("p"+(numPlaces-1), "tFail" +(numFailTransitions-1));
						lpnObj.addPlace("pFail"+numFailPlaces, false);
						numFailPlaces++;
						lpnObj.addMovement( "tFail" +(numFailTransitions-1),"pFail"+(numFailPlaces-1));
						lpnObj.addPlace("p"+numPlaces, false);
						numPlaces++;
						lpnObj.addMovement( "t" +(numTransitions-1),"p"+(numPlaces-1));
					} 


					break;

				case lpn.parser.properties.PropertyLexer.IF :
					System.out.println("IF statement");
					if(list.getItemCount()!=0){
						list.removeAll();

					}
					String condition=  generateExpression((CommonTree)switchCaseTree.getChild(0));
					list.add(condition);

					for(int j=0;j<switchCaseTree.getChildCount();j++){
						if(switchCaseTree.getChild(j).getType()==lpn.parser.properties.PropertyLexer.ELSEIF){

							condition=  generateExpression((CommonTree)switchCaseTree.getChild(j).getChild(0));
							list.add(condition);


						}
					}
					if(numPlaces==0){

						lpnObj.addPlace("pStart"+numStartPlaces, true);
						pFirst= "pStart"+numStartPlaces;
						numStartPlaces++;
					
						lpnObj.addPlace("pEnd"+numEndPlaces, false);
						numEndPlaces++;

					}
					else{
						pFirst= "p0";
						lpnObj.addTransition("t" + numTransitions);
						numTransitions++;
						lpnObj.addMovement("p"+(numPlaces-1), "t" +(numTransitions-1));
						lpnObj.addPlace("pStart"+numStartPlaces, false);
						numStartPlaces++;
						lpnObj.addMovement( "t" +(numTransitions-1),"pStart"+(numStartPlaces-1));
						lpnObj.addPlace("pEnd"+numEndPlaces, false);
						numEndPlaces++;
					}

					for(int x=0;x<list.getItemCount();x++){

						System.out.println("list is : "+list.getItem(x)+"\n");
					}
					for(int j=0;j<switchCaseTree.getChildCount();j++){
						if(j==0){
							enablingCond=  generateExpression((CommonTree)switchCaseTree.getChild(0));
							lpnObj.addTransition("t" + numTransitions);
							StringBuffer sb = new StringBuffer();
							String newEnablingCond1= "";

							for(int m=0;m<list.getItemCount();m++){

								if(list.getItem(m).matches(enablingCond)){
									if(m==(list.getItemCount()-1)){
										newEnablingCond1 = "("+list.getItem(m)+")";
									}
									else{
										newEnablingCond1 = "("+list.getItem(m)+")&";
									}
								}
								else{
									if(m==(list.getItemCount()-1)){

										newEnablingCond1 = "~("+list.getItem(m)+")";
									}
									else{
										newEnablingCond1 = "~("+list.getItem(m)+")&";
									}
								}

								sb.append(newEnablingCond1);

							}
							String newEnablingCond = sb.toString();
							System.out.println("newEnablinCondition : "+newEnablingCond+"\n");

							lpnObj.addEnabling("t" +numTransitions, enablingCond);
							numTransitions++;
							lpnObj.addMovement("pStart"+(numStartPlaces-1), "t" +(numTransitions-1));
							lpnObj.addPlace("p"+numPlaces, false);
							numPlaces++;
							lpnObj.addMovement("t" +(numTransitions-1), "p"+(numPlaces-1));

						}

						else if(!(switchCaseTree.getChild(j).getType()==lpn.parser.properties.PropertyLexer.ELSEIF) & !(switchCaseTree.getChild(j).getType()==lpn.parser.properties.PropertyLexer.ELSE)){
							lpnObj=generateLPN((CommonTree)switchCaseTree.getChild(j), lpnObj, true);


						}

					}
					lpnObj.addTransition("t" + numTransitions);
					numTransitions++;
					lpnObj.addMovement("p"+(numPlaces-1),"t" +(numTransitions-1));
					lpnObj.addMovement("t" +(numTransitions-1), "pEnd"+(numEndPlaces-1));
					for(int j=0;j<switchCaseTree.getChildCount();j++){
						if(switchCaseTree.getChild(j).getType()==lpn.parser.properties.PropertyLexer.ELSEIF){
							lpnObj=generateLPN((CommonTree)switchCaseTree.getChild(j), lpnObj, true);
							}
					}
					
					for(int j=0;j<switchCaseTree.getChildCount();j++){
						if(switchCaseTree.getChild(j).getType()==lpn.parser.properties.PropertyLexer.ELSE){
							lpnObj=generateLPN((CommonTree)switchCaseTree.getChild(j), lpnObj, true);
							}
					}
			
					String newEnablingCond1 = "";
					StringBuffer sb = new StringBuffer();
						for(int m=0;m<list.getItemCount();m++){

								if(m==(list.getItemCount()-1)){

									newEnablingCond1 = "~("+list.getItem(m)+")";
								}
								else{
									newEnablingCond1 = "~("+list.getItem(m)+")&";
								}
						sb.append(newEnablingCond1);

						}
						String newEnablingCond = sb.toString();
						
						lpnObj.addTransition("t" + numTransitions);
						lpnObj.addEnabling("t" +numTransitions, newEnablingCond);
					numTransitions++;
					lpnObj.addMovement("pStart"+(numStartPlaces-1),"t" +(numTransitions-1));
					lpnObj.addMovement("t" +(numTransitions-1),"pEnd"+(numEndPlaces-1)); 
					
					lpnObj.addTransition("t" + numTransitions);
					numTransitions++;
					lpnObj.addMovement("pEnd"+(numEndPlaces-1),"t" +(numTransitions-1));
					
					lpnObj.addPlace("p"+numPlaces, false);
					numPlaces++;
					lpnObj.addMovement("t" +(numTransitions-1),"p"+(numPlaces-1));
					break;
				case lpn.parser.properties.PropertyLexer.END :
					break;
				case lpn.parser.properties.PropertyLexer.ELSEIF :
					System.out.println("ELSEIF ");	
					for(int j=0;j<switchCaseTree.getChildCount();j++){

						if(j==0){
							enablingCond=  generateExpression((CommonTree)switchCaseTree.getChild(0));
							if(numPlaces==0){
								lpnObj.addPlace("p"+numPlaces, true);
								numPlaces++;
							}
							lpnObj.addTransition("t" + numTransitions);
							StringBuffer sb2 = new StringBuffer();
							String newEnablingCondition1= "";

							for(int m=0;m<list.getItemCount();m++){

								if(list.getItem(m).matches(enablingCond)){
									if(m==(list.getItemCount()-1)){
										newEnablingCondition1 = "("+list.getItem(m)+")";
									}
									else{
										newEnablingCondition1 = "("+list.getItem(m)+")&";
									}
								}
								else{
									if(m==(list.getItemCount()-1)){

										newEnablingCondition1 = "~("+list.getItem(m)+")";
									}
									else{
										newEnablingCondition1 = "~("+list.getItem(m)+")&";
									}
								}

								sb2.append(newEnablingCondition1);

							}
							String newEnablingCondition = sb2.toString();
							System.out.println("newEnablinCondition in ELSEIF : "+newEnablingCondition+"\n");

							lpnObj.addEnabling("t" +numTransitions, newEnablingCondition);
							numTransitions++;
							lpnObj.addMovement("pStart"+(numStartPlaces-1), "t" +(numTransitions-1));
							lpnObj.addPlace("p"+numPlaces, false);
							numPlaces++;
							lpnObj.addMovement("t" +(numTransitions-1), "p"+(numPlaces-1));
						}
						else if(switchCaseTree.getChild(j).getType()==lpn.parser.properties.PropertyLexer.IF){
							lpnObj=generateLPN((CommonTree)switchCaseTree.getChild(j), lpnObj, true);
						}
						else{

							lpnObj=generateLPN((CommonTree)switchCaseTree.getChild(j), lpnObj, true);
						}

					}
					lpnObj.addTransition("t" + numTransitions);
					numTransitions++;
					lpnObj.addMovement("p"+(numPlaces-1),"t" +(numTransitions-1));
					lpnObj.addMovement("t" +(numTransitions-1), "pEnd"+(numEndPlaces-1));
					break;
				case lpn.parser.properties.PropertyLexer.ELSE :
					System.out.println("ELSE ");	
					
					StringBuffer sb3 = new StringBuffer();
					String newEnablingCondition2= "";

					for(int m=0;m<list.getItemCount();m++){

							if(m==(list.getItemCount()-1)){

								newEnablingCondition2 = "~("+list.getItem(m)+")";
							}
							else{
								newEnablingCondition2 = "~("+list.getItem(m)+")&";
							}

						sb3.append(newEnablingCondition2);

					}
					String newEnablingCond2 = sb3.toString();
					System.out.println("newEnablinCondition in ELSE : "+newEnablingCond2+"\n");
					lpnObj.addTransition("t" + numTransitions);
					lpnObj.addEnabling("t" +numTransitions, newEnablingCond2);
					numTransitions++;
					lpnObj.addMovement("pStart"+(numStartPlaces-1), "t" +(numTransitions-1));
					lpnObj.addPlace("p"+numPlaces, false);
					numPlaces++;
					lpnObj.addMovement("t" +(numTransitions-1), "p"+(numPlaces-1));
					for(int j=0;j<switchCaseTree.getChildCount();j++){

						lpnObj=generateLPN((CommonTree)switchCaseTree.getChild(j), lpnObj, true);
							//enablingCond=  generateExpression((CommonTree)switchCaseTree.getChild(0));
							if(numPlaces==0){
								lpnObj.addPlace("p"+numPlaces, true);
								numPlaces++;
							}
						
					}
					lpnObj.addTransition("t" + numTransitions);
					numTransitions++;
					lpnObj.addMovement("p"+(numPlaces-1),"t" +(numTransitions-1));
					lpnObj.addMovement("t" +(numTransitions-1), "pEnd"+(numEndPlaces-1));
					break;
				case lpn.parser.properties.PropertyLexer.POSEDGE :
					enablingCond=  generateExpression((CommonTree)switchCaseTree.getChild(0));
					System.out.println("");
				break;
				default :
					break;
				}
			}

		}
		
		return lpnObj;
	}

	public static String generateExpression(CommonTree newChild) {

		String result = "";
		String string1= "";
		String string2="";
		if ( newChild != null ) {

			switch (newChild.getType()) {
			case lpn.parser.properties.PropertyLexer.WAIT: 
				break;
			case lpn.parser.properties.PropertyLexer.IF: 
				break;
			case lpn.parser.properties.PropertyLexer.ID : 
				result= newChild.toString();
				System.out.println("String in ID : "+result);
				break;
			case lpn.parser.properties.PropertyLexer.FLOAT:
				result=newChild.toString();
				break;
			case lpn.parser.properties.PropertyLexer.INT	:
				result=newChild.toString();
				System.out.println("String in INT :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.STRING	:
				result=newChild.toString();
				break;
			case lpn.parser.properties.PropertyLexer.POSEDGE	:
				result=generateExpression((CommonTree)newChild.getChild(0));
				break;
			case lpn.parser.properties.PropertyLexer.GET :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + ">" +string2);
				System.out.println("String in GET :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.AND :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "&" +string2);
				System.out.println("String in AND :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.DIV :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "/" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.EQUAL :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "=" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.GETEQ :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + ">=" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.LET :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "<" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.LETEQ :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "<=" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.MINUS :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "-" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.MOD :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "%" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.MULT :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "*" +string2);
				System.out.println("result2 :"+result);
				break;

			case lpn.parser.properties.PropertyLexer.NOT :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				result= ("~" +string1);
				System.out.println("String in NOT :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.NOT_EQUAL :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "!=" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.OR :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "|" +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.PLUS :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + " + " +string2);
				System.out.println("result2 :"+result);
				break;
			case lpn.parser.properties.PropertyLexer.SAMEAS :
				string1= generateExpression((CommonTree)newChild.getChild(0));
				string2= generateExpression((CommonTree)newChild.getChild(1));
				result= (string1 + "=" +string2);
				System.out.println("String in SAMEAS :"+result);
				break;

			default :
				break;
			}

		}
		return result;
	} 

}