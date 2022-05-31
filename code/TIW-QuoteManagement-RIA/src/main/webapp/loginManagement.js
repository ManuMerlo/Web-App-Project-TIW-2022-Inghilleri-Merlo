/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("loginButton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'CheckLogin', e.target.closest("form"),
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
	        var data;
            switch (x.status) {
              case 200:
                data = JSON.parse(x.responseText)
            	sessionStorage.setItem('username', data.username);
            	if(data.role==="client")
                window.location.href = "ClientHome.html";
                else if (data.role==="worker")
                window.location.href = "WorkerHome.html";
                break;
              case 400: // bad request
              case 401: // unauthorized
              case 500: // server error
                data=x.responseText;
            	document.getElementById("warning").textContent = data;
                break;
            }
          }
        }
      );
    } else {
  	 form.reportValidity();
    }
  });

})();