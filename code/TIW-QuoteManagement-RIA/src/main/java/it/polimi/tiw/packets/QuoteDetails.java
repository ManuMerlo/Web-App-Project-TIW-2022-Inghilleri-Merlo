package it.polimi.tiw.packets;

import java.util.List;

import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.beans.Quote;

public class QuoteDetails {

	private Quote quote;
	private Product product;
	private List<Option> options;
	
	public QuoteDetails(Quote quote,Product product, List<Option> options){
		this.quote=quote;
		this.product = product;
		this.options = options;
	}
	
	public Quote getQuote() {
		return quote;
	}
	public Product getProduct() {
		return product;
	}
	
	public List<Option> getOptions(){
		return options;
	
	}
}
