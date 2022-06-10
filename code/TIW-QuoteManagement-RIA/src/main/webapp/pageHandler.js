{ // avoid variables ending up in the global scope

	// page components
	let personalMessage, createQuoteForm, quotesList, optionsList, unmanagedQuotesList,
		pageHandler = new PageHandler(); // main controller
		
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "Login.html";
		} else {
			pageHandler.start();
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
			//this.selectContainer.style.visibility = "hidden";
			this.selectContainer.style.display = "none";
			//this.optionContainer.style.visibility = "hidden";
			//this.requestQuoteBtn.style.visibility = "hidden";
		};

		this.clear = () => {
			var self = this;
			var links = this.optionContainer.querySelectorAll("option");
			links.forEach((link => {
				self.optionContainer.removeChild(link);
			}))
		};

		this.show = (selectedProduct, options) => {

			this.selectedProduct = selectedProduct;
			/*makeCall("GET", "GetOptionsData?productCode=" + selectedProduct, null,
				req => {
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
			);*/
			var elem;
			var self = this;
			options.forEach(option => {
				elem = document.createElement("option");
				elem.textContent = option.name;
				elem.setAttribute("value", option.code);
				self.optionContainer.appendChild(elem);
			});
			this.requestQuoteBtn.addEventListener("click", e => {
				this.createQuote(e);
			}, false);

			this.selectContainer.style.display = null;
		};

		/*
		this.update = options => {
			var elem;
			// build updated list
			var self = this;
			options.forEach(option => {
				elem = document.createElement("option");
				elem.textContent = option.name;
				elem.setAttribute("value", option.code);
				self.optionContainer.appendChild(elem);
			});
			//this.requestQuoteBtn.style.visibility = "visible";
			this.requestQuoteBtn.addEventListener("click", e => {
				this.createQuote(e);
			}, false);

			//this.optionContainer.style.visibility = "visible";
			//this.selectContainer.style.visibility = "visible";
			this.selectContainer.style.display = null;
		};*/

		this.createQuote = e => {
			var form = e.target.closest("form");
			if (form.checkValidity() && this.checkChosenOptions()) {
				var self = this;
				makeCall("POST", "CreateQuote?productCode=" + self.selectedProduct, form,
					req => {
						if (req.readyState == 4) {
							var message = req.responseText;
							if (req.status == 200) {
								self.warning.textContent = "";
								quotesList.show(true, false);
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

		this.checkChosenOptions = () => {
			var x = document.getElementById("id_selectOptions");
			if (x.length <= 0) {
				this.warning.textContent = "Invalid chosen options";
				return false;
			}
			for (index = 0; index < x.length; index++) {
				if (x.options[index] == null) {
					this.warning.textContent = "Invalid chosen options";
					return false;
				}
			}
			return true;
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

		this.show = selectedProduct => {
			var self = this;
			makeCall("GET", "GetProductsData", null,
				req => {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var productsToShow = JSON.parse(req.responseText);
							self.update(productsToShow);
							/*if (selectedProduct)
								next();*/
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						} else {
							self.warning.textContent = message;
						}
					}
				}
			);
		};

		this.update = objects => {
			var anchor;
			var self = this;
			objects.forEach(object => {
				anchor = document.createElement("a");
				linkText = document.createTextNode(object.product.name);
				anchor.appendChild(linkText);
				anchor.setAttribute('productCode', object.product.code);
				anchor.setAttribute('productName', object.product.name);
				anchor.addEventListener("click", e => {
					self.dropdownBtn.textContent = e.target.getAttribute("productName");
					optionsList.clear();
					//optionsList.show(e.target.getAttribute("productCode"));
					optionsList.show(object.product.code, object.options);
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



	function QuotesList(_warning, _quotesContainer, _noQuotes) {
		this.warning = _warning;
		this.quotesContainer = _quotesContainer;
		this.noQuotes = _noQuotes;
		//this.i = 0;

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
											//quotesList.showDetails(e.target.getAttribute("quoteId"));
										} else {
											e.target.textContent = "ViewDetails";
											//quotesList.hide(e.target.getAttribute("quoteId");
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

		this.show = (update, unmanaged) => {
			var self = this;
			this.clear;
			var servlet = "GetQuotesData";
			if (unmanaged) {
				servlet = "GetQuotesData?workerId=0";
			}
			makeCall("GET", servlet, null,
				req => {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var quotes = JSON.parse(req.responseText);
							if (quotes.length === 0) {
								self.noQuotes.textContent = "There are no quotes";
								//self.noQuotes.style.visibility = "visible";
							}
							else {
								self.noQuotes.textContent = "";
								//self.noQuotes.style.visibility = "hidden";
								if (update)
									self.update(self.quotesContainer, quotes[quotes.length - 1]);
								else {
									quotes.forEach(quote => {
										self.update(self.quotesContainer, quote);
									});
								}
								self.quotesContainer.style.visibility = "visible";
							}
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
		};

		this.update = (quotesContainer, quote) => {
			self = this;
			var button, panel;
			button = document.createElement("button");
			button.textContent = "Quote #" + quote.id;
			button.className = "accordion";
			button.id = quote.id;
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
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						} else {
							self.warning.textContent = message;
						}
					}
				}
			);
		};


		this.updateDetails = (quote, product, options, clientUsername, panel) => {

			let card, card_title, card_data, b1, b2, b3, br, form, d1;
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

			options.forEach((option => {

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
			} else if (sessionStorage.getItem('role') === "worker") {
				br = document.createElement("p");
				card_data.appendChild(br);
				form = document.createElement("form");
				form.className = "login-form";
				form.action = "#";
				d1 = document.createElement("div");
				d1.className = "form-group";
				b1 = document.createElement("label");
				b1.textContent = "Price";

				b2 = document.createElement("input");
				b2.type = "number";
				b2.min = "0";
				b2.placeholder = "Enter a price";
				b2.required = "true";
				b2.name = "price";
				b2.id = quote.id + "_price";

				b3 = document.createElement("button");
				b3.className = "btn btn-large btn-blue";
				b3.type = "submit";
				b3.textContent = "Update";
				b3.id = "id_updatePriceBtn";
				b3.addEventListener("click", e => {
					this.updatePrice(e, quote);
				}, false);
				d1.appendChild(b1);
				d1.appendChild(b2);
				d1.appendChild(b3);
				form.appendChild(d1);
				card_data.appendChild(form);
			}

			card.appendChild(card_data);
			d1 = document.createElement("div");
			d1.className = "warning-message";
			sp = document.createElement("span");
			sp.id = quote.id + "_warning";
			d1.appendChild(sp);
			card.appendChild(d1);
			br = document.createElement("br");
			card.appendChild(br);
			panel.appendChild(card);


			if (panel.style.maxHeight) {
				panel.style.maxHeight = null;
			} else {
				panel.style.maxHeight = panel.scrollHeight + "px";
			}
		};

		this.updatePrice = (e, quote) => {
			var form = e.target.closest("form");
			var self = this;
			if (form.checkValidity() && this.checkCorrectPrice(quote.id)) {
				makeCall("POST", "UpdatePrice?quoteId=" + quote.id, form,
					req => {
						if (req.readyState == 4) {
							var message = req.responseText;
							var elem;
							if (req.status == 200) {
								//self.warning.textContent = "you have correctly priced a quote";
								quotesList.clear();
								quotesList.show(false, false);
								elem = document.getElementById((String)(quote.id)).nextElementSibling;
								unmanagedQuotesList.quotesContainer.removeChild(elem);
								elem = document.getElementById((String)(quote.id));
								unmanagedQuotesList.quotesContainer.removeChild(elem);
							} else if (req.status == 403) {
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
							}
							else {
								document.getElementById(quote.id + "_warning").textContent = message;
							}
						}
					}
				);
			} else {
				form.reportValidity();
			}
			//this.reset();
		};

		this.checkCorrectPrice = quoteId => {
			var price = document.getElementById(quoteId + "_price");
			if (price == null || price <= 0) return false;
			return true;
		};

		this.clear = () => {
			var self = this;
			var quotes = this.quotesContainer.querySelectorAll("div,button");
			quotes.forEach((quote => {
				self.quotesContainer.removeChild(quote);
			}))
		};

		this.reset = function() {
			this.quotesContainer.style.visibility = "hidden";
		};
	}


	function PageHandler() {
		var warningContainer = document.getElementById("id_warning");

		this.start = () => {
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
			} else if ((sessionStorage.getItem('role') === "worker")) {
				unmanagedQuotesList = new QuotesList(
					warningContainer,
					document.getElementById("id_unmanagedQuotesList"),
					document.getElementById("id_noUnmanagedQuotes"));
				unmanagedQuotesList.show(false, true);
			}
			quotesList = new QuotesList(
				warningContainer,
				document.getElementById("id_quotesList"),
				document.getElementById("id_noQuotes"));
			quotesList.show(false, false);

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
				window.sessionStorage.removeItem('role');
			});
		};
	}
};