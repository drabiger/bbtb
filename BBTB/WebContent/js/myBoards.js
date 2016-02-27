require.config({
    baseUrl: 'js',
    
    shim: {
        "angular": {
            exports: "angular"
        },
        "angular-route": {
            deps: ["angular"]
        },
        'angular.animate': ['angular'],
        'angular.ui.bootstrap': ['angular'],
        'bootstrap' : ['jquery']
    },
    
    paths: {
    	"jquery" : "lib/jquery-2.1.4.min",
    	"bootstrap" : "lib/bootstrap.min",
    	"angular" : "lib/angular",
    	"angular.animate" : "lib/angular-animate",
    	"angular.ui.bootstrap" : "lib/ui-bootstrap-tpls-1.2.0",
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
require(['jquery', 'bootstrap', 'angular', 'angular.animate', 'angular.ui.bootstrap', 'bbtb/myBoardsController', 'bbtb/userController'],
function   ($, bootstrap, angular, angularAnimate, angularBootstrap, myBoardsController, userController) {
    //jQuery, bootstrap and angular are all
    //loaded and can be used here now.
	angular.element().ready(function() {
		// bootstrap the app manually
		angular.bootstrap(document, ['myBoards']);
	});
});