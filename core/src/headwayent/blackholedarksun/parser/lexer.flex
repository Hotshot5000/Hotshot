package headwayent.blackholedarksun.parser;

import java.util.HashMap;import headwayent.blackholedarksun.parser.ast.InitialCondParam;import java_cup.runtime.*;
import headwayent.blackholedarksun.parser.*;

/**
 * This class is a simple example lexer.
 */
%%

%class Lexer
%unicode
%cup
%line
%column

%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Identifier = [:jletter:] [:jletterdigit:]*

IntegerLiteral = [-+]?[0-9]+
FloatLiteral = [-+]?([0-9]*\.[0-9]+|[0-9]+)
Comma = ","

%state STRING

%%

/* keywords */
//<YYINITIAL> "abstract"           { return symbol(sym.ABSTRACT); }
//<YYINITIAL> "boolean"            { return symbol(sym.BOOLEAN); }
//<YYINITIAL> "break"              { return symbol(sym.BREAK); }

{IntegerLiteral}            { return symbol(sym.NUMBER, new Integer(yytext())); }
{FloatLiteral}            { return symbol(sym.FLOAT, new Float(yytext())); }
{Comma}                     { return symbol(sym.COMMA); }

<YYINITIAL> "cutscene"              { return symbol(sym.CUTSCENE); }
<YYINITIAL> "initial_conds"              { return symbol(sym.INITIAL_CONDS); }
<YYINITIAL> "use_skybox_data_from_level"              { return symbol(sym.use_skybox_data_from_level); }

<YYINITIAL> "set_speed"              { return symbol(sym.set_speed); }
<YYINITIAL> "change_speed"              { return symbol(sym.change_speed); }
<YYINITIAL> "change_position"              { return symbol(sym.change_position); }
<YYINITIAL> "change_orientation"              { return symbol(sym.change_orientation); }
<YYINITIAL> "completion_time"              { return symbol(sym.completion_time); }

<YYINITIAL> "skybox"              { return symbol(sym.skybox); }
<YYINITIAL> "parallel_task"              { return symbol(sym.parallel_task); }
<YYINITIAL> "light_dir"              { return symbol(sym.light_dir); }
<YYINITIAL> "light_type"              { return symbol(sym.light_type); }
<YYINITIAL> "light_power_scale"              { return symbol(sym.light_power_scale); }
<YYINITIAL> "light_diffuse_color"              { return symbol(sym.light_diffuse_color); }
<YYINITIAL> "light_specular_color"              { return symbol(sym.light_specular_color); }
<YYINITIAL> "ambient_light_upperhemi_lowerhemi_dir"              { return symbol(sym.ambient_light_upperhemi_lowerhemi_dir); }

<YYINITIAL> "obj_def"              { return symbol(sym.obj_def); }

<YYINITIAL> "mesh"              { return symbol(sym.mesh); }
<YYINITIAL> "type"              { return symbol(sym.type); }
<YYINITIAL> "position"              { return symbol(sym.position); }
<YYINITIAL> "pos"              { return symbol(sym.position); }
<YYINITIAL> "orientation"              { return symbol(sym.orientation); }
<YYINITIAL> "speed"              { return symbol(sym.speed); }
<YYINITIAL> "ai"              { return symbol(sym.ai); }
<YYINITIAL> "friendly"              { return symbol(sym.friendly); }
<YYINITIAL> "health"              { return symbol(sym.health); }
<YYINITIAL> "behavior"              { return symbol(sym.behavior); }
<YYINITIAL> "start_delay"              { return symbol(sym.start_delay); }
<YYINITIAL> "end_delay"              { return symbol(sym.end_delay); }

<YYINITIAL> "obj"              { return symbol(sym.object); }

<YYINITIAL> "show_text"              { return symbol(sym.show_text); }
<YYINITIAL> "show_text_pos"              { return symbol(sym.show_text_pos); }
<YYINITIAL> "play_sound"              { return symbol(sym.play_sound); }
<YYINITIAL> "play_sound_from_pos"              { return symbol(sym.play_sound_from_pos); }
<YYINITIAL> "play_sound_from_obj_pos"              { return symbol(sym.play_sound_from_obj_pos); }

<YYINITIAL> "secs"              { return symbol(sym.secs); }
<YYINITIAL> "ms"              { return symbol(sym.msecs); }
<YYINITIAL> "msecs"              { return symbol(sym.msecs); }

<YYINITIAL> "obj_event"              { return symbol(sym.obj_event); }
<YYINITIAL> "camera_event"              { return symbol(sym.camera_event); }
<YYINITIAL> "camera_attach"              { return symbol(sym.camera_attach_event); }
<YYINITIAL> "camera_detach"              { return symbol(sym.camera_detach_event); }

<YYINITIAL> "attach"              { return symbol(sym.attach); }
<YYINITIAL> "detach"              { return symbol(sym.detach); }
<YYINITIAL> "look_at"              { return symbol(sym.look_at); }
<YYINITIAL> "look_at_pos"              { return symbol(sym.look_at_pos); }
<YYINITIAL> "spawn"              { return symbol(sym.spawn); }
<YYINITIAL> "exit"              { return symbol(sym.exit); }


//<YYINITIAL> "initial_conds_param_list"              {  new HashMap<String, InitialCondParam>(); }

<YYINITIAL> {
  /* identifiers */
//  {Identifier}                   { return symbol(sym.IDENTIFIER); }

  /* literals */
  //{DecIntegerLiteral}            { return symbol(sym.INTEGER_LITERAL); }
  \"                             { string.setLength(0); yybegin(STRING); }

  /* operators */
//      "="                            { return symbol(sym.EQ); }
//      "=="                           { return symbol(sym.EQEQ); }
//      "+"                            { return symbol(sym.PLUS); }

  "{"                            { return symbol(sym.BRACE_OPEN); }
  "}"                            { return symbol(sym.BRACE_CLOSE); }
  //";"                            { return symbol(sym.SEMICOLON); }

  /* comments */
  {Comment}                      { /* ignore comment */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore whitespace */ }
}

<STRING> {
  \"                             { yybegin(YYINITIAL);
                                   return symbol(sym.STRING_LITERAL,
                                   string.toString()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}

/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                    yytext()+">"); }