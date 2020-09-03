/* Kotlin Basic Syntax */
grammar Kotlin;

//Parser rules
prog: packageList? list?;
packageList:packageP+;
packageP: definitionP|importP;
definitionP: 'package' packagename;
importP: 'import' packagename ('.''*')?;
packagename:ID ('.'ID)*;

list:start|list start;

start: declGlobalList|funcList|classList|interfaceList;

interfaceList:interfaceDecl+;

classList:classDecl+;
classDecl:('abstract')? 'class' ID '(' classparamList? ')'(':'extendsORimplements)? classcompound;
classcompound:'{'classStmtList?'}';

classStmtList:classStmt | classStmtList classStmt;
classStmt:stmt|getter;
getter:('override')? keyword ID (':'type|('List'|'Set')'<'type'>') 'get''('')' '=' expr;
extendsORimplements:classORinterface| extendsORimplements','classORinterface;
classORinterface:ID|ID'('argList?')';

interfaceDecl:'interface' ID '{'declID* function*'}';

declGlobalList:(withassign)+;
funcList: function+;

function: ('override')? 'fun' ID '(' paramList? ')' funcType? (compoundStmt|funcassign)|abstractFunction;
abstractFunction: 'abstract''fun' ID '(' paramList?')' funcType?;

funcassign:'='(assignexpr|withElse|whenStmt);

funcType: ':'type('?')?;

declID:('override')? declTypeId;
declTypeId:withassign|noassign;
noassign:keyword ID (':'type|('List'|'Set')'<'type'>');
withassign:noassign'='assignexpr
	|keyword ID '=' assignexpr;
classparamList: classParamID
          | classparamList ',' classParamID;
classParamID:paramID|keyword paramID;
keyword:'val'|'var';

paramID:ID':'(type|('List'|'Set')'<'type'>');
paramList: paramID
	  | paramList ',' paramID;

compoundStmt: '{' stmtList? '}';

stmtList: stmt+;

stmt: callStmt
	|retStmt
	|expression
	|ifStmt
	|identifierStmt
	|logexpression
	|function
	|forStmt
	|whileStmt
	|whenStmt
	|lambdaexpression;
lambdaexpression:ID lambdaTerm+;
lambdaTerm:'.'ID'{'(call|ID)'}';
forStmt:'for''('ID 'in' (exprID|explicitRange) ')'compoundStmt;

callStmt: call;

whileStmt:'while''(' condition ')'compoundStmt;

whenStmt:'when' '(' ID ')''{' whenexpr+ '}'
	|'when''{'whenexpr+'}';

whenexpr:expr '->' (expr|retStmt)
	|'else''->' (expr|retStmt);

ifStmt: withElse|noElse|shortIf;
withElse: 'if''(' condition ')' compoundStmt 'else' compoundStmt ;

noElse:'if''(' condition ')' compoundStmt ;
condition: call 
	|logexpression;

explicitRange: (expression|call) ('..'|'downTo')(expression|call) ('step' (expression|call))?;
identifierStmt:declare|exprID ('+='|'*='|'/='|'-='|'=') assignexpr;
declare:declID;


assignexpr: expr
	|shortIf;
shortIf:'if''(' condition ')' (expr|retStmt) ('else' (expr|retStmt))?;

retStmt:'return' (expr)?;

expression:num
	|listID
	|exprID
	|'(' expression ')'
	|expression ('*'|'/')expression
	|expression('+'|'-')expression
	|exprID('++'|'--');
suffix: 'L'|'f'|'F';
exprID:ID|ID'.'ID;
listID:ID'['(ID|INT)']';
logexpression: logexpression('>'|'<'|'!='|'=='|'>='|'<=')logexpression
	|logexpression('&&'|'||')logexpression
	|logsides;
	

logsides:'!'logsides
	|'is' type
	|'in' (explicitRange|exprID)
	|(expression|stringLiteral) logsides?;

call: callName'(' argList? ')'| ID'.'ID'(' argList? ')';

callName: ID| 'Any';
type:'Int' | 'Unit' | 'Long' | 'String' | 'Double' | 'Boolean'|'Byte'|'Any';

expr: call 
	|stringLiteral
	|expression
	|logexpression;

argList: expr | argList ',' expr;

stringLiteral: '"' ( stringContent )*'"' ;
stringContent: ~('\\' | '/*'|'"' )+ | '$'|';'|'\''|'?'|'['|']'|'!'|'.';
num:(INT|REAL)suffix?;


//Lexer rules
INT:[-]?[0-9]+;
REAL:[-]?[0-9]+'.'[0-9]+;
ID: [A-Za-z]+[0-9]*;
WHITESPACE:  [ \t]+ -> skip;
NEWLINE : ('\r'?'\n'|'\r') -> skip;
COMMENT: '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT: '/*' .*? '*/' -> skip;
