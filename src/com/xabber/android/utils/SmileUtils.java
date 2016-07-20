/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xabber.android.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.xabber.androiddevs.R;

public class SmileUtils {
	public static final String ee_1 = "[):]";
	public static final String ee_2 = "[:D]";
	public static final String ee_3 = "[;)]";
	public static final String ee_4 = "[:-o]";
	public static final String ee_5 = "[:p]";
	public static final String ee_6 = "[(H)]";
	public static final String ee_7 = "[:@]";
	public static final String ee_8 = "[:s]";
	public static final String ee_9 = "[:$]";
	public static final String ee_10 = "[:(]";
	public static final String ee_11 = "[:'(]";
	public static final String ee_12 = "[:|]";
	public static final String ee_13 = "[(a)]";
	public static final String ee_14 = "[8o|]";
	public static final String ee_15 = "[8-|]";
	public static final String ee_16 = "[+o(]";
	public static final String ee_17 = "[<o)]";
	public static final String ee_18 = "[|-)]";
	public static final String ee_19 = "[*-)]";
	public static final String ee_20 = "[:-#]";
	public static final String ee_21 = "[:-*]";
	public static final String ee_22 = "[^o)]";
	public static final String ee_23 = "[8-)]";
	public static final String ee_24 = "[(|)]";
	public static final String ee_25 = "[(u)]";
	public static final String ee_26 = "[(S)]";
	public static final String ee_27 = "[(*)]";
	public static final String ee_28 = "[(#)]";
	public static final String ee_29 = "[(R)]";
	public static final String ee_30 = "[({)]";
	public static final String ee_31 = "[(})]";
	public static final String ee_32 = "[(k)]";
	public static final String ee_33 = "[(F)]";
	public static final String ee_34 = "[(W)]";
	public static final String ee_35 = "[(D)]";
	public static final String ee_36 = "[(A)]";
	public static final String ee_37 = "[(B)]";
	public static final String ee_38 = "[(R)]";
	public static final String ee_39 = "[(H)]";
	public static final String ee_40 = "[(U)]";
	public static final String ee_41 = "[(T)]";
	public static final String ee_42 = "[(K)]";
	public static final String ee_43 = "[(U%)]";
	public static final String ee_44 = "[(I)]";
	public static final String ee_45 = "[(P)]";
	public static final String ee_46 = "[(;)]";
	public static final String ee_47 = "[(')]";
	public static final String ee_48 = "[(ZX)]";
	public static final String ee_49 = "[(FS)]";
	public static final String ee_50 = "[(FH)]";
	public static final String ee_51 = "[(BF)]";
	public static final String ee_52 = "[(YJ)]";
	public static final String ee_53 = "[(KH)]";
	public static final String ee_54 = "[(RE)]";
	public static final String ee_55 = "[(BNB)]";
	public static final String ee_56 = "[(KK)]";
	public static final String ee_57 = "[(TYR)]";
	public static final String ee_58 = "[(BHG)]";
	public static final String ee_59 = "[(JKJ)]";
	public static final String ee_60 = "[(HGJ)]";
	public static final String ee_61 = "[(IY)]";
	public static final String ee_62 = "[(ERE)]";
	public static final String ee_63 = "[(GGJ)]";
	public static final String ee_64 = "[(UUY)]";
	public static final String ee_65 = "[(I,G)]";
	public static final String ee_66 = "[(BNH)]";
	public static final String ee_67 = "[(FH)]";
	public static final String ee_68 = "[(ES)]";
	public static final String ee_69 = "[(AC)]";
	public static final String ee_70 = "[(XV)]";
	public static final String ee_71 = "[(VC)]";
	public static final String ee_72 = "[(FGD)]";
	public static final String ee_73 = "[(ACCS)]";
	public static final String ee_74 = "[(XC)]";
	public static final String ee_75 = "[(VC)]";
	public static final String ee_76 = "[(FGF)]";
	public static final String ee_77 = "[(TH)]";
	public static final String ee_78 = "[(O;)]";
	public static final String ee_79 = "[(,M)]";
	public static final String ee_80 = "[(KK.)]";
	public static final String ee_81 = "[(;L)]";
	public static final String ee_82 = "[(LKL)]";
	public static final String ee_83 = "[(KLK)]";
	public static final String ee_84 = "[(JK)]";
	public static final String ee_85 = "[(L;L)]";
	public static final String ee_86 = "[(,.)]";
	public static final String ee_87 = "[(/M)]";
	public static final String ee_88 = "[(JKJ)]";
	public static final String ee_89 = "[(;KK)]";
	public static final String ee_90 = "[(JKJ,)]";
	public static final String ee_91 = "[(K,,)]";
	public static final String ee_92 = "[(MN,)]";
	public static final String ee_93 = "[(,N,)]";
	public static final String ee_94 = "[(M,M)]";
	public static final String ee_95 = "[(;..)]";
	public static final String ee_96 = "[(.M.)]";
	public static final String ee_97 = "[(,.,)]";
	public static final String ee_98 = "[(,.,)]";
	public static final String ee_99 = "[(,.M)]";
	public static final String ee_100 = "[(M.M)]";
	public static final String ee_101 = "[(.MM)]";
	public static final String ee_102 = "[(M.ML)]";
	public static final String ee_103 = "[(ZX)]";
	public static final String ee_104 = "[(XCZ)]";
	public static final String ee_105 = "[(ERFS)]";

	private static final Factory spannableFactory = Spannable.Factory
			.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {

		addPattern(emoticons, ee_1, R.drawable.ee_1);
		addPattern(emoticons, ee_2, R.drawable.ee_2);
		addPattern(emoticons, ee_3, R.drawable.ee_3);
		addPattern(emoticons, ee_4, R.drawable.ee_4);
		addPattern(emoticons, ee_5, R.drawable.ee_5);
		addPattern(emoticons, ee_6, R.drawable.ee_6);
		addPattern(emoticons, ee_7, R.drawable.ee_7);
		addPattern(emoticons, ee_8, R.drawable.ee_8);
		addPattern(emoticons, ee_9, R.drawable.ee_9);
		addPattern(emoticons, ee_10, R.drawable.ee_10);
		addPattern(emoticons, ee_11, R.drawable.ee_11);
		addPattern(emoticons, ee_12, R.drawable.ee_12);
		addPattern(emoticons, ee_13, R.drawable.ee_13);
		addPattern(emoticons, ee_14, R.drawable.ee_14);
		addPattern(emoticons, ee_15, R.drawable.ee_15);
		addPattern(emoticons, ee_16, R.drawable.ee_16);
		addPattern(emoticons, ee_17, R.drawable.ee_17);
		addPattern(emoticons, ee_18, R.drawable.ee_18);
		addPattern(emoticons, ee_19, R.drawable.ee_19);
		addPattern(emoticons, ee_20, R.drawable.ee_20);
		addPattern(emoticons, ee_21, R.drawable.ee_21);
		addPattern(emoticons, ee_22, R.drawable.ee_22);
		addPattern(emoticons, ee_23, R.drawable.ee_23);
		addPattern(emoticons, ee_24, R.drawable.ee_24);
		addPattern(emoticons, ee_25, R.drawable.ee_25);
		addPattern(emoticons, ee_26, R.drawable.ee_26);
		addPattern(emoticons, ee_27, R.drawable.ee_27);
		addPattern(emoticons, ee_28, R.drawable.ee_28);
		addPattern(emoticons, ee_29, R.drawable.ee_29);
		addPattern(emoticons, ee_30, R.drawable.ee_30);
		addPattern(emoticons, ee_31, R.drawable.ee_31);
		addPattern(emoticons, ee_32, R.drawable.ee_32);
		addPattern(emoticons, ee_33, R.drawable.ee_33);
		addPattern(emoticons, ee_34, R.drawable.ee_34);
		addPattern(emoticons, ee_35, R.drawable.ee_35);
		addPattern(emoticons, ee_36, R.drawable.ee_36);
		addPattern(emoticons, ee_37, R.drawable.ee_37);
		addPattern(emoticons, ee_38, R.drawable.ee_38);
		addPattern(emoticons, ee_39, R.drawable.ee_39);
		addPattern(emoticons, ee_40, R.drawable.ee_40);
		addPattern(emoticons, ee_41, R.drawable.ee_41);
		addPattern(emoticons, ee_42, R.drawable.ee_42);
		addPattern(emoticons, ee_43, R.drawable.ee_43);
		addPattern(emoticons, ee_44, R.drawable.ee_44);
		addPattern(emoticons, ee_45, R.drawable.ee_45);
		addPattern(emoticons, ee_46, R.drawable.ee_46);
		addPattern(emoticons, ee_47, R.drawable.ee_47);
		addPattern(emoticons, ee_48, R.drawable.ee_48);
		addPattern(emoticons, ee_49, R.drawable.ee_49);
		addPattern(emoticons, ee_50, R.drawable.ee_50);
		addPattern(emoticons, ee_51, R.drawable.ee_51);
		addPattern(emoticons, ee_52, R.drawable.ee_52);
		addPattern(emoticons, ee_53, R.drawable.ee_53);
		addPattern(emoticons, ee_54, R.drawable.ee_54);
		addPattern(emoticons, ee_55, R.drawable.ee_55);
		addPattern(emoticons, ee_56, R.drawable.ee_56);
		addPattern(emoticons, ee_57, R.drawable.ee_57);
		addPattern(emoticons, ee_58, R.drawable.ee_58);
		addPattern(emoticons, ee_59, R.drawable.ee_59);
		addPattern(emoticons, ee_60, R.drawable.ee_60);
		addPattern(emoticons, ee_61, R.drawable.ee_61);
		addPattern(emoticons, ee_62, R.drawable.ee_62);
		addPattern(emoticons, ee_63, R.drawable.ee_63);
		addPattern(emoticons, ee_64, R.drawable.ee_64);
		addPattern(emoticons, ee_65, R.drawable.ee_65);
		addPattern(emoticons, ee_66, R.drawable.ee_66);
		addPattern(emoticons, ee_67, R.drawable.ee_67);
		addPattern(emoticons, ee_68, R.drawable.ee_68);
		addPattern(emoticons, ee_69, R.drawable.ee_69);
		addPattern(emoticons, ee_70, R.drawable.ee_70);
		addPattern(emoticons, ee_71, R.drawable.ee_71);
		addPattern(emoticons, ee_72, R.drawable.ee_72);
		addPattern(emoticons, ee_73, R.drawable.ee_73);
		addPattern(emoticons, ee_74, R.drawable.ee_74);
		addPattern(emoticons, ee_75, R.drawable.ee_75);
		addPattern(emoticons, ee_76, R.drawable.ee_76);
		addPattern(emoticons, ee_77, R.drawable.ee_77);
		addPattern(emoticons, ee_78, R.drawable.ee_78);
		addPattern(emoticons, ee_79, R.drawable.ee_79);
		addPattern(emoticons, ee_80, R.drawable.ee_80);
		addPattern(emoticons, ee_81, R.drawable.ee_81);
		addPattern(emoticons, ee_82, R.drawable.ee_82);
		addPattern(emoticons, ee_83, R.drawable.ee_83);
		addPattern(emoticons, ee_84, R.drawable.ee_84);
		addPattern(emoticons, ee_85, R.drawable.ee_85);
		addPattern(emoticons, ee_86, R.drawable.ee_86);
		addPattern(emoticons, ee_87, R.drawable.ee_87);
		addPattern(emoticons, ee_88, R.drawable.ee_88);
		addPattern(emoticons, ee_89, R.drawable.ee_89);
		addPattern(emoticons, ee_90, R.drawable.ee_90);
		addPattern(emoticons, ee_91, R.drawable.ee_91);
		addPattern(emoticons, ee_92, R.drawable.ee_92);
		addPattern(emoticons, ee_93, R.drawable.ee_93);
		addPattern(emoticons, ee_94, R.drawable.ee_94);
		addPattern(emoticons, ee_95, R.drawable.ee_95);
		addPattern(emoticons, ee_96, R.drawable.ee_96);
		addPattern(emoticons, ee_97, R.drawable.ee_97);
		addPattern(emoticons, ee_98, R.drawable.ee_98);
		addPattern(emoticons, ee_99, R.drawable.ee_99);
		addPattern(emoticons, ee_100, R.drawable.ee_100);
		addPattern(emoticons, ee_101, R.drawable.ee_101);
		addPattern(emoticons, ee_102, R.drawable.ee_102);
		addPattern(emoticons, ee_103, R.drawable.ee_103);
		addPattern(emoticons, ee_104, R.drawable.ee_104);
		addPattern(emoticons, ee_105, R.drawable.ee_105);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
			int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * 
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
		boolean hasChanges = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(),
						matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start()
							&& spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					hasChanges = true;
					spannable.setSpan(new ImageSpan(context, entry.getValue()),
							matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
		Spannable spannable = spannableFactory.newSpannable(text);
		addSmiles(context, spannable);
		return spannable;
	}

	public static boolean containsKey(String key) {
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(key);
			if (matcher.find()) {
				b = true;
				break;
			}
		}

		return b;
	}
}
