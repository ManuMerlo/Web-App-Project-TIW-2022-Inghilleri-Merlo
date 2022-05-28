package it.polimi.tiw.beans;


public class Quote {
	private int id;
	private int clientId;
	private int productCode;
	private int workerId;
	private int price;
	private Product product;
	
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getClientId() {
		return clientId;
	}
	
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	public int getProductCode() {
		return productCode;
	}
	
	public void setProductCode(int productCode) {
		this.productCode = productCode;
	}
	
	public int getWorkerId() {
		return workerId;
	}
	
	public void setWorkerId(int workerId) {
		this.workerId = workerId;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	
	
}
