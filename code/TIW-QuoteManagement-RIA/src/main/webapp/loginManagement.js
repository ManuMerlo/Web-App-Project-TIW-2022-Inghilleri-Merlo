/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

	var open_register = document.getElementById("open_register");
	var title = document.getElementById("title");
	var form = document.getElementById("f1");
	var loginButton = document.getElementById("loginButton");
	//var passwordInput = loginButton.closest("form").querySelector('input[name="password"]');
	//var repeatPasswordInput = loginButton.closest("form").querySelector('input[name="repeatPassword"]');
	var warning = document.getElementById("warning");

	//TODO ci sono 2 variabili che si chiamano form

	loginButton.addEventListener('click', e => {
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			if (loginButton.textContent === "Login") {
				makeCall("POST", 'CheckLogin', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var data;
							switch (x.status) {
								case 200:
									data = JSON.parse(x.responseText)
									sessionStorage.setItem('username', data.username);
									sessionStorage.setItem('role', data.role);
									if (data.role === "client")
										window.location.href = "ClientHome.html";
									else if (data.role === "worker")
										window.location.href = "WorkerHome.html";
									break;
								case 400: // bad request
								case 401: // unauthorized
								case 500: // server error
									data = x.responseText;
									warning.textContent = data;
									break;
							}
						}
					}
				);
			} else {
				if (document.getElementById("passwordInput").value != document.getElementById("repeatPassword").value) {
					warning.textContent = "Passwords do not match";
					return;
				}
				makeCall("POST", 'Register', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var data;
							switch (x.status) {
								case 200:
									document.getElementById("open_register").textContent = "Register Now";
									returnToLogin();
									break;
								case 400: // bad request
								case 401: // unauthorized
								case 500: // server error
									data = x.responseText;
									document.getElementById("warning").textContent = data;
									break;
							}
						}
					}
				);
			}

		} else {
			form.reportValidity();
		}
	});

	function insertAfter(newNode, existingNode) {
		existingNode.parentNode.insertBefore(newNode, existingNode.nextSibling);
	}

	function returnToLogin() {
		title.textContent = "Insert your credentials to Login";
		loginButton.textContent = "Login";
		form.removeChild(document.getElementById("username"));
		form.removeChild(document.getElementById("repeatPwd"));
	}

	open_register.addEventListener("click", e => {
		if (e.target.textContent === "Register Now") {
			e.target.textContent = "Go to login";
			title.textContent = "Insert your credentials to Register";
			loginButton.textContent = "Register";
			const usernameDiv = document.createElement("div");
			usernameDiv.id = "username";
			usernameDiv.className = "form-group";
			var label = document.createElement("Label");
			label.setAttribute("for", "username");
			label.textContent = "Username";
			usernameDiv.appendChild(label);
			var input = document.createElement("input");
			input.name = "username";
			input.type = "text";
			input.placeholder = "Enter your username";
			input.required = true;
			usernameDiv.appendChild(input);
			var currentDiv = document.getElementById("email");
			form.insertBefore(usernameDiv, currentDiv);
			const repeatPwdDiv = document.createElement("div");
			repeatPwdDiv.id = "repeatPwd";
			repeatPwdDiv.className = "form-group";
			label = document.createElement("Label");
			label.setAttribute("for", "repeatPassword");
			label.textContent = "Repeat Password";
			repeatPwdDiv.appendChild(label);
			input = document.createElement("input");
			input.name = "repeatPassword";
			input.id = "repeatPassword";
			input.type = "password";
			input.placeholder = "Repeat your password";
			input.required = true;
			repeatPwdDiv.appendChild(input);
			currentDiv = document.getElementById("password");
			insertAfter(repeatPwdDiv, currentDiv);
		} else {
			e.target.textContent = "Register Now";
			returnToLogin();
			//title.textContent = "Insert your credentials to Login";
			//loginButton.textContent = "Login";
			//form.removeChild(document.getElementById("username"));
			//form.removeChild(document.getElementById("repeatPwd"));

		}
	});

})();