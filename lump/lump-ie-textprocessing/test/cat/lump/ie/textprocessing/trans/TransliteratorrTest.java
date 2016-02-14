package cat.lump.ie.textprocessing.trans;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import cat.lump.ie.textprocessing.transform.Transliteratorr;

public class TransliteratorrTest {

	Transliteratorr trans;

	@Test
	public void testGetRussian() {
		trans = new Transliteratorr(new Locale("Russian"));
		
		Assert.assertEquals(
			"Éta kategoriâ soderžit 5 podkategorij iz 5 vsego.", 
			trans.get("Эта категория содержит 5 подкатегорий из 5 всего."));
	}
	
	@Test
	public void testGetGreek() {
		trans = new Transliteratorr(new Locale("Greek"));
		
		Assert.assertEquals(
			"To chēmikó stoicheío rḗnio eínai barý, polý dýstēkto, argyróleuko", 
			trans.get("Το χημικό στοιχείο ρήνιο είναι βαρύ, πολύ δύστηκτο, αργυρόλευκο"));
	}

}
