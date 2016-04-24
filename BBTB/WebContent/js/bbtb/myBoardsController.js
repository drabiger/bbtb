define([ 'angular' ], function(angular) {
	var app = angular.module('myBoards', [ 'user', 'ngAnimate', 'ui.bootstrap' ]);

	app.controller('MyBoardsController', [ '$http', '$scope', '$q', '$timeout',
			'authenticatedUser',
			function($http, $scope, $q, $timeout, authenticatedUser) {
				var thiz = this;
				
				this.boards = null;
				
				this.totalItems = 0;
				
				this.currentPage = 1;
				
				this.itemsPerPage = 5;
				
				this.initialized = false;

				loadMyBoards = function() {
					$http.get('bbtb/api/boards/my').
					success(function(data, status, headers, config) {
				 		console.log("get test board", data);
				 		thiz.boards = data;
				 		thiz.totalItems = thiz.boards.length;
				    }).
				    error(function(data, status, headers, config) {
				    	// log error
				    	console.log("error fetching board. status=" + status);
				    });
				};
				
				this.getBoardsPaginated = function() {
					if(thiz.boards) {
						var startIdx = (thiz.currentPage - 1) * thiz.itemsPerPage;
						var endIdx = startIdx + thiz.itemsPerPage;
						return thiz.boards.slice(startIdx, endIdx);
					} else {
						return null;
					}
				};
				
				this.createBoard = function() {
					$http.post('bbtb/api/boards').
					then(function(response) {
				 		console.log("create board", response);
				 		var location = response.headers("location");
				 		var splitResponse = location.split("/");
				 		var uuid = splitResponse[splitResponse.length-1];
				 		window.location.replace("board.html?b=" + uuid);
				    }, 
				    function(response) {
				    	// log error
				    	console.log("error creating board. response=" + response);
				    });
				};
				
				this.pageChanged = function() {
					console.log('Page changed to: ' + thiz.currentPage);
				};
		
				authenticatedUser.registerHandler(initializeMyBoards);
			} ]);
});

/*
 * to call a function inside the controller from the console, example:
 * angular.element(document.getElementById('TABLE')).scope().controller.initPlacements()
 */