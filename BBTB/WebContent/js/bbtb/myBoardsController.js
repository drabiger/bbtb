define([ 'angular' ], function(angular) {
	var app = angular.module('myBoards', [ 'user' ]);

	app.controller('MyBoardsController', [ '$http', '$scope', '$q', '$timeout',
			'authenticatedUser',
			function($http, $scope, $q, $timeout, authenticatedUser) {
				var thiz = this;
				
				var boards = null;

				initializeMyBoards = function(user) {
					loadMyBoards();
				};
				
				loadMyBoards = function() {
					$http.get('bbtb/api/boards/my').
					success(function(data, status, headers, config) {
				 		console.log("get test board", data);
				 		boards = data;
				    }).
				    error(function(data, status, headers, config) {
				    	// log error
				    	console.log("error fetching board. status=" + status);
				    });
				};
				
				this.getBoards = function() {
					return boards;
				};
		
				authenticatedUser.registerHandler(initializeMyBoards);
			} ]);
});

/*
 * to call a function inside the controller from the console, example:
 * angular.element(document.getElementById('TABLE')).scope().controller.initPlacements()
 */