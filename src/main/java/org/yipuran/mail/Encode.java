package org.yipuran.mail;

/**
 * 文字エンコード処理.
 */
public final class Encode{
	/** private constructor. */
   private Encode(){}
   /**
    * Unicode(cp932) から JIS へ文字コード変換をします。.
    * <PRE>
    *   ￥：0xff3c[FULLWIDTH REVERSE SOLIDUS] -> 0x005c[REVERSE SOLIDUS]
    *   ～：0xff5e[FULLWIDTH TILDE]           -> 0x301c[WAVE DASH]
    *   ∥：0x2225[PARALLEL TO]               -> 0x2016[DOUBLE VERTICAL LINE]
    *   －：0xff0d[FULLWIDTH HYPHEN-MINUS]    -> 0x2212[MINUS SIGN]
    *   ￠：0xffe0[FULLWIDTH CENT SIGN]       -> 0x00a2[CENT SIGN]
    *   ￡：0xffe1[FULLWIDTH POUND SIGN]      -> 0x00a3[POUND SIGN]
    *   ￢：0xffe2[FULLWIDTH NOT SIGN]        -> 0x00ac[NOT SIGN]
    * </PRE>
    * @param s 変換前文字列<code>String</code>
    * @return 変換後文字列<code>String</code>
    */
   public static String convCp932toJIS(String s) {
   	if (s == null) return s;
   	StringBuffer buffer = new StringBuffer();
   	for (int i = 0; i < s.length(); i++) {
   		char c  = s.charAt(i);
   		switch (c) {
   			case 0xff3c:    // FULLWIDTH REVERSE SOLIDUS ->
   				c = 0x005c; // REVERSE SOLIDUS
               break;
   			case 0xff5e:    // FULLWIDTH TILDE ->
               c = 0x301c; // WAVE DASH
               break;
   			case 0x2225:    // PARALLEL TO ->
               c = 0x2016; // DOUBLE VERTICAL LINE
               break;
   			case 0xff0d:    // FULLWIDTH HYPHEN-MINUS ->
               c = 0x2212; // MINUS SIGN
               break;
   			case 0xffe0:    // FULLWIDTH CENT SIGN ->
               c = 0x00a2; // CENT SIGN
               break;
   			case 0xffe1:    // FULLWIDTH POUND SIGN ->
               c = 0x00a3; // POUND SIGN
               break;
   			case 0xffe2:    // FULLWIDTH NOT SIGN ->
               c = 0x00ac; // NOT SIGN
               break;
   		}
   		buffer.append(c);
   	}
   	return buffer.toString();
   }

   /**
    * ProphibitCode　の変換.
    * ローマ数字、丸数字等の文字
    * @param s 変換前文字列<code>String</code>
    * @return 変換後文字列<code>String</code>
    */
   public static String convProphibitCode(String s){
      if (s == null) return s;
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < s.length(); i++) {
      	char c  = s.charAt(i);
      	switch (c) {
      		case 0x2160:    // Ⅰ->I
      			c = 0x0049;
      			break;
      		case 0x2161:    // Ⅱ->II
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0049;
      			break;
      		case 0x2162:    // Ⅲ->III
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0049;
      			break;
      		case 0x2163:    // Ⅳ->IV
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0056;
      			break;
      		case 0x2164:    // Ⅴ->V
      			c = 0x0056;
      			break;
      		case 0x2165:    // Ⅵ->VI
      			c = 0x0056;
      			sb.append(c);
      			c = 0x0049;
      			break;
      		case 0x2166:    // Ⅶ->VII
      			c = 0x0056;
      			sb.append(c);
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0049;
      			break;
      		case 0x2167:    // Ⅷ->VIII
      			c = 0x0056;
      			sb.append(c);
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0049;
      			break;
      		case 0x2168:    // Ⅸ->IX
      			c = 0x0049;
      			sb.append(c);
      			c = 0x0058;
      			break;
      		case 0x2169:    // Ⅹ->X
      			c = 0x0058;
      			break;
      		case 0x2170:    // ⅰ->i
      			c = 0x0069;
      			break;
      		case 0x2171:    // ⅱ->ii
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0069;
      			break;
      		case 0x2172:    // ⅲ->iii
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0069;
      			break;
      		case 0x2173:    // ⅳ->iv
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0076;
      			break;
      		case 0x2174:    // ⅴ->v
      			c = 0x0076;
      			break;
      		case 0x2175:    // ⅵ->vi
      			c = 0x0076;
      			sb.append(c);
      			c = 0x0069;
      			break;
      		case 0x2176:    // ⅶ->vii
      			c = 0x0076;
      			sb.append(c);
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0069;
      			break;
      		case 0x2177:    // ⅷ->viii
      			c = 0x0076;
      			sb.append(c);
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0069;
      			break;
      		case 0x2178:    // ⅸ->ix
      			c = 0x0069;
      			sb.append(c);
      			c = 0x0078;
      			break;
      		case 0x2179:    // ⅹ->x
      			c = 0x0078;
      			break;
      		case 0x2460:    // ①->(1)
      			sb.append("(1");
      			c = ')';
      			break;
      		case 0x2461:    // ②->(2)
      			sb.append("(2");
      			c = ')';
      			break;
      		case 0x2462:    // ③->(3)
      			sb.append("(3");
      			c = ')';
      			break;
      		case 0x2463:    // ④->(4)
      			sb.append("(4");
      			c = ')';
      			break;
      		case 0x2464:    // ⑤->(5)
      			sb.append("(5");
      			c = ')';
      			break;
      		case 0x2465:    // ⑥->(6)
      			sb.append("(6");
      			c = ')';
      			break;
      		case 0x2466:    // ⑦->(7)
      			sb.append("(7");
      			c = ')';
      			break;
      		case 0x2467:    // ⑧->(8)
      			sb.append("(8");
      			c = ')';
      			break;
      		case 0x2468:    // ⑨->(9)
      			sb.append("(9");
      			c = ')';
      			break;
      		case 0x2469:    // ⑩->(10)
      			sb.append("(10");
      			c = ')';
      			break;
      		case 0x246a:    // ⑪>(11)
      			sb.append("(11");
      			c = ')';
      			break;
      		case 0x246b:    // ⑫->(1)
      			sb.append("(12");
      			c = ')';
      			break;
      		case 0x246c:    // ⑬->(1)
      			sb.append("(13");
      			c = ')';
      			break;
      		case 0x246d:    // ⑭->(1)
      			sb.append("(14");
      			c = ')';
      			break;
      		case 0x246e:    // ⑮->(1)
      			sb.append("(15");
      			c = ')';
      			break;
      		case 0x246f:    // ⑯->(1)
      			sb.append("(16");
      			c = ')';
      			break;
      		case 0x2470:    // ⑰->(1)
      			sb.append("(17");
      			c = ')';
      			break;
      		case 0x2471:    // ⑱->(1)
      			sb.append("(18");
      			c = ')';
      			break;
      		case 0x2472:    // ⑲->(1)
      			sb.append("(19");
      			c = ')';
      			break;
      		case 0x2473:    // ⑳->(1)
      			sb.append("(20");
      			c = ')';
      			break;
      	}
      	sb.append(c);
      }
      return sb.toString();
   }

}
