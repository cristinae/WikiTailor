package cat.lump.ie.textprocessing.sentence;


import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import cat.lump.ie.textprocessing.Decomposition;

public class SentenceTest {

	Decomposition sentences;
	private String text = 
		"The 1952 Winter Olympics took place in Oslo, Norway, from 14 to 25 "
		+ "February. All of the venues for the games were located in Oslo's "
		+ "metropolitan area with the exception of the alpine skiing events, "
		+ "which were held at Norefjell, 113 km (70 mi) away. A new hotel was "
		+ "built for the press and dignitaries, along with three dormitories "
		+ "to house athletes and coaches, creating the first modern Olympic "
		+ "Village. The games attracted 694 athletes representing 30 "
		+ "countries, who participated in four sports and 22 events. There "
		+ "was one demonstration sport, bandy, in which three Scandinavian "
		+ "countries competed.";
			
	@Before
	public void setUp() throws Exception {
		sentences = new SentencesOpennlp(Locale.ENGLISH);
	}

	@Test
	public void testGetStrings() {
		List<String> expected = Arrays.asList(new String[]{
			"The 1952 Winter Olympics took place in Oslo, Norway, from 14 to "
			+ "25 February.",
			"All of the venues for the games were located in Oslo's "
			+ "metropolitan area with the exception of the alpine skiing "
			+ "events, which were held at Norefjell, 113 km (70 mi) away.",
			"A new hotel was built for the press and dignitaries, along with "
			+ "three dormitories to house athletes and coaches, creating the "
			+ "first modern Olympic Village.",
			"The games attracted 694 athletes representing 30 countries, who "
			+ "participated in four sports and 22 events.",
			"There was one demonstration sport, bandy, in which three "
			+ "Scandinavian countries competed."});
		
		assertEquals(expected,  
				sentences.getStrings(text));
	}

	@Test
	public void testGetStringsGerman(){
		sentences = new SentencesOpennlp(Locale.GERMAN);
		text = "Koblenz (mundartlich: Kowelenz) ist eine kreisfreie Stadt im "
			+ "nördlichen Rheinland-Pfalz. Sie ist mit rund 109.000 "
			+ "Einwohnern nach Mainz und Ludwigshafen am Rhein die "
			+ "drittgrößte Stadt dieses Landes und bildet eines seiner "
			+ "fünf Oberzentren.";
		List<String> expected = Arrays.asList(new String[]{
			"Koblenz (mundartlich: Kowelenz) ist eine kreisfreie Stadt im "
			+ "nördlichen Rheinland-Pfalz.",
			"Sie ist mit rund 109.000 Einwohnern nach Mainz und "
			+ "Ludwigshafen am Rhein die drittgrößte Stadt dieses Landes "
			+ "und bildet eines seiner fünf Oberzentren." 	
		});
		assertEquals(expected,  
				sentences.getStrings(text));
	}
	
	@Test
	public void testGetStringsSpanish(){
		sentences = new SentencesOpennlp(new Locale("es"));
		text = "La Asociación Deportiva San Antonio pertenece a las comunas de "
			+ "San Antonio y de Santo Domingo. Es un torneo de fútbol "
			+ "organizado por la Asociación Nacional de Fútbol Amateur de "
			+ "Chile (ANFA), perteneciente a la Federación de Fútbol de "
			+ "Chile.";
		List<String> expected = Arrays.asList(new String[]{
			"La Asociación Deportiva San Antonio pertenece a las comunas "
			+ "de San Antonio y de Santo Domingo.",
			"Es un torneo de fútbol organizado por la Asociación Nacional "
			+ "de Fútbol Amateur de Chile (ANFA), perteneciente a la "
			+ "Federación de Fútbol de Chile."});
		assertEquals(expected,  
				sentences.getStrings(text));

	}
	
	@Test
	public void testGetStringsGreek(){
		sentences = new SentencesOpennlp(new Locale("el"));
		
		text = "Η Μάχη της Μόσχας (ρώσικα: Битва под Москвой, γερμανικά: "
			+ "Schlacht um Moskau) είναι το όνομα που έχει δοθεί από "
			+ "Σοβιετικούς ιστορικούς σε δύο περιόδους σημαντικών μαχών "
			+ "κατά μήκος ενός τομέα 600 χιλιομέτρων στο Ανατολικό "
			+ "Μέτωπο κατά την διάρκεια του Β' Παγκοσμίου Πολέμου. "
			+ "Διαδραματίστηκε μεταξύ Οκτωβρίου 1941 και Ιανουαρίου 1942. "
			+ "Ο Χίτλερ θεωρούσε την Μόσχα -πρωτεύουσα της Σοβιετικής "
			+ "Ένωσης και μεγαλύτερη πόλη της- κύριο στρατηγικό στόχο των "
			+ "δυνάμεων του Άξονα στην Επιχείρηση Μπαρμπαρόσα.";
		List<String> expected = Arrays.asList(new String[]{
			"Η Μάχη της Μόσχας (ρώσικα: Битва под Москвой, γερμανικά: " 
			+ "Schlacht um Moskau) είναι το όνομα που έχει δοθεί από " 
			+ "Σοβιετικούς ιστορικούς σε δύο περιόδους σημαντικών μαχών " 
			+ "κατά μήκος ενός τομέα 600 χιλιομέτρων στο Ανατολικό " 
			+ "Μέτωπο κατά την διάρκεια του Β' Παγκοσμίου Πολέμου.",
			 "Διαδραματίστηκε μεταξύ Οκτωβρίου 1941 και Ιανουαρίου 1942.",
			 "Ο Χίτλερ θεωρούσε την Μόσχα -πρωτεύουσα της Σοβιετικής " 
			+ "Ένωσης και μεγαλύτερη πόλη της- κύριο στρατηγικό στόχο των " 
			+ "δυνάμεων του Άξονα στην Επιχείρηση Μπαρμπαρόσα."});
		assertEquals(expected,  
				sentences.getStrings(text));
	}

	//TODO we need somebody else to properly define this test
//	@Test
//	public void testGetStringsArabic){
//		sentences = new SentencesOpennlp(new Locale("ar"));
//		
//		text = "حزام كايبر (بالإنكليزية: Kuiper belt؛ نقحرة: كايْبِر؛ بالرغم من أن الاسم يكتب غالبًا بالعربية على هذا الشكل الخاطئ: حزام كويبر) ويُسمى أيضاً حزام إدجوورث، هو عبارة عن منطقة من النظام الشمسي تتكون من الأجسام المتجمدة والصخور، تمتد من عند كوكب نبتون (30 وحدة فلكية (و.ف) إلى ما يقارب 55 و.ف بعيدة عن الشمس.[1] وهو مشابه لحزام الكويكبات الواقع بين كوكبي المريخ والمشتري مع أنه أعرض منه بعشرين مرة وأضخم منه بما بين 20 و200 مرة.[2][3] وكما حزام الكويكبات، يتكون حزام كايبر بشكل أساسي من أجسام صغيرة أو بقايا من مراحل تكون النظام الشمسي الأولية، لكن الأجسام في حزام الكويكبات تتكون بشكل رئيسي من الصخور والمعدن بينما تلك في حزام كايبر تتكون من "مواد متطايرة متجمدة"، هي عبارة مركبات كيميائية ذات نقطة غليان منخفضة تتواجد في القشرة أو الغلاف الجوي لبعض الكواكب والأقمار، وهي تسمى أيضا "بالجليدية"، ومن شاكلتها الميثان والأمونيا والماء، ويحتوي هذا الحزام على ثلاثة كواكب قزمة على الأقل هي: بلوتو وهاوميا وماكيماكي.";
//		List<String> expected = Arrays.asList(new String[]{
//			,"زام كايبر (بالإنكليزية: Kuiper belt؛ نقحرة: كايْبِر؛ بالرغم من أن الاسم يكتب غالبًا بالعربية على هذا الشكل الخاطئ: حزام كويبر) ويُسمى أيضاً حزام إدجوورث، هو عبارة عن منطقة من النظام الشمسي تتكون من الأجسام المتجمدة والصخور، تمتد من عند كوكب نبتون (30 وحدة فلكية (و.ف) إلى ما يقارب 55 و.ف بعيدة عن الشمس."
//,"وهو مشابه لحزام الكويكبات الواقع بين كوكبي المريخ والمشتري مع أنه أعرض منه بعشرين مرة وأضخم منه بما بين 20 و200 مرة."
//
//		assertEquals(expected,  
//				sentences.getStrings(text));
//	}

	
	
//	@Test
//	public void testGetSpans() {
//		List<Span> expected = new ArrayList<Span>(); 
//		expected.add(new Span(0, 76));
//		expected.add(new Span(77, 248));
//		expected.add(new Span(249, 404));
//		expected.add(new Span(405, 511));
//		expected.add(new Span(512, 601));
//
//		assertEquals(expected, sentences.getSpans(text));		
//	}

}
