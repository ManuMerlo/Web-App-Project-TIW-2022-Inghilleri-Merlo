/**
 * Login management
 */

(() => {

	var open_register = document.getElementById("open_register"),
		title = document.getElementById("title"),
		form = document.getElementById("f1"),
		loginButton = document.getElementById("loginButton"),
		warning = document.getElementById("warning");

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") != null) {
			if (sessionStorage.getItem("role") == "client")
				window.location.href = "ClientHome.html";
			else if (sessionStorage.getItem("role") == "worker")
				window.location.href = "WorkerHome.html";
		}
	}, false);

	loginButton.addEventListener('click', e => {
		if (form.checkValidity() && checkEmail(document.getElementById("emailInput").textContent)) {
			if (loginButton.textContent === "Login") {
				makeCall("POST", 'CheckLogin', e.target.closest("form"),
					x => {
						if (x.readyState == XMLHttpRequest.DONE) {
							var data;
							switch (x.status) {
								case 200:
									data = JSON.parse(x.responseText)
									sessionStorage.setItem('username', data.username);
									sessionStorage.setItem('role', data.role);
									localStorage.setItem('reloadPage', Date.now());
									localStorage.removeItem('reloadPage');
									if (data.role === "client")
										window.location.href = "ClientHome.html";
									else if (
										data.role === "worker")
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
				if (document.getElementById("passwordInput").value != document.getElementById("repeatPasswordInput").value) {
					warning.textContent = "Passwords do not match";
					e.preventDefault();
					return;
				}
				makeCall("POST", 'Register', e.target.closest("form"),
					x => {
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
									warning.textContent = data;
									break;
							}
						}
					}
				);
			}

		} else {
			e.preventDefault();
			form.reportValidity();
		}
	});

	function returnToLogin() {
		title.textContent = "Insert your credentials to Login";
		loginButton.textContent = "Login";
		warning.textContent = "";
		document.getElementById("username").style.display = "none";
		document.getElementById("usernameInput").required = false;
		document.getElementById("repeatPwd").style.display = "none";
		document.getElementById("repeatPasswrodInput").required = false;
	}

	function checkEmail(email) {
		// recupero il valore della email indicata nel form
		var email = form.emailInput.value;
		// se non ho inserito nulla nel campo
		if (email == '' || !(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email))) {
			warning.textContent = "Invalid email format";
			swal("Error!", "Invalid email format", "error");
			return false;
		}

		return true;
	}

	open_register.addEventListener("click", e => {
		if (e.target.textContent === "Register Now") {
			e.target.textContent = "Go to login";
			title.textContent = "Insert your credentials to Register";
			loginButton.textContent = "Register";
			document.getElementById("username").style.display = null;
			document.getElementById("usernameInput").required = true;
			document.getElementById("repeatPwd").style.display = null;
			document.getElementById("repeatPasswrodInput").required = true;
		} else {
			e.target.textContent = "Register Now";
			returnToLogin();
		}
	});

})();

