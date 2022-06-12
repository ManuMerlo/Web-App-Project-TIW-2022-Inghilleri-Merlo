/*(function() {

	if (!sessionStorage.length) {
		// Ask other tabs for session storage
		// Se la sessione è vuota, chiedo la sessione ad altre tab 
		localStorage.setItem('getSessionStorage', Date.now());
	};

	//we have a component that will listen to all storage events
	window.addEventListener('storage', function(event) {

	
		if (event.key == 'getSessionStorage') {
			// Some tab asked for the sessionStorage -> send it
			// Appena accedo alla pagina nuova, invio un evento richiedendo i dati
			// localStorage.setItem genera un altro evento, con key Session storage dunque la pagina che 
			// ha richiesto la sessione, può leggerla tramite il localStorage.
			localStorage.setItem('sessionStorage', JSON.stringify(sessionStorage));
			//subito dopo aver messo il valore, viene tolto per non rendereil salvataggio permanente alla chiusura del browser
			localStorage.removeItem('sessionStorage');

		} else if (event.key == 'sessionStorage' && !sessionStorage.length) {
			// viene preso il valore caricato nel localStorage
			var data = JSON.parse(event.newValue);

			for (key in data) {
				sessionStorage.setItem(key, data[key]);
			}
		}
	});

	window.onbeforeunload = function() {
		//sessionStorage.clear();
	};

})();*/

(function() {

	if (!sessionStorage.length) {
		// Ask other tabs for session storage
		// Se la sessione è vuota, chiedo la sessione ad altre tab 
		localStorage.setItem('getSessionStorage', Date.now());
	};

	//we have a component that will listen to all storage events
	window.addEventListener('storage', function(event) {

	
		if (event.key == 'getSessionStorage') {
			// Some tab asked for the sessionStorage -> send it
			// Appena accedo alla pagina nuova, invio un evento richiedendo i dati
			// localStorage.setItem genera un altro evento, con key Session storage dunque la pagina che 
			// ha richiesto la sessione, può leggerla tramite il localStorage.
			localStorage.setItem('sessionStorage', JSON.stringify(sessionStorage));
			//subito dopo aver messo il valore, viene tolto per non rendereil salvataggio permanente alla chiusura del browser
			localStorage.removeItem('sessionStorage');

		} else if (event.key == 'sessionStorage' && !sessionStorage.length) {
			// viene preso il valore caricato nel localStorage
			var data = JSON.parse(event.newValue);

			for (key in data) {
				sessionStorage.setItem(key, data[key]);
			}
		} else if(event.key == 'reloadPage')
		{
			window.location.reload();
		}
	});

	window.onbeforeunload = function() {
		//sessionStorage.clear();
	};

})();