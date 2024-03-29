package it.polimi.tiw.packets;

import java.util.List;

import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Product;

public class ProductOptions {
	private Product product;
	private List<Option> options;
	
	public ProductOptions(Product product, List<Option> options) {
		this.product=product;
		this.options=options;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public List<Option> getOptions() {
		return options;
	}
	
	

}
