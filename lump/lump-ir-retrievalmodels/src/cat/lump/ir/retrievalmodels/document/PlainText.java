package cat.lump.ir.retrievalmodels.document;

import java.io.Serializable;

import cat.lump.aq.basics.check.CHK;

class PlainText implements Serializable{//{extends Representation{

	

//	protected PlainText(Dictionary dictionary, Locale language) {
//		super(dictionary, language);
//	}

	/** Auto generated ID for serialization */
	private static final long serialVersionUID = 5902889493438135812L;
	
	private String text;
	
//	@Override
	public void setText(String text){
		CHK.CHECK_NOT_NULL(text);
		this.text = text;
	}
	
//	@Override
	public String getText(){
		return text;
	}

//	@Override
//	protected String preprocess(String text) {
//		return text;
//	}
	
}
