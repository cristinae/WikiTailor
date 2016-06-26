package cat.lump.ir.lucene.query;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cat.lump.aq.basics.io.files.FileIO;
import cat.lump.ir.lucene.index.LuceneIndexerWT;
import cat.lump.ir.lucene.query.LuceneQuerierWT;

/** 
 * Test for the {@code LuceneQuerierWT} class.
 * An index id generated using LuceneIndexerWT and documents are 
 * queried with LuceneQuerierWT.
 * 
 * The test is done for Arabic because it is the language with more 
 * peculiarities
 * 
 * //TODO Far from being finished
 *  
 * @author cristina
 * @since Jul 9, 2015
 */
public class LuceneQuerierWParTest {

	private final static String relDir = File.separator + "tmpWParTest";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// Put mock documents in a directory
		String doc1 = "الصفحة الرئيسية" + "\n" + " __لافهرس__ __لاتحريرقسم__"; 
		String doc2 = " فعملية الهضم عندها تتم خارج بطنها، ولذلك تحتفظ بفرائسها حية، لكي تبقى طازجة." + "\n" +
						"صفاته	" + "\n" +
						"معظم العناكب لها أربعة أزواج من العيون تقع أعلى الجبهة من مقدمة الرأس. "; 
		String doc3 = " مارك ميزفنسكي " + "\n" +
						"مارك ميزفنسكي ولد في 15 ديسمبر 1977 وهو مصرفي تزوج من تشيلسي كلينتون ابنة هيلاري كلينتون وبيل كلينتون في 31 تموز / يوليو الزوجين اجتمع لأول مر " + "\n" +
						"ة في سن المراهقة في 1990 في حفل يقيمه الحزب الديمقراطي السياسية واستمرت صداقتهما في حين أن كلا يدرسون في جامعة ستانفورد درس مارك ميزفنسكي في ج " + "\n" +
						"امعة ستانفورد في بالو ألتو كاليفورنيا مارك لديه 9 من الأخوة مارك هو يهودي الديانة " + "\n" +
						"عرس القرن: زفاف تشيلسي كلينتون يتكلف ملايين الدولاراتhttp://www.bbc.co.uk/arabic/artandculture/2010/07/100731_chelsea_clinton_wedding.shtml"; 
		String doc4 = "الصفحة الرئيسية home is far"; 
		
		final String path = System.getProperty("user.dir") + relDir;
		new File(path).mkdirs();
		
		try {
			FileIO.stringToFile(new File(path, "1.ar.txt"), doc1, false);
			FileIO.stringToFile(new File(path, "2.ar.txt"), doc2, false);
			FileIO.stringToFile(new File(path, "3.ar.txt"), doc3, false);
			FileIO.stringToFile(new File(path, "4.ar.txt"), doc4, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		// Let's create the index
		LuceneIndexerWT lIndexer = new LuceneIndexerWT(new Locale("ar"), path, path);
		lIndexer.index();
		lIndexer.close();	

	}

	@AfterClass
	public static void cleanAfterClass() throws Exception {
		FileUtils.deleteDirectory(new File(System.getProperty("user.dir") + relDir));
	}
	
	
	@Test
	public void testQueryIDs() {
		String lang = "ar";
		String voc9555 ="على إلى ثمار فاك تفاح التي ثمر فيتام كما حيث عنب تمر انواع" +
		" تين لون زراع شجر هذه سكر بعض اشجار فوائد يحتو يمكن غذائ وهو انتاج احد نسب هذا اخر" +
		" مثل جسم ذلك ذات عالم وهي غير امراض بين تحتو موز هند جنوب اكثر نوع ولا اوراق عند فان" +
		" عمل اصناف تمور خارج يساعد كبير عام بعد ايضا سرط برتقال اصل عرب أيضا الذي مناطق كثير" +
		" وفي وقد ماء لكن عصير بذور حجم ومن كمثر فواك علاج تكون خاص حلو اسم ازهار صغير" +
		" حرار عال دول قشر ليم منها عاد تاريخ نمو تناول خلال مواد جنس صين يتم أنه طريق"; 
		String vocNum = "31"; 
		String vocEng = "home";
		String vocEngSW = "is";
		String vocDoc14 ="صفح";
		
		LuceneQuerierWT lQuerier = 
				new LuceneQuerierWT(lang, System.getProperty("user.dir") + relDir, 999);		
		lQuerier.loadIndex(new Locale(lang));
		
		//No English SW should be present
		String topDocsEngSW = lQuerier.queryIDs(vocEngSW);
		assertEquals("",topDocsEngSW);

		//English content words should be present
		String topDocsEng = lQuerier.queryIDs(vocEng);
		assertEquals("4\n",topDocsEng);

		//Numbers are removed when indexing
		String topDocsNum = lQuerier.queryIDs(vocNum);
		assertEquals("",topDocsNum);

		//Looking for a term
		String topDocs14 = lQuerier.queryIDs(vocDoc14);
		assertEquals("1\n4\n",topDocs14);

		//Looking for multiple terms
		String topDocs9555 = lQuerier.queryIDs(voc9555);
		assertEquals("2\n",topDocs9555);
	}

}
