{ // avoid variables ending up in the global scope

	// page components
	let personalMessage, createQuoteForm, quoteList, optionsList,
		pageHandler = new PageHandler(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "Login.html";
		} else {
			pageHandler.start(); // initialize the components
		}
	}, false);


	// Constructors of view components

	class PersonalMessage {
		constructor(_username, usernameText) {
			this.username = _username;
			this.show = function() {
				usernameText.textContent = this.username;
			};
		}
	}

	function OptionsList(_warning, _selectContainer, _optionContainer, _requestQuoteBtn) {
		this.warning = _warning;
		this.selectContainer = _selectContainer;
		this.optionContainer = _optionContainer;
		this.requestQuoteBtn = _requestQuoteBtn;
		this.selectedProduct = -1;

		this.reset = () => {
			this.selectContainer.style.visibility = "hidden";
			this.optionContainer.style.visibility = "hidden";
			this.requestQuoteBtn.style.visibility = "hidden";
		};

		this.clear = () => {
			var self = this;
			var links = this.optionContainer.querySelectorAll("option");
			links.forEach((function(link) {
				self.optionContainer.removeChild(link);
			}))
		};

		this.show = (selectedProduct) => {
			var self = this;
			this.selectedProduct = selectedProduct;
			makeCall("GET", "GetOptionsData?productCode=" + selectedProduct, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var optionsToShow = JSON.parse(req.responseText);
							self.update(optionsToShow); // self visible by closure
						} else {
							self.warning.textContent = message;
						}
					}
				}
			);
		};


		this.update = (options) => {
			var elem;
			// build updated list
			var self = this;
			options.forEach(function(option) {
				elem = document.createElement("option");
				elem.textContent = option.name;
				elem.setAttribute("value", option.code);
				self.optionContainer.appendChild(elem);
			});
			this.requestQuoteBtn.style.visibility = "visible";
			this.requestQuoteBtn.addEventListener("click", (e) => {
				this.createQuote(e);
			}, false);

			this.optionContainer.style.visibility = "visible";
			this.selectContainer.style.visibility = "visible";
		};

		this.createQuote = (e) => {
			var form = e.target.closest("form");
			if (form.checkValidity()) {
				var self = this;
				makeCall("POST", "CreateQuote1?productCode=" + self.selectedProduct, form,
					function(req) {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								self.warning.textContent = "you have correctly requested a quote";
								quoteList.show(true);
							} else if (req.status == 403) {
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
							}
							else {
								self.warning.textContent = message;
							}
						}
					}
				);
			} else {
				form.reportValidity();
			}
			this.reset();
			createQuoteForm.getDropdownBtn().textContent = "";
		};
	}

	function CreateQuoteForm(_warning, _formContainer, _productContainer, _dropdownBtn) {
		this.warning = _warning;
		this.formContainer = _formContainer;
		this.productContainer = _productContainer;
		this.dropdownBtn = _dropdownBtn;

		this.reset = function() {
			this.formContainer.style.visibility = "hidden";
		};

		this.getDropdownBtn = () => {
			return this.dropdownBtn;
		}

		this.show = (selectedProduct) => {
			var self = this;
			makeCall("GET", "GetProductsData", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var productsToShow = JSON.parse(req.responseText);
							self.update(productsToShow); // self visible by closure
							if (selectedProduct)
								next(); // show the default element of the list if present

						} else {
							self.warning.textContent = message;
						}
					}
				}
			);
		};

		this.update = (products) => {
			var anchor;
			var self = this;
			products.forEach(function(product) {
				anchor = document.createElement("a");
				linkText = document.createTextNode(product.name);
				anchor.appendChild(linkText);
				anchor.setAttribute('productCode', product.code);
				anchor.setAttribute('productName', product.name);
				anchor.addEventListener("click", (e) => {
					self.dropdownBtn.textContent = e.target.getAttribute("productName")
					optionsList.clear();
					optionsList.show(e.target.getAttribute("productCode"));
				}, false);
				anchor.href = "#";

				self.productContainer.appendChild(anchor);
			});
			this.productContainer.style.visibility = "visible";

		};

		/*this.autoclick = function(productCode) {
			var e = new Event("click");
			var selector = "a[productCode='" + productCode + "']";
			var anchorToClick = // the first product or the selectedProduct
				(productCode) ? document.querySelector(selector) : this.productContainer.querySelectorAll("a")[0];
			if (anchorToClick)
				anchorToClick.dispatchEvent(e);
		};*/
	}



	function QuoteList(_warning, _quotesContainer, _noQuotes) {
		this.warning = _warning;
		this.quotesContainer = _quotesContainer;
		this.noQuotes = _noQuotes;
		this.i = 0;

		/*this.show = () => {
			var self = this;
			var card, card_title, card_data, text, b1, b1, viewBtn;
			this.clear;
			makeCall("GET", "GetQuotesData", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var quotes = JSON.parse(req.responseText);
							if (quotes.length === 0)
								self.noQuotes.style.visibility = "visible";
							else {
								self.noQuotes.style.visibility = "hidden";
								quotes.forEach(function(quote) {
									card = document.createElement("div");
									card.className = "card card-blue";
									if (self.i % 2 === 0)
										card.className += " even";
									card_title = document.createElement("div");
									card_title.className = "card-title";
									card_title.textContent = "Quote #" + quote.id;
									card.appendChild(card_title);
									card_data = document.createElement("div");
									card_data.className = "card-data";

									b1 = document.createElement("b");
									b1.textContent = "Product: " + quote.productCode;
									card_data.appendChild(b1);


									br = document.createElement("br");
									card_data.appendChild(br);

									b2 = document.createElement("b");
									b2.textContent = "Status: " + text;
									text = quote.workerId === 0 ? "waiting" : "processed";
									card_data.appendChild(b2);


									card.appendChild(card_data);
									viewBtn = document.createElement("a");
									viewBtn.className = "btn btn-purple btn-small btn-primary";
									viewBtn.textContent = "ViewDetails";
									viewBtn.setAttribute('quoteId', quote.id);
									viewBtn.addEventListener("click", (e) => {
										if (e.target.textContent === "ViewDetails") {
											e.target.textContent = "Hide";
											//quoteList.showDetails(e.target.getAttribute("quoteId"));
										} else {
											e.target.textContent = "ViewDetails";
											//quoteList.hide(e.target.getAttribute("quoteId");
										}
									});
									card.appendChild(viewBtn);
									self.quotesContainer.appendChild(card);
									self.i++;
								});
								self.quotesContainer.style.display = "block";
							}
							self.quotesContainer.style.visibility = "visible";
						}
						else {
							self.warning.textContent = message;

						}
					}
				}
			);
		};*/

		this.show = update => {
			var self = this;
			this.clear;
			makeCall("GET", "GetQuotesData", null,
				req => {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var quotes = JSON.parse(req.responseText);
							if (quotes.length === 0) {
								self.noQuotes.textContent = "You currently have no quotes";
								//self.noQuotes.style.visibility = "visible";
							}
							else {
								self.noQuotes.textContent = "";
								//self.noQuotes.style.visibility = "hidden";
								if (update === true)
									self.update(self.quotesContainer, quotes[quotes.length - 1]);
								else {
									quotes.forEach(function(quote) {
										self.update(self.quotesContainer, quote);
									});
								}
								self.quotesContainer.style.visibility = "visible";
							}
						}
						else {
							self.warning.textContent = message;

						}
					}
				}
			);
		};

		this.update = (quotesContainer, quote) => {
			self = this;
			var button, panel;
			button = document.createElement("button");
			button.textContent = "Quote #" + quote.id;
			button.className = "accordion";
			button.setAttribute("quoteid", quote.id);
			quotesContainer.appendChild(button);
			panel = document.createElement("div");
			panel.className = "panel";
			quotesContainer.appendChild(panel);
			button.addEventListener("click", e => {
				e.target.classList.toggle("active");
				panel = e.target.nextElementSibling;
				self.addDetails(panel, e.target.getAttribute("quoteId"));
			});
		};

		this.addDetails = (panel, quoteId) => {
			self = this;
			makeCall("GET", "GetQuoteDetails?quoteId=" + quoteId, null,
				req => {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var data = JSON.parse(req.responseText)
							self.updateDetails(data.quote, data.product, data.options, data.clientUsername, panel); // self visible by closure
						} else {
							self.warning.textContent = message;
						}
					}
				}
			);
		};


		this.updateDetails = (quote, product, options, clientUsername, panel) => {
			let card, card_title, card_data, b1, b2, br;
			while (panel.firstChild) {
				panel.firstChild.remove();
			}
			/*var details = panel.querySelectorAll("div");
			if (details.length > 0) {
				details.forEach((function(detail) {
					panel.removeChild(detail);
				}))
			}*/
			card = document.createElement("div");
			
			br = document.createElement("br");
			card.appendChild(br);
			
			card_title = document.createElement("div");
			card_title.className = "card-title";
			card_title.textContent = product.name;
			card.appendChild(card_title);
			card_data = document.createElement("div");
			card_data.className = "card-data";

			b2 = document.createElement("b");
			b2.textContent = "Product code: ";
			card_data.appendChild(b2);
			card_data.appendChild(document.createTextNode(product.code));
			br = document.createElement("br");
			card_data.appendChild(br);

			if (sessionStorage.getItem("role") === "worker") {
				b2 = document.createElement("b");
				b2.textContent = "Client username: ";
				card_data.appendChild(b2);
				card_data.appendChild(document.createTextNode(clientUsername));
				br = document.createElement("br");
				card_data.appendChild(br);
			}

			options.forEach((function(option) {

				b1 = document.createElement("b");
				b1.textContent = "Option: ";
				card_data.appendChild(b1);
				card_data.appendChild(document.createTextNode(option.name));

				br = document.createElement("br");
				card_data.appendChild(br);

				b2 = document.createElement("b");
				b2.textContent = "Type: ";
				card_data.appendChild(b2);
				card_data.appendChild(document.createTextNode(option.type));
				br = document.createElement("br");
				card_data.appendChild(br);


			}
			))

			b2 = document.createElement("b");
			b2.textContent = "Status: ";
			card_data.appendChild(b2);
			card_data.appendChild(document.createTextNode(quote.workerId === 0 ? "waiting" : "processed"));

			if (quote.workerId !== 0) {
				br = document.createElement("br");
				card_data.appendChild(br);
				b4 = document.createElement("b");
				b4.textContent = "Price: ";
				card_data.appendChild(b4);
				card_data.appendChild(document.createTextNode(quote.price));
			}
			
			card.appendChild(card_data);
			br = document.createElement("br");
			card.appendChild(br);
			panel.appendChild(card);
			
			
			if (panel.style.maxHeight) {
				panel.style.maxHeight = null;
			} else {
				panel.style.maxHeight = panel.scrollHeight + "px"
			}
		};

		this.clear = () => {
			var self = this;
			var quotes = this.quotesContainer.querySelectorAll("div");
			quotes.forEach((function(quote) {
				self.quotesContainer.removeChild(quote);
			}))
		};

		this.reset = function() {
			this.quotesContainer.style.visibility = "hidden";
		};
	}



	function PageHandler() {
		var warningContainer = document.getElementById("id_warning");

		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'), document.getElementById("id_username"));
			personalMessage.show();
			if (sessionStorage.getItem('role') === "client") {
				createQuoteForm = new CreateQuoteForm(
					warningContainer,
					document.getElementById("id_createQuoteContainer"),
					document.getElementById("id_productContainer"),
					document.getElementById("id_dropdownBtn"));
				createQuoteForm.show(null);
				optionsList = new OptionsList(
					warningContainer,
					document.getElementById("id_selectContainer"),
					document.getElementById("id_selectOptions"),
					document.getElementById("id_requestQuoteBtn"));
				optionsList.reset();
			}
			quoteList = new QuoteList(
				warningContainer,
				document.getElementById("id_quotesList"),
				document.getElementById("id_noQuotes"));
			quoteList.show(false);


			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			});
		};
	}
};