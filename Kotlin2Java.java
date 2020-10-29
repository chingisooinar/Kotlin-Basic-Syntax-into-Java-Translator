import java.lang.*;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
class KotlinConverter extends KotlinBaseVisitor<Integer>{
	public ArrayList<String> output=new ArrayList<String>();
	ArrayList<String> constructor=new ArrayList<String>();
	ArrayList<String> superc=new ArrayList<String>();
	HashMap<String,String> typemap=new HashMap<String,String>();
	HashMap<String,String> storage=new HashMap<String,String>();
	HashMap<Integer,ArrayList<String>> classmap =new  HashMap<Integer,ArrayList<String>>();
	HashMap<Integer,String> classStorage=new HashMap<Integer,String>();
	ArrayList<String> getters=new ArrayList<String>();
	ArrayList<String> packages=new ArrayList<String>();
	String currentfunc=null;
	public  String Main="input";
	String rangestmt=null;
	int funcassign=0;
	Boolean isinner=false;
	Boolean explore=false;
	int innercount=1;
	Boolean isinterface=false;
	Boolean parent=false;
	int classpointer=0;
	String currentclass=Main;
	String functype=null;
	int callactive=0;
	int whenstmt=0;
	Boolean separate=false;
	public  void setMain(String ma){
		Main=ma;
		currentclass=Main;
	}	
	@Override public Integer visitProg(KotlinParser.ProgContext ctx) { 
	typemap.put("Int","int");
	classmap.put(0,new ArrayList<String>());
	typemap.put("Any","Object");
	typemap.put("String","String");
	typemap.put("String?","String");
	typemap.put("Int?","Integer");
	typemap.put("int?","Integer");
	typemap.put("String?","String");
	typemap.put("Long?","Long");
	typemap.put("long?","Long");
	typemap.put("double?","Double");
	typemap.put("Unit","void");
	typemap.put("Long","long");
	typemap.put("Double","double");
	typemap.put("Double?","Double");
	typemap.put("Boolean","Boolean");
	typemap.put("Boolean?","Boolean");
	explore=true;
	visitChildren(ctx);
	explore=false;
	return visitChildren(ctx); }

	@Override public Integer visitPackageList(KotlinParser.PackageListContext ctx) { 
	if(explore)return null;
	
	return visitChildren(ctx); }

	@Override public Integer visitPackageP(KotlinParser.PackagePContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitDefinitionP(KotlinParser.DefinitionPContext ctx) { 
		String toadd="";
		packages.add(ctx.getChild(0).getText());
		for(int i=1;i<ctx.getChildCount();i++)toadd+=ctx.getChild(i).getText();
		packages.add(toadd+";\n");
	
	return null; }

	@Override public Integer visitImportP(KotlinParser.ImportPContext ctx) { 
	       	String toadd="";
                packages.add(ctx.getChild(0).getText());
                for(int i=1;i<ctx.getChildCount();i++)toadd+=ctx.getChild(i).getText();
                packages.add(toadd+";\n");

	
	return null; }

	@Override public Integer visitPackagename(KotlinParser.PackagenameContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitList(KotlinParser.ListContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitStart(KotlinParser.StartContext ctx) { 
	
		if(explore){
			for(int i=0;i<ctx.getChildCount();i++){
				if(ctx.getChild(i) instanceof KotlinParser.ClassListContext){
					visit(ctx.getChild(i));
				}
			}
		return null;
		}
	
	return visitChildren(ctx); }

	@Override public Integer visitInterfaceList(KotlinParser.InterfaceListContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitClassList(KotlinParser.ClassListContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitClassDecl(KotlinParser.ClassDeclContext ctx) { 
		if(ctx.getChild(0).getText().equals("abstract"))currentclass=ctx.getChild(2).getText();
		else currentclass=ctx.getChild(1).getText();
		if(explore){
			storage.put(currentclass+"()",currentclass);
			typemap.put(currentclass,currentclass);
			visit(ctx.getChild(ctx.getChildCount()-1));
			return null;

		}
		classpointer+=1;
		isinner=true;
		output.add("static");
		classmap.put(classpointer,new ArrayList<String>());
		for(int i=0;i<ctx.getChildCount();i++){
			if(ctx.getChild(i).getText().equals("(")||ctx.getChild(i).getText().equals(")")||ctx.getChild(i).getText().equals(":"))continue;
			if(ctx.getChild(i).getChildCount()==0)output.add(ctx.getChild(i).getText());
			else visit(ctx.getChild(i));
		}
		currentclass=Main;
		isinner=false;
		return null; }

	@Override public Integer visitClasscompound(KotlinParser.ClasscompoundContext ctx) { 
	        if(explore){
			visit(ctx.getChild(1));
			return null;
		}
		int i=ctx.getChildCount();
                output.add("{");
                output.add("\n");
		for(int j=0;j<constructor.size();j++){
                        if(!constructor.get(j).equals(","))output.add( constructor.get(j)+";\n");
                }

		output.add("public "+currentclass+"(");
		HashMap<String,String> init =new HashMap<String,String>();
		for(int j=0;j<constructor.size();j++){
			//System.out.println(constructor.get(j));
			if(!constructor.get(j).equals(",")){
				String[] args=constructor.get(j).split(" ");
				if(args.length>2)args[0]=args[0]+" "+args[1];
				output.add(args[0]+" "+"temp"+j);
				init.put(args[args.length-1],"temp"+j);
				continue;
			}
			output.add(constructor.get(j));
		}
		output.add("){\n");
		
		if(!superc.isEmpty()){
			output.add("super(");
			//System.out.println("!"+superc.get(j));
			for(int j=0;j<superc.size();j++){
				if(init.keySet().contains(superc.get(j)))output.add(init.get(superc.get(j)));
				else output.add(superc.get(j));
				//System.out.println("!"+superc.get(j));

			}
			output.add(");\n");

		}
		for(String j:init.keySet()){
                        output.add(j+"="+init.get(j)+";\n");

                }

		output.add("}\n");
		constructor.clear();
		superc.clear();
                visitChildren(ctx);
                output.add("}\n");
                //output.add("\n");
                return null; }

	@Override public Integer visitClassStmtList(KotlinParser.ClassStmtListContext ctx) { 
		if(explore){
			visitChildren(ctx);
			return null;

		}
	        for(int i=0;i<ctx.getChildCount();i++){
                        visit(ctx.getChild(i));
                        if(!output.get(output.size()-1).contains("}")||!output.get(output.size()-1).equals("}\n"))output.add(";\n");
                }
                return null; }


	@Override public Integer visitClassStmt(KotlinParser.ClassStmtContext ctx) { 
	if(explore){
		if(ctx.getChild(0) instanceof KotlinParser.GetterContext){
			visit(ctx.getChild(0));
		}
		return null;
	}
	
	return visitChildren(ctx); }

	@Override public Integer visitGetter(KotlinParser.GetterContext ctx) { 
		int offset=0;
		if(ctx.getChild(0).getText().equals("override")){
		//	output.add("@Override\n");
			offset=1;
		}
		if(explore){
			getters.add(ctx.getChild(offset+1).getText());

			return null;
		}
		String var="";
		if(ctx.getChild(offset).getText().equals("val"))var+="private ";
		var+=ctx.getChild(offset+3).getText()+" ";
		var+=ctx.getChild(offset+1).getText();
		output.add(var+";\n");
		storage.put(ctx.getChild(offset+1).getText(),typemap.get(ctx.getChild(offset+3).getText()));
		output.add("public "+typemap.get(ctx.getChild(offset+3).getText())+" get"+ctx.getChild(offset+1).getText());
		output.add("(){\n");
		output.add(ctx.getChild(offset+1).getText()+"=");
		visit(ctx.getChild(offset+8));
		output.add(";\n");
		output.add("return "+ctx.getChild(offset+1).getText()+";");
		output.add("}\n");
		getters.add(ctx.getChild(offset+1).getText());
		
	
		return null; }

	@Override public Integer visitExtendsORimplements(KotlinParser.ExtendsORimplementsContext ctx) { 
		for(int i=0;i<ctx.getChildCount();i++){
                        if(ctx.getChild(i).getChildCount()==0)continue;
                        else visit(ctx.getChild(i));
                }

	
	
	return null; }

	@Override public Integer visitClassORinterface(KotlinParser.ClassORinterfaceContext ctx) { 
	if(ctx.getChildCount()==1){
		output.add("implements "+ctx.getChild(0).getText());
	}else{
		output.add("extends "+ctx.getChild(0).getText());
		int init=output.size();
		visit(ctx.getChild(2));
		for(int i=init;i<output.size();i++){
			superc.add(output.get(i));
		}
		while(output.size()!=init)output.remove(output.size()-1);
		for(Integer i:classStorage.keySet()){
			if(classStorage.get(i).equals(ctx.getChild(0).getText())){
				for(String j:classmap.get(i)){
					classmap.get(classpointer).add(j);
				}
			}
		}
	}
	
	return null; }

	@Override public Integer visitInterfaceDecl(KotlinParser.InterfaceDeclContext ctx) { 
		separate=true;
		isinterface=true;
		for(int i=0;i<ctx.getChildCount();i++){
			if(ctx.getChild(i).getChildCount()==0)output.add(ctx.getChild(i).getText());
			else{
			       	visit(ctx.getChild(i));
				if(!output.get(output.size()-1).contains("}"))output.add(";\n");
			}

		}
		isinterface=false;
		separate=false;
		output.add("\n");
		return null; }

	@Override public Integer visitDeclGlobalList(KotlinParser.DeclGlobalListContext ctx) { 
		for(int i=0;i<ctx.getChildCount();i++){
			output.add("static");
                        visit(ctx.getChild(i));
                        output.add(";\n");
                }

		return null; }

	@Override public Integer visitFuncList(KotlinParser.FuncListContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitFunction(KotlinParser.FunctionContext ctx) {
       		int offset=0;
		if(ctx.getChild(0) instanceof KotlinParser.AbstractFunctionContext){
			visit(ctx.getChild(0));
			return null;
		}
		parent=true;
		if(ctx.getChild(0).getText().equals("override")){
			offset=1;
			output.add("@Override\n");

		}
		int i=1+offset;
		int typeadded=0;
	
		classmap.get(classpointer).add(ctx.getChild(i).getText());
		if(!classStorage.containsKey(classpointer))classStorage.put(classpointer,currentclass);
		int limit=ctx.getChildCount();
		if(ctx.getChild(i).getText().equals("main")){
			output.add("public");
			output.add("static");
			output.add("void");
			output.add("main");
			output.add("(");
			output.add("String[]");
			output.add("args");
			output.add(")");
			visitChildren(ctx);
			parent=false;
			return null;
		}
		
		if(!isinner)output.add("public static");
		else output.add("public");
		int init=output.size();
		while(i<limit-1){
			if(ctx.getChild(i).getText().equals("(") && !ctx.getChild(i+1).getText().equals(")")){
				String parameters=ctx.getChild(i+1).getText();
				String[] ids= parameters.split(",");
				output.add(ctx.getChild(i).getText());
				for(String j:ids){
					String[] args=j.split(":");
					output.add(typemap.get(args[1])+" "+args[0]);
					storage.put(args[0],typemap.get(args[1]));
					output.add(",");
				}
				output.remove(output.size()-1);
				i+=2;
				continue;
			}
			if(!ctx.getChild(i).getText().equals(")")&& (ctx.getChild(i+1).getText().charAt(0)=='{'||ctx.getChild(i+1).getText().charAt(0)=='=')){
				String[] args=ctx.getChild(i).getText().split(":");
                                output.add(init,typemap.get(args[1]));
				storage.put(ctx.getChild(1).getText()+"()",typemap.get(args[1]));
				typeadded=1;
		       		i++;
		 		continue;		
			}
			output.add(ctx.getChild(i).getText());
			i++;
		}	
		if(typeadded==0)output.add(init,"void");
		visit(ctx.getChild(i));
		if(funcassign==1 && typeadded==0){
			funcassign=0;
			output.remove(init);
			output.add(init,functype);
		}
		parent=false;
		return null; 
	}

	@Override public Integer visitAbstractFunction(KotlinParser.AbstractFunctionContext ctx) { 
		int i=1;
		
		output.add("abstract public");
		classmap.get(classpointer).add(ctx.getChild(2).getText());
		if(!classStorage.containsKey(classpointer))classStorage.put(classpointer,currentclass);
		int init=output.size();
		while(i<ctx.getChildCount()){
			if(ctx.getChild(i).getText().equals("fun")){
				i++;
				continue;
			}
			if(ctx.getChild(i).getText().equals("(") && !ctx.getChild(i+1).getText().equals(")")){
                                String parameters=ctx.getChild(i+1).getText();
                                String[] ids= parameters.split(",");
                                output.add(ctx.getChild(i).getText());
                                for(String j:ids){
                                        String[] args=j.split(":");
                                        output.add(typemap.get(args[1])+" "+args[0]);
                                        storage.put(args[0],typemap.get(args[1]));
                                        output.add(",");
                                }
                                output.remove(output.size()-1);
                                i+=2;
                                continue;
                        }
                        if(ctx.getChild(i) instanceof KotlinParser.FuncTypeContext){
                                String[] args=ctx.getChild(i).getText().split(":");
                                output.add(init,typemap.get(args[1]));
                                storage.put(ctx.getChild(2).getText()+"()",typemap.get(args[1]));
                                //typeadded=1;
                                i++;
                                continue;
                        }
                        output.add(ctx.getChild(i).getText());
			i++;


		}
		return null; }

	@Override public Integer visitFuncassign(KotlinParser.FuncassignContext ctx) { 
	
	funcassign=1;
	output.add("{\n");
	output.add("return");
	visit(ctx.getChild(1));
	funcassign=1;
	
	output.add(";");
	output.add("}\n");
	//funcassign=0;
	return null; }

	@Override public Integer visitFuncType(KotlinParser.FuncTypeContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitDeclID(KotlinParser.DeclIDContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitDeclTypeId(KotlinParser.DeclTypeIdContext ctx) { return visitChildren(ctx); }

	public String getType(){
		String type=null;
	               
                try{	
			if(isNumeric(output.get(output.size()-1))&&currentfunc==null&& output.get(output.size()-1).contains("L")){
				type="long";
			}
			else{
                        int temp=Integer.parseInt(output.get(output.size()-1));
                        
                        type="int";
			}
                }catch(Exception e){
			if(currentfunc==null&&isNumeric(output.get(output.size()-1)) && output.get(output.size()-1).contains("f")){
				type="float";
			}
			else{
                        try{
                                double d=Double.parseDouble(output.get(output.size()-1));
                                type="double";
                        }catch(Exception e1){
                                if(currentfunc!=null)type=storage.get(currentfunc);
                                else type="String";
                                
                        }
			}


                }
		return type;
	}
	@Override public Integer visitNoassign(KotlinParser.NoassignContext ctx) { 
	if(ctx.getChild(0).getText().equals("val"))output.add("final");
	if(isinterface)output.add(typemap.get(ctx.getChild(3).getText()+"?"));
	else output.add(typemap.get(ctx.getChild(3).getText()));
	output.add(ctx.getChild(1).getText());
	
	if(isinterface)output.add("=null");
	
	return null; }

	@Override public Integer visitWithassign(KotlinParser.WithassignContext ctx) { 
	visit(ctx.getChild(0));
	int infer=1;
	if(ctx.getChild(0) instanceof KotlinParser.KeywordContext)funcassign=1;
	if(output.get(output.size()-1).equals(";\n")){
		infer=0;
		output.remove(output.size()-1);
	}
	if(output.get(output.size()-1).equals("var"))output.remove(output.size()-1);
	int i=0;
	for(i=1;i<ctx.getChildCount();i++){
		output.add(ctx.getChild(i).getText());
		if(ctx.getChild(i).getText().equals("="))break;
	}
	String ID=output.get(output.size()-2);
	int size=output.size();
	visit(ctx.getChild(i+1));
	if(funcassign!=0){

		storage.put(ID,functype);
		output.add(size-2,functype);
		funcassign=0;
		currentfunc=null;
	}
	funcassign=0;
	//output.add(";\n");
	return null; 
	}

	@Override public Integer visitClassparamList(KotlinParser.ClassparamListContext ctx) { 
	
		for(int i=0;i<ctx.getChildCount();i++){
			if(ctx.getChild(i).getChildCount()==0)constructor.add(ctx.getChild(i).getText());
			else visit(ctx.getChild(i));
		}
		return null; }

	@Override public Integer visitClassParamID(KotlinParser.ClassParamIDContext ctx) { 
	if(ctx.getChildCount()==2){
		if(ctx.getChild(0).getText().equals("val"))constructor.add("final");
		visit(ctx.getChild(1));
	}else{
		visit(ctx.getChild(0));
	}
	
	
	return null; }

	@Override public Integer visitKeyword(KotlinParser.KeywordContext ctx) { 
		if(ctx.getChild(0).getText().equals("val"))output.add("final");
		else output.add("var");
		return visitChildren(ctx); }

	@Override public Integer visitParamID(KotlinParser.ParamIDContext ctx) { 
		String type="";
		String store="";
		for(int i=2;i<ctx.getChildCount();i++){
			String temp=ctx.getChild(i).getText();
			if(ctx.getChild(i) instanceof KotlinParser.TypeContext){
				if(type.contains("<"))temp=typemap.get(temp+"?");
				else temp=typemap.get(temp);
			}
			type+=temp;
		}
		if(!constructor.isEmpty()&&constructor.get(constructor.size()-1).equals("final")){
			constructor.remove(constructor.size()-1);
			store=type;
			type="final "+type;
		}
		constructor.add(type+" "+ctx.getChild(0).getText());
		storage.put(ctx.getChild(0).getText(),store);
		return null; }

	@Override public Integer visitParamList(KotlinParser.ParamListContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitCompoundStmt(KotlinParser.CompoundStmtContext ctx) { 
		int i=ctx.getChildCount();
		output.add("{");
		output.add("\n");
		visitChildren(ctx);
		output.add("}\n");
		//output.add("\n");
		return null; 
	}

	@Override public Integer visitStmtList(KotlinParser.StmtListContext ctx) { 
		for(int i=0;i<ctx.getChildCount();i++){
			visit(ctx.getChild(i));
			if(!output.get(output.size()-1).contains("}")||!output.get(output.size()-1).equals("}\n"))output.add(";\n");
		}
		return null; }

	@Override public Integer visitStmt(KotlinParser.StmtContext ctx) { 
		for(int i=0;i<ctx.getChildCount();i++){
			if(parent&&ctx.getChild(i) instanceof KotlinParser.FunctionContext){
				output.add("class Inner"+innercount+"{\n");
				classpointer=innercount;
				if(parent)isinner=true;
				currentclass="Inner"+classpointer;
				classmap.put(classpointer,new ArrayList<String>());
				visit(ctx.getChild(i));
				output.add("}\n");
				innercount++;
				currentclass=Main;
				continue;
			}
			visit(ctx.getChild(i));
		}
		isinner=false;
		return null; }

	@Override public Integer visitLambdaexpression(KotlinParser.LambdaexpressionContext ctx) { 
		output.add(ctx.getChild(0).getText()+".stream()");
		for(int i=1;i<ctx.getChildCount();i++){
			visit(ctx.getChild(i));
		}
	
		return null; }

	@Override public Integer visitLambdaTerm(KotlinParser.LambdaTermContext ctx) { 
		//output.add("."+ctx.getChild(1).getText()+"(");
		if(ctx.getChild(3) instanceof KotlinParser.CallContext){
			output.add("."+ctx.getChild(1).getText()+"(");
			int size=output.size();
			visit(ctx.getChild(3));
			String method="";
			for(int i=size;i<output.size();i++)method+=output.get(i);

			while(size!=output.size()){
				
				output.remove(output.size()-1);
			}
			
			output.add("it->"+method);
		}else{
			String toadd=ctx.getChild(1).getText();
			if(ctx.getChild(1).getText().equals("sortedBy")){
				toadd="sorted";
			}
			output.add("."+toadd+"(");
		}
		output.add(")");
		return null; }

	@Override public Integer visitForStmt(KotlinParser.ForStmtContext ctx) { 
		output.add("for(");

		if(ctx.getChild(4).getText().contains("..")||ctx.getChild(4).getText().contains("downTo")){
			output.add("int");
			output.add(ctx.getChild(2).getText());
			output.add("=");
			rangestmt="for";
			visit(ctx.getChild(4));
			
		}else{
			int indices=0;
			String toadd=ctx.getChild(4).getText();
			if(toadd.contains(".")){
				
				String temp="";
				
				for(int i=0;toadd.charAt(i)!='.';i++)
					temp+=toadd.charAt(i);
				if(toadd.contains("indices")){
					indices=1;
					
					toadd=temp;
				}
				else toadd=temp;
		
			}
			String type=storage.get(toadd);
			if(type.contains("<")){
				int i=0;
				for( i=0;type.charAt(i)!='<';i++);
				i++;
				String temp="";
				for(int j=i;type.charAt(j)!='>';j++)
					temp+=type.charAt(j);
				type=temp;

			}
			output.add(type);
			output.add(ctx.getChild(2).getText());
			if(indices==1){
				output.add("=0;"+ctx.getChild(2).getText()+"<="+toadd+".size()-1"+";"+ctx.getChild(2).getText()+"++");
			}else{
			output.add(":");
			output.add(toadd);
			}
		}
		
		output.add(")");
		visit(ctx.getChild(6));
		return null; }

	@Override public Integer visitCallStmt(KotlinParser.CallStmtContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitWhileStmt(KotlinParser.WhileStmtContext ctx) { 
		rangestmt="while";	
		output.add("while(");
		visit(ctx.getChild(2));
		output.add(")");
		visit(ctx.getChild(4));
		rangestmt=null;
		return null; }

	@Override public Integer visitWhenStmt(KotlinParser.WhenStmtContext ctx) { 
		if(funcassign!=0){
			output.remove(output.size()-1);
			whenstmt=3;
			funcassign=0;

		}
		if(ctx.getChild(1).getText().equals("(")){
			if(whenstmt!=3)whenstmt=1;
			//output.add(ctx.getChild(2).getText());
			for(int i =5;!ctx.getChild(i).getText().equals("}");i++){
				if(i!=5)output.add("else");
				output.add(ctx.getChild(2).getText());
				visit(ctx.getChild(i));
			}
		}
		else{
		       	whenstmt=2;
			for(int i =2;!ctx.getChild(i).getText().equals("}");i++){
				if(i!=2)output.add("else");
				visit(ctx.getChild(i));
			}
		}
		output.remove(output.size()-1);
		if(whenstmt==3){
			functype=getType();
		}
		
		//visitChildren(ctx); 
		whenstmt=0;
		return null;
	}

	@Override public Integer visitWhenexpr(KotlinParser.WhenexprContext ctx) { 
		if(whenstmt==1||whenstmt==3){
			String obj=output.get(output.size()-1);
			output.remove(output.size()-1);
		
				
			if(!ctx.getChild(0).getText().equals("else"))output.add("if(");
			//if(ctx.getChild(0).getText().equals("else")){
				//output.add("else");
			//}
			if(ctx.getChild(0).getChild(0) instanceof KotlinParser.ExpressionContext){
				//if(storage.get(obj).equals("Object"))output.add("
				if(!storage.get(obj).equals("Object"))output.add(obj+"==");
				else output.add(obj+".equals(");
				visit(ctx.getChild(0));
				if(storage.get(obj).equals("Object"))output.add(")");
			}
			else if(ctx.getChild(0).getChild(0) instanceof KotlinParser.StringLiteralContext){
                                output.add(obj+".equals(");
                                visit(ctx.getChild(0));
				output.add(")");
                        }
			else if(ctx.getChild(0).getChild(0) instanceof KotlinParser.LogexpressionContext){
                                output.add(obj);
                                visit(ctx.getChild(0));
                               
                        }


			if(!ctx.getChild(0).getText().equals("else"))output.add(")");
			if(whenstmt==3)output.add("return");

			visit(ctx.getChild(2));
			output.add(";\n");

		}else if(whenstmt==2){
			if(!ctx.getChild(0).getText().equals("else"))output.add("if(");
                        if(ctx.getChild(0).getText().equals("else")){
                                output.add("else");
                        }
			visit(ctx.getChild(0));

			if(!ctx.getChild(0).getText().equals("else"))output.add(")");
                        if(whenstmt==3)output.add("return");

                        visit(ctx.getChild(2));
                        output.add(";\n");

		
		}
		return null; 
	}

	@Override public Integer visitIfStmt(KotlinParser.IfStmtContext ctx) { 
		rangestmt="if";
		visitChildren(ctx); 
		rangestmt=null;
		return null;}

	@Override public Integer visitWithElse(KotlinParser.WithElseContext ctx) { 
		output.add("if(");
		visit(ctx.getChild(2));
		output.add(")");
		functype=null;
		visit(ctx.getChild(4));
		output.add("else");
		visit(ctx.getChild(6));
		return null; }

	@Override public Integer visitNoElse(KotlinParser.NoElseContext ctx) { 
		output.add("if(");
                visit(ctx.getChild(2));
                output.add(")");
                visit(ctx.getChild(4));
		
		return null; }

	@Override public Integer visitCondition(KotlinParser.ConditionContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitExplicitRange(KotlinParser.ExplicitRangeContext ctx) { 
		if(rangestmt.equals("for")){
			String i=output.get(output.size()-2);
			//output.add(ctx.getChild(0).getText()+";");
			String log="<=";
			String op="+";
			if(ctx.getChild(1).getText().equals("downTo")){
				log=">=";
				op="-";
			}
			String step=null;
			if(ctx.getChildCount()==5)step=ctx.getChild(4).getText();
			visit(ctx.getChild(0));
			output.add(";"+i+log);
			visit(ctx.getChild(2));
			output.add(";"+i+"="+i+op);
			if(step==null)output.add("1");
			else visit(ctx.getChild(4));

			rangestmt=null;
			return null;	
		}else if(rangestmt.equals("if")||rangestmt.equals("while")){	
			String id=output.get(output.size()-1);
			output.add(">=");
			visit(ctx.getChild(0));
			output.add("&&");
			output.add(id);
			output.add("<=");
			visit(ctx.getChild(2));
			rangestmt=null;
			return null;
		}
	
		return visitChildren(ctx); }

	@Override public Integer visitIdentifierStmt(KotlinParser.IdentifierStmtContext ctx) { 
		if(ctx.getChildCount()>1){
			int i=0;
			for(i=0;i<ctx.getChildCount();i++){
				output.add(ctx.getChild(i).getText());
				if(ctx.getChild(i).getText().contains("="))break;

			}
			visit(ctx.getChild(i+1));
			//output.add(";\n");
			return null;
		}
		return visitChildren(ctx); }

	@Override public Integer visitDeclare(KotlinParser.DeclareContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitAssignexpr(KotlinParser.AssignexprContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitShortIf(KotlinParser.ShortIfContext ctx) { 
		if(ctx.getChildCount()==5){
			output.add("if(");
			visit(ctx.getChild(2));
			output.add(")");
			visit(ctx.getChild(4));
		//	return null;
		}
		else{	
		visit(ctx.getChild(2));
		output.add("?");
		if(funcassign!=0){
			functype=null;
			funcassign=1;
		}

		visit(ctx.getChild(4));
		output.add(":");
		visit(ctx.getChild(6));
		}

		return null; }

	@Override public Integer visitRetStmt(KotlinParser.RetStmtContext ctx) { 
		int restore=0;
		/*if(output.get(output.size()-1).equals("}")){
			restore=1;
			output.remove(output.size()-1);

		}*/
		//int i=0;
		for(int i=0;i<ctx.getChildCount();i++){
			if(ctx.getChild(i).getChildCount()==0)output.add(ctx.getChild(i).getText());
                        else visit(ctx.getChild(i));


		}
		//output.add(";\n");
		/*if(restore==1){
			output.add("}");
		}*/
		return null; 
	}
	public static boolean isNumeric(String strNum) {
   		 if (strNum == null) {
        		return false;
    		}
    		try {
        		double d = Double.parseDouble(strNum);
    		} catch (NumberFormatException nfe) {
			return false;
    		}
    		return true;
		}
	public Integer spread(KotlinParser.ExpressionContext ctx) {
		return visitChildren(ctx);
	}
	@Override public Integer visitExpression(KotlinParser.ExpressionContext ctx) {
       		
		if(funcassign==1){
		
			funcassign=2;
			spread(ctx);
			funcassign=3;

			

		}
		if(funcassign==2)return visitChildren(ctx);
		if(funcassign!=2){
		for(int i=0;i<ctx.getChildCount();i++){
			if(ctx.getChild(i).getChildCount()==0)output.add(ctx.getChild(i).getText());
			else visit(ctx.getChild(i));
		}	
		
		}
		
		return null; }

	@Override public Integer visitSuffix(KotlinParser.SuffixContext ctx) { return visitChildren(ctx); }
	int priority(String id){
		switch(id){
			case "int": return 1;
			case "Integer": return 1;
			case "Long":return 1;
			case "long":return 1;
			case "float": return 2;
			case "Float": return 2;
			case "double":return 3;
			case "Double":return 3;
			case "String": return 4;
			case "Boolean":return 4;
		}
		return 0;

	}
	 @Override public Integer visitListID(KotlinParser.ListIDContext ctx) {
	 output.add(ctx.getChild(0).getText()+".get("+ctx.getChild(2).getText()+")");
	 return null;

	 }
	@Override public Integer visitExprID(KotlinParser.ExprIDContext ctx) { 
		String toadd=ctx.getChild(0).getText();

		if(funcassign==1 || funcassign==2){
			if(functype==null){
				functype=storage.get(toadd);

			}else{
				if(priority(functype)<priority(storage.get(toadd))){
					functype=storage.get(toadd);
				}
			}
		}else{
			toadd=ctx.getChild(0).getText();
			if(getters.contains(toadd))toadd="get"+toadd+"()";
			if(ctx.getChildCount()==3){
				if(ctx.getChild(2).getText().equals("length")){
					if(storage.get(ctx.getChild(0).getText()).equals("Object")){
						toadd="((String) "+ctx.getChild(0).getText()+")";

					}
					toadd+="."+ctx.getChild(2).getText()+"()";

				}
				else if(ctx.getChild(2).getText().equals("size")){
                                        toadd+="."+ctx.getChild(2).getText()+"()";

                                }
				else if(ctx.getChild(2).getText().equals("lastIndex")||ctx.getChild(2).getText().equals("indices")){
					toadd+=".size()-1";
				}
				else if(getters.contains(ctx.getChild(2).getText())){
					toadd+=".get"+ctx.getChild(2).getText()+"()";
				}
				else{

					toadd+="."+ctx.getChild(2).getText();
				}
				

			}
			output.add(toadd);

		}
		return null; }

	@Override public Integer visitLogexpression(KotlinParser.LogexpressionContext ctx) { 
		if(funcassign==1){
			functype="Boolean";
		}
		for(int i=0;i<ctx.getChildCount();i++){

			if(ctx.getChild(i).getChildCount()==0)output.add(ctx.getChild(i).getText());
                        else visit(ctx.getChild(i));


		}
		return null; }

	@Override public Integer visitLogsides(KotlinParser.LogsidesContext ctx) { 
		if(ctx.getChildCount()==1){
			visit(ctx.getChild(0));
			return null;
		}
		if(ctx.getChildCount()==2){
			if(!ctx.getChild(0).getText().equals("!")&&ctx.getChild(1) instanceof KotlinParser.LogsidesContext){
				visit(ctx.getChild(0));
				visit(ctx.getChild(1));
				return null;
			}
			if(ctx.getChild(0).getText().equals("is")){

				output.add("instanceof");
				output.add(typemap.get(ctx.getChild(1).getText()+"?"));
				return null;
			}
			if(ctx.getChild(0).getText().equals("!")){
				
				output.add(output.size()-1,"!(");
				visit(ctx.getChild(1));
				output.add(")");
				return null;

			}
			if(ctx.getChild(0).getText().equals("in")){
				

				if((rangestmt!=null&&(rangestmt.equals("if")||rangestmt.equals("while")))&& ctx.getChild(1) instanceof KotlinParser.ExplicitRangeContext){
					visit(ctx.getChild(1));


				}else{
					if(ctx.getChild(1).getText().contains(".")){
					String ob=">="+"0"+" && "+output.get(output.size()-1)+"<=";
					output.add(ob);
					visit(ctx.getChild(1));
					}else{
						String obj=output.get(output.size()-1);
						output.remove(output.size()-1);
						output.add(ctx.getChild(1).getText()+"."+"contains("+obj+")");
					}
	

				}
		

			}
			
                        


		}
	
		return null; }

	@Override public Integer visitCall(KotlinParser.CallContext ctx) { 
		int restore=0;

		int skip=0;
                int i=0;
		if(funcassign==1){
			funcassign=0;
			restore=1;
		}
		
		int end=1;
		
		if(callactive==1 && ctx.getChildCount()==1)visitChildren(ctx);

                if(output.get(output.size()-1).contains("=") || funcassign==1)end=0;
		if(ctx.getChildCount()>4){
			if(ctx.getChild(2).getText().equals("toIntOrNull")){
				String obj=ctx.getChild(0).getText();
				if(storage.containsKey(obj) && storage.get(obj).equals("Object")){
					obj="((String)"+obj+")";
				}
				output.add(obj+".matches(\"-?\\\\d+(\\\\.\\\\d+)?\")"+"?"+"Integer.parseInt("+obj+"):null");	
				return null;
			}
			else if(ctx.getChild(2).getText().equals("sum")){
				//System.out.println("scream"+storage.get(ctx.getChild(0).getText()));
				int k=0;
				for(;storage.get(ctx.getChild(0).getText()).charAt(k)!='<';k++);
				k++;
				String type="";
				for(;storage.get(ctx.getChild(0).getText()).charAt(k)!='>';k++)type+=storage.get(ctx.getChild(0).getText()).charAt(k);
				output.add(ctx.getChild(0).getText()+"."+"stream().mapTo"+type+"("+type+"::"+typemap.get(type)+"Value).sum()");
				return null;

			}
			else{
			output.add(ctx.getChild(0).getText()+"."+ctx.getChild(2).getText()+"(");
			callactive=1;
			if(ctx.getChildCount()==6)visit(ctx.getChild(4));
			callactive=0;

			}

		}
		else if(ctx.getChildCount()==4){
			if(ctx.getChild(0).getText().equals("print") || ctx.getChild(0).getText().equals("println"))output.add("System.out."+ctx.getChild(i).getText()+"(");
			else if(ctx.getChild(0).getText().equals("listOf")){
				output.add("List.of(");
			}
			else if(ctx.getChild(0).getText().equals("setOf")){
				output.add("Set.of(");

			}
			else{	
				String clas="";
				
				for(int j=0;j<=classpointer;j++){
					if(classmap.get(j).contains(ctx.getChild(0).getText())){
						clas=classStorage.get(j);
					}
				}
				if(currentclass.equals(clas))clas="";
				else if(clas.equals(""))clas="new ";
				else clas="(new "+clas+"()).";
				
			       	output.add(clas+ctx.getChild(0).getText()+"(");
				
			}
			//if(end==0)currentfunc=ctx.getChild(0).getText();
			//output.add("(");
			callactive=1;
                        visit(ctx.getChild(2));
                      	callactive=0;

		}else{
			 if(ctx.getChild(0).getText().equals("print") || ctx.getChild(0).getText().equals("println"))output.add("System.out."+ctx.getChild(i).getText()+"(");
			 else if(ctx.getChild(0).getText().equals("Any")){
				output.add("new Object(");
			}else{
				
				String clas="";

                                for(int j=0;j<=classpointer;j++){
                                        if(classmap.get(j).contains(ctx.getChild(0).getText())){
                                                clas=classStorage.get(j);
                                        }
                                }
                                if(currentclass.equals(clas))clas="";
                                else clas="(new "+clas+"()).";
                                output.add(clas+ctx.getChild(0).getText()+"(");


			}

		}

		if(output.get(output.size()-1).equals(";\n"))output.remove(output.size()-1);
		if(ctx.getChild(0).getText().equals("listOf")){
                        storage.put("listOf()","List<"+typemap.get(getType()+"?")+">");

                }
		if(ctx.getChild(0).getText().equals("setOf")){
                        storage.put("setOf()","Set<"+typemap.get(getType()+"?")+">");

                }

		if(end==0)currentfunc=ctx.getChild(0).getText()+"()";
		if(restore==1)funcassign=1;
	
		if(funcassign==1){
			if(!ctx.getChild(1).getText().equals("."))functype=storage.get(currentfunc);
			else functype=storage.get(ctx.getChild(0).getText());
		}
		output.add(")");


		return null; 
	}

	@Override public Integer visitCallName(KotlinParser.CallNameContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitType(KotlinParser.TypeContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitExpr(KotlinParser.ExprContext ctx) { return visitChildren(ctx); }

	@Override public Integer visitArgList(KotlinParser.ArgListContext ctx) { 
		for(int i=0;i<ctx.getChildCount();i++){
			if(i%2==0){
				visit(ctx.getChild(i));
			}else{
				output.add(",");
			}
		}
		return null; }

	@Override public Integer visitStringLiteral(KotlinParser.StringLiteralContext ctx) { 
			                      
	                  return visitChildren(ctx); 
	}

	@Override public Integer visitStringContent(KotlinParser.StringContentContext ctx) { 
		if(output.get(output.size()-1).equals("}"))return visitChildren(ctx);
		int restore=0;
		int end=1;
		if(output.get(output.size()-1).contains("="))end=0;
		String toadd="\"";
                if(output.get(output.size()-1).equals("}")){
                        restore=1;
                        output.remove(output.size()-1);

                }
                int i=0;
                for(i=0;i<ctx.getChildCount();i++){
                        toadd=toadd+ctx.getChild(i).getText()+" ";
                }
		output.add(toadd.trim()+"\"");
		if(callactive==0){
                //if(end==1)output.add(";\n");
                if(restore==1){
                        output.add("}");
                }
		}
		if(funcassign==1)functype="String";
		return visitChildren(ctx); }

	@Override public Integer visitNum(KotlinParser.NumContext ctx) { 
		String temp="";
		for(int i=0; i<ctx.getChildCount();i++){
			temp+=ctx.getChild(i).getText();
		}
		if(funcassign==1|| funcassign==2){
					if(functype==null){
						//System.out.println(temp+":("+functype);
						output.add(temp);
                                		functype=getType();
						output.remove(output.size()-1);


                        		}else{

					//System.out.println(temp+":)"+functype);
                                        output.add(temp);
                                        if(priority(functype)<priority(getType()))functype=getType();
                                        output.remove(output.size()-1);}
                }else{

			output.add(temp);
		}

	return visitChildren(ctx); }
	public void print(String file) throws Exception{
		PrintWriter writer=new PrintWriter(new FileOutputStream(file));
		if(!packages.isEmpty()){

		 for(String i:packages){
                 writer.print(i+" ");
                }
		}
		writer.println("import java.util.*;");
		
		if(!output.isEmpty()){
		writer.println("public class "+Main+"{");
		for(String i:output){
                        writer.print(i+" ");
                }
		writer.println("}");
		}
		writer.flush();
		//writer.println("}");
		writer.close();


	}
}
public class Kotlin2Java{
        public static void main(String[] args) throws IOException{
                //File file=new File(args[0]);
                InputStream in=new FileInputStream(args[0]);
                KotlinLexer lexer=new KotlinLexer(CharStreams.fromStream(in));
                CommonTokenStream tokens=new CommonTokenStream(lexer);
                KotlinParser parser=new KotlinParser(tokens);
                ParseTree walker=parser.prog();
                //evallis listener=new evallis();
		KotlinConverter visitor=new KotlinConverter();
		String file="input.java";
		if(args.length==2){
			file=args[1].replace("[","");
			file=file.replace("]","");
			String coms="";
			for(int i=0;file.charAt(i)!='.';i++)coms+=file.charAt(i);
			//System.out.println(coms);
			visitor.setMain(coms);
		}
		visitor.visit(walker);
		try{
		visitor.print(file);
		}catch(Exception e){
			System.out.println("error");
		}		//wa:lker.walk(listener,parser.prog());

        }
}

