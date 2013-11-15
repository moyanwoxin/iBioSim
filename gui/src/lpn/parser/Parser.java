/* Generated By:JavaCC: Do not edit this line. Parser.java */
  package lpn.parser;

  public class Parser implements ParserConstants {

    public Parser (String property) {
      this(new java.io.ByteArrayInputStream(property.getBytes()));
    }

    public boolean parseProperty() {
      try {
        Goal();
        return true;
      }
      catch (Exception e) {
        System.out.println(e.toString());
        return false;
      }
    }

    public static void main(String [] args) {
      try {
         Parser p = new Parser(System.in);
         //Parser p = new Parser(args[0]);
         p.Goal();
      }
      catch (Exception e) {
          System.out.println(e.toString());
      }

    }

  final public void Goal() throws ParseException {
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case MINF:
      case MAXF:
      case IDIV:
      case FLOOR:
      case CEIL:
      case UNIFORM:
      case NORMAL:
      case EXPONENTIAL:
      case GAMMA:
      case LOGNORMAL:
      case CHISQ:
      case LAPLACE:
      case CAUCHY:
      case RAYLEIGH:
      case POISSON:
      case BINOMIAL:
      case BERNOULLI:
      case BITNOT:
      case BITOR:
      case BITAND:
      case BITXOR:
      case INT:
      case BOOL:
      case BIT:
      case RATE:
      case OBRACE:
      case BOOL_FALSE:
      case BOOL_TRUE:
      case EG:
      case EF:
      case AG:
      case AF:
      case Pr:
      case Ss:
      case INF:
      case ID:
      case INTEGER:
      case REAL:
      case MINUS:
      case OPAR:
      case NOT:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      Property();
    }
    jj_consume_token(0);
  }

  final public void Property() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OBRACE:
    case EG:
    case EF:
    case AG:
    case AF:
      Props();
      break;
    case MINF:
    case MAXF:
    case IDIV:
    case FLOOR:
    case CEIL:
    case UNIFORM:
    case NORMAL:
    case EXPONENTIAL:
    case GAMMA:
    case LOGNORMAL:
    case CHISQ:
    case LAPLACE:
    case CAUCHY:
    case RAYLEIGH:
    case POISSON:
    case BINOMIAL:
    case BERNOULLI:
    case BITNOT:
    case BITOR:
    case BITAND:
    case BITXOR:
    case INT:
    case BOOL:
    case BIT:
    case RATE:
    case BOOL_FALSE:
    case BOOL_TRUE:
    case Pr:
    case Ss:
    case INF:
    case ID:
    case INTEGER:
    case REAL:
    case MINUS:
    case OPAR:
    case NOT:
      Probproperty();
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Probproperty() throws ParseException {
    if (jj_2_1(3)) {
      jj_consume_token(Pr);
      Relop();
      jj_consume_token(REAL);
      jj_consume_token(OBRACE);
      Probprop();
      jj_consume_token(CBRACE);
    } else if (jj_2_2(2)) {
      jj_consume_token(Pr);
      jj_consume_token(OBRACE);
      Probprop();
      jj_consume_token(CBRACE);
    } else if (jj_2_3(2)) {
      jj_consume_token(Pr);
      jj_consume_token(EQUAL);
      jj_consume_token(QMARK);
      jj_consume_token(OBRACE);
      Probprop();
      jj_consume_token(CBRACE);
    } else if (jj_2_4(3)) {
      jj_consume_token(Ss);
      Relop();
      jj_consume_token(REAL);
      jj_consume_token(OBRACE);
      Probproperty();
      jj_consume_token(CBRACE);
    } else if (jj_2_5(2)) {
      jj_consume_token(Ss);
      jj_consume_token(OBRACE);
      Probproperty();
      jj_consume_token(CBRACE);
    } else if (jj_2_6(2)) {
      jj_consume_token(Ss);
      jj_consume_token(EQUAL);
      jj_consume_token(QMARK);
      jj_consume_token(OBRACE);
      Probproperty();
      jj_consume_token(CBRACE);
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case MINF:
      case MAXF:
      case IDIV:
      case FLOOR:
      case CEIL:
      case UNIFORM:
      case NORMAL:
      case EXPONENTIAL:
      case GAMMA:
      case LOGNORMAL:
      case CHISQ:
      case LAPLACE:
      case CAUCHY:
      case RAYLEIGH:
      case POISSON:
      case BINOMIAL:
      case BERNOULLI:
      case BITNOT:
      case BITOR:
      case BITAND:
      case BITXOR:
      case INT:
      case BOOL:
      case BIT:
      case RATE:
      case BOOL_FALSE:
      case BOOL_TRUE:
      case INF:
      case ID:
      case INTEGER:
      case REAL:
      case MINUS:
      case OPAR:
      case NOT:
        Hsf();
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void Probprop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PG:
      jj_consume_token(PG);
      Bound();
      jj_consume_token(OPAR);
      Probproperty();
      jj_consume_token(CPAR);
      break;
    case PF:
      jj_consume_token(PF);
      Bound();
      jj_consume_token(OPAR);
      Probproperty();
      jj_consume_token(CPAR);
      break;
    case MINF:
    case MAXF:
    case IDIV:
    case FLOOR:
    case CEIL:
    case UNIFORM:
    case NORMAL:
    case EXPONENTIAL:
    case GAMMA:
    case LOGNORMAL:
    case CHISQ:
    case LAPLACE:
    case CAUCHY:
    case RAYLEIGH:
    case POISSON:
    case BINOMIAL:
    case BERNOULLI:
    case BITNOT:
    case BITOR:
    case BITAND:
    case BITXOR:
    case INT:
    case BOOL:
    case BIT:
    case RATE:
    case BOOL_FALSE:
    case BOOL_TRUE:
    case Pr:
    case Ss:
    case INF:
    case ID:
    case INTEGER:
    case REAL:
    case MINUS:
    case OPAR:
    case NOT:
      Probproperty();
      jj_consume_token(PU);
      Bound();
      Probproperty();
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

//void Props():
//{}
//{
// LOOKAHEAD(3) Prop() Andprop()
// | LOOKAHEAD(3) Prop() Orprop()
// | LOOKAHEAD(3) Prop() Impliesprop()
// | Prop()
//}
  final public void Props() throws ParseException {
    Prop();
    Props_prime();
  }

  final public void Props_prime() throws ParseException {
    Andprop();
  }

  final public void Andprop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
      jj_consume_token(AND);
      Prop();
      Andprop();
      break;
    default:
      jj_la1[4] = jj_gen;

    }
  }

  final public void Orprop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OR:
      jj_consume_token(OR);
      Prop();
      Orprop();
      break;
    default:
      jj_la1[5] = jj_gen;

    }
  }

  final public void Impliesprop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MINUS:
      jj_consume_token(MINUS);
      jj_consume_token(GREATERTHAN);
      Prop();
      Impliesprop();
      break;
    default:
      jj_la1[6] = jj_gen;

    }
  }

//void Prop():
//{}
//{
// LOOKAHEAD(3) Fronttype() <OPAR> Bound() Hsf() <CPAR>
// | Fronttype() <OPAR> Bound() Prop()<CPAR>
// | Midprop()
//}
  final public void Prop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EG:
    case EF:
    case AG:
    case AF:
      Fronttype();
      Bound();
      jj_consume_token(OPAR);
      Prop_prime();
      jj_consume_token(CPAR);
      break;
    case OBRACE:
      Midprop();
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Prop_prime() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MINF:
    case MAXF:
    case IDIV:
    case FLOOR:
    case CEIL:
    case UNIFORM:
    case NORMAL:
    case EXPONENTIAL:
    case GAMMA:
    case LOGNORMAL:
    case CHISQ:
    case LAPLACE:
    case CAUCHY:
    case RAYLEIGH:
    case POISSON:
    case BINOMIAL:
    case BERNOULLI:
    case BITNOT:
    case BITOR:
    case BITAND:
    case BITXOR:
    case INT:
    case BOOL:
    case BIT:
    case RATE:
    case BOOL_FALSE:
    case BOOL_TRUE:
    case INF:
    case ID:
    case INTEGER:
    case REAL:
    case MINUS:
    case OPAR:
    case NOT:
      Hsf();
      break;
    case EG:
    case EF:
    case AG:
    case AF:
      Fronttype();
      Bound();
      jj_consume_token(OPAR);
      Prop_prime();
      jj_consume_token(CPAR);
      break;
    case OBRACE:
      Midprop();
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

// void Midprop():
//{}
//{
// <OPAR> Propinner() <CPAR> 
//}
  final public void Midprop() throws ParseException {
    jj_consume_token(OBRACE);
    Propinner();
    jj_consume_token(CBRACE);
  }

//void Propinner():
//{}
//{
// LOOKAHEAD(3) Prop() Midtype() Bound() Prop()
// | LOOKAHEAD(3) Prop() Midtype() Bound() Hsf()
// | LOOKAHEAD(3) Hsf() Midtype() Bound() Prop()
// | Hsf() Midtype() Bound() Hsf()
//}
  final public void Propinner() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MINF:
    case MAXF:
    case IDIV:
    case FLOOR:
    case CEIL:
    case UNIFORM:
    case NORMAL:
    case EXPONENTIAL:
    case GAMMA:
    case LOGNORMAL:
    case CHISQ:
    case LAPLACE:
    case CAUCHY:
    case RAYLEIGH:
    case POISSON:
    case BINOMIAL:
    case BERNOULLI:
    case BITNOT:
    case BITOR:
    case BITAND:
    case BITXOR:
    case INT:
    case BOOL:
    case BIT:
    case RATE:
    case BOOL_FALSE:
    case BOOL_TRUE:
    case INF:
    case ID:
    case INTEGER:
    case REAL:
    case MINUS:
    case OPAR:
    case NOT:
      Hsf();
      Midtype();
      Bound();
      Propinner_prime();
      break;
    case OBRACE:
    case EG:
    case EF:
    case AG:
    case AF:
      Prop();
      Midtype();
      Bound();
      Propinner_prime();
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Propinner_prime() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OBRACE:
    case EG:
    case EF:
    case AG:
    case AF:
      Prop();
      break;
    case MINF:
    case MAXF:
    case IDIV:
    case FLOOR:
    case CEIL:
    case UNIFORM:
    case NORMAL:
    case EXPONENTIAL:
    case GAMMA:
    case LOGNORMAL:
    case CHISQ:
    case LAPLACE:
    case CAUCHY:
    case RAYLEIGH:
    case POISSON:
    case BINOMIAL:
    case BERNOULLI:
    case BITNOT:
    case BITOR:
    case BITAND:
    case BITXOR:
    case INT:
    case BOOL:
    case BIT:
    case RATE:
    case BOOL_FALSE:
    case BOOL_TRUE:
    case INF:
    case ID:
    case INTEGER:
    case REAL:
    case MINUS:
    case OPAR:
    case NOT:
      Hsf();
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Fronttype() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AG:
      jj_consume_token(AG);
      break;
    case AF:
      jj_consume_token(AF);
      break;
    case EG:
      jj_consume_token(EG);
      break;
    case EF:
      jj_consume_token(EF);
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Midtype() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AU:
      jj_consume_token(AU);
      break;
    case EU:
      jj_consume_token(EU);
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Bound() throws ParseException {
    if (jj_2_7(2)) {
      jj_consume_token(OSQUARE);
      Relop();
      Hsf();
      jj_consume_token(CSQUARE);
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OSQUARE:
        jj_consume_token(OSQUARE);
        Hsf();
        jj_consume_token(COMMA);
        Hsf();
        jj_consume_token(CSQUARE);
        break;
      default:
        jj_la1[13] = jj_gen;

      }
    }
  }

  final public void Hsf() throws ParseException {
    Andexpr();
    Hsf_prime();
  }

  final public void Hsf_prime() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OR:
      jj_consume_token(OR);
      Andexpr();
      Hsf_prime();
      break;
    case IMPLIC:
      jj_consume_token(IMPLIC);
      Andexpr();
      Hsf_prime();
      break;
    default:
      jj_la1[14] = jj_gen;

    }
  }

  final public void Andexpr() throws ParseException {
    Relation();
    Andexpr_prime();
  }

  final public void Andexpr_prime() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AND:
      jj_consume_token(AND);
      Relation();
      Andexpr_prime();
      break;
    default:
      jj_la1[15] = jj_gen;

    }
  }

  final public void Relation() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MINF:
    case MAXF:
    case IDIV:
    case FLOOR:
    case CEIL:
    case UNIFORM:
    case NORMAL:
    case EXPONENTIAL:
    case GAMMA:
    case LOGNORMAL:
    case CHISQ:
    case LAPLACE:
    case CAUCHY:
    case RAYLEIGH:
    case POISSON:
    case BINOMIAL:
    case BERNOULLI:
    case BITNOT:
    case BITOR:
    case BITAND:
    case BITXOR:
    case INT:
    case BOOL:
    case RATE:
    case BOOL_FALSE:
    case BOOL_TRUE:
    case INF:
    case ID:
    case INTEGER:
    case REAL:
    case MINUS:
    case OPAR:
      Arithexpr();
      Relation_prime();
      break;
    case NOT:
      jj_consume_token(NOT);
      Relation();
      break;
    case BIT:
      jj_consume_token(BIT);
      jj_consume_token(OPAR);
      Arithexpr();
      jj_consume_token(COMMA);
      Arithexpr();
      jj_consume_token(CPAR);
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Relation_prime() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQUAL:
    case LESSTHAN:
    case GREATERTHAN:
    case GEQ:
    case LEQ:
      Relop();
      Arithexpr();
      break;
    default:
      jj_la1[17] = jj_gen;

    }
  }

  final public void Arithexpr() throws ParseException {
    Multexpr();
    Arithexpr_prime();
  }

  final public void Arithexpr_prime() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PLUS:
      jj_consume_token(PLUS);
      Multexpr();
      Arithexpr_prime();
      break;
    case MINUS:
      jj_consume_token(MINUS);
      Multexpr();
      Arithexpr_prime();
      break;
    default:
      jj_la1[18] = jj_gen;

    }
  }

  final public void Multexpr() throws ParseException {
    Term();
    Multexpr_prime();
  }

  final public void Multexpr_prime() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MULT:
      jj_consume_token(MULT);
      Term();
      Multexpr_prime();
      break;
    case DIV:
      jj_consume_token(DIV);
      Term();
      Multexpr_prime();
      break;
    case MOD:
      jj_consume_token(MOD);
      Term();
      Multexpr_prime();
      break;
    case POWER:
      jj_consume_token(POWER);
      Term();
      Multexpr_prime();
      break;
    case ID:
      Id();
      break;
    default:
      jj_la1[19] = jj_gen;

    }
  }

  final public void Term() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPAR:
      jj_consume_token(OPAR);
      Hsf();
      jj_consume_token(CPAR);
      break;
    case ID:
      Id();
      break;
    case INTEGER:
      jj_consume_token(INTEGER);
      break;
    case REAL:
      jj_consume_token(REAL);
      break;
    case INF:
      jj_consume_token(INF);
      break;
    case RATE:
      jj_consume_token(RATE);
      jj_consume_token(OPAR);
      Id();
      jj_consume_token(CPAR);
      break;
    case INT:
      jj_consume_token(INT);
      jj_consume_token(OPAR);
      Hsf();
      jj_consume_token(CPAR);
      break;
    case MINUS:
      jj_consume_token(MINUS);
      Term();
      break;
    case BOOL_FALSE:
      jj_consume_token(BOOL_FALSE);
      break;
    case BOOL_TRUE:
      jj_consume_token(BOOL_TRUE);
      break;
    case FLOOR:
    case CEIL:
    case EXPONENTIAL:
    case CHISQ:
    case LAPLACE:
    case CAUCHY:
    case RAYLEIGH:
    case POISSON:
    case BERNOULLI:
    case BITNOT:
    case BOOL:
      Unop();
      jj_consume_token(OPAR);
      Arithexpr();
      jj_consume_token(CPAR);
      break;
    case MINF:
    case MAXF:
    case IDIV:
    case UNIFORM:
    case NORMAL:
    case GAMMA:
    case LOGNORMAL:
    case BINOMIAL:
    case BITOR:
    case BITAND:
    case BITXOR:
      Binop();
      jj_consume_token(OPAR);
      Arithexpr();
      jj_consume_token(COMMA);
      Arithexpr();
      jj_consume_token(CPAR);
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Relop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQUAL:
      jj_consume_token(EQUAL);
      break;
    default:
      jj_la1[21] = jj_gen;
      if (jj_2_8(2)) {
        jj_consume_token(LESSTHAN);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LEQ:
          jj_consume_token(LEQ);
          break;
        default:
          jj_la1[22] = jj_gen;
          if (jj_2_9(2)) {
            jj_consume_token(GREATERTHAN);
          } else {
            switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
            case GEQ:
              jj_consume_token(GEQ);
              break;
            default:
              jj_la1[23] = jj_gen;
              jj_consume_token(-1);
              throw new ParseException();
            }
          }
        }
      }
    }
  }

  final public void Unop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BITNOT:
      jj_consume_token(BITNOT);
      break;
    case EXPONENTIAL:
      jj_consume_token(EXPONENTIAL);
      break;
    case CHISQ:
      jj_consume_token(CHISQ);
      break;
    case LAPLACE:
      jj_consume_token(LAPLACE);
      break;
    case CAUCHY:
      jj_consume_token(CAUCHY);
      break;
    case RAYLEIGH:
      jj_consume_token(RAYLEIGH);
      break;
    case POISSON:
      jj_consume_token(POISSON);
      break;
    case BERNOULLI:
      jj_consume_token(BERNOULLI);
      break;
    case BOOL:
      jj_consume_token(BOOL);
      break;
    case FLOOR:
      jj_consume_token(FLOOR);
      break;
    case CEIL:
      jj_consume_token(CEIL);
      break;
    default:
      jj_la1[24] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Binop() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case BITOR:
      jj_consume_token(BITOR);
      break;
    case BITAND:
      jj_consume_token(BITAND);
      break;
    case BITXOR:
      jj_consume_token(BITXOR);
      break;
    case UNIFORM:
      jj_consume_token(UNIFORM);
      break;
    case NORMAL:
      jj_consume_token(NORMAL);
      break;
    case GAMMA:
      jj_consume_token(GAMMA);
      break;
    case LOGNORMAL:
      jj_consume_token(LOGNORMAL);
      break;
    case BINOMIAL:
      jj_consume_token(BINOMIAL);
      break;
    case MINF:
      jj_consume_token(MINF);
      break;
    case MAXF:
      jj_consume_token(MAXF);
      break;
    case IDIV:
      jj_consume_token(IDIV);
      break;
    default:
      jj_la1[25] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Id() throws ParseException {
    jj_consume_token(ID);
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_3_9() {
    if (jj_scan_token(GREATERTHAN)) return true;
    return false;
  }

  private boolean jj_3_6() {
    if (jj_scan_token(Ss)) return true;
    if (jj_scan_token(EQUAL)) return true;
    return false;
  }

  private boolean jj_3_8() {
    if (jj_scan_token(LESSTHAN)) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_scan_token(Ss)) return true;
    if (jj_scan_token(OBRACE)) return true;
    return false;
  }

  private boolean jj_3_4() {
    if (jj_scan_token(Ss)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(REAL)) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_scan_token(Pr)) return true;
    if (jj_scan_token(EQUAL)) return true;
    return false;
  }

  private boolean jj_3R_2() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(64)) {
    jj_scanpos = xsp;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_scan_token(106)) {
    jj_scanpos = xsp;
    if (jj_3_9()) {
    jj_scanpos = xsp;
    if (jj_scan_token(105)) return true;
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(Pr)) return true;
    if (jj_scan_token(OBRACE)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_scan_token(Pr)) return true;
    if (jj_3R_2()) return true;
    if (jj_scan_token(REAL)) return true;
    return false;
  }

  private boolean jj_3_7() {
    if (jj_scan_token(OSQUARE)) return true;
    if (jj_3R_2()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[26];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static private int[] jj_la1_2;
  static private int[] jj_la1_3;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
      jj_la1_init_2();
      jj_la1_init_3();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x3fffffe,0x3fffffe,0x3fffffe,0x3fffffe,0x0,0x0,0x0,0x0,0x3fffffe,0x3fffffe,0x3fffffe,0x0,0x0,0x0,0x20000000,0x0,0x3fffffe,0x0,0x0,0x0,0x2fffffe,0x0,0x0,0x0,0x86f930,0x3906ce,};
   }
   private static void jj_la1_init_2() {
      jj_la1_2 = new int[] {0x8d41879a,0x8d41879a,0x8d400018,0x8d419818,0x0,0x0,0x80000000,0x782,0x8d40079a,0x8d40079a,0x8d40079a,0x780,0x60,0x20000,0x0,0x0,0x8d400018,0x80005,0xc0000000,0x1000000,0x8d400018,0x1,0x0,0x0,0x0,0x0,};
   }
   private static void jj_la1_init_3() {
      jj_la1_3 = new int[] {0x21,0x21,0x21,0x21,0x80,0x100,0x0,0x0,0x21,0x21,0x21,0x0,0x0,0x0,0x100,0x80,0x21,0x600,0x0,0x5c,0x1,0x0,0x400,0x200,0x0,0x0,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[9];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 26; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[116];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 26; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
          if ((jj_la1_2[i] & (1<<j)) != 0) {
            la1tokens[64+j] = true;
          }
          if ((jj_la1_3[i] & (1<<j)) != 0) {
            la1tokens[96+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 116; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 9; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

  }
