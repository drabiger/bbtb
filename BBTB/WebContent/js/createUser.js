require.config({
    baseUrl: 'js',
    
    shim: {
        "angular": {
            exports: "angular"
        },
        "angular-route": {
            deps: ["angular"]
        }
    },
    
    paths: {
    	"jquery" : "lib/jquery-2.1.4.min",
    	"bootstrap" : "lib/bootstrap.min",
    	"angular" : "lib/angular",
    	"gapi" : "https://apis.google.com/js/api:client.js",
    	waitSeconds: 40
    }
});

define('gapi', ['https://apis.google.com/js/client.js'],
	    function(){
	        console.log('gapi loaded');
	        return gapi.client;
	    }
	);

// Start the main app logic.
require(['jquery', 'bootstrap', 'angular', 'bbtb/login', 'bbtb/userController', 'bbtb/userCreateFormController'],
function   ($, bootstrap, angular, gapi, userController, userCreateFormController) {
    //jQuery, bootstrap and angular are all
    //loaded and can be used here now.
	angular.element().ready(function() {
		// bootstrap the app manually
		angular.bootstrap(document, ['userCreation']);
	});
});