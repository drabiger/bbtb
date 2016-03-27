require
		.config({
			baseUrl : 'js',
			paths : {
				"angular-material" : "http://ajax.googleapis.com/ajax/libs/angular_material/1.0.0/angular-material.min",
				"jquery" : "lib/jquery-2.1.4.min",
				"bootstrap" : "lib/bootstrap.min",
				"angular" : "lib/angular",
				"angular.animate" : "lib/angular-animate",
				"angular-aria" : "lib/angular-aria-min",
				"angular.ui.bootstrap" : "lib/ui-bootstrap-tpls-1.2.0",
				"gapi" : "https://apis.google.com/js/api:client.js",
				waitSeconds : 40
			},
			shim : {
				"angular" : {
					exports : "angular"
				},
				'angular-material' : 'angular-aria',
				'angular.animate' : [ 'angular' ],
				'angular.ui.bootstrap' : [ 'angular' ],
				"bootstrap" : {
					deps : [ "jquery", "angular" ]
				},
				"bbtb/raceSelectionController" : {
					deps : [ "angular" ]
				},
			}
		});

define('gapi', [ 'https://apis.google.com/js/client.js' ], function() {
	console.log('gapi loaded');
	return gapi.client;
});

// Start the main app logic.
require([ 'jquery', 'bootstrap', 'angular', 'angular.animate', 
		'angular.ui.bootstrap', 'bbtb/raceSelectionController' ], function($,
		bootstrap, angular, angularAnimate, angularBootstrap,
		raceSelectionController) {
	// jQuery, bootstrap and angular are all
	// loaded and can be used here now.
	angular.element().ready(function() {
		// bootstrap the app manually
		angular.bootstrap(document, [ 'raceSelection' ]);
	});
});