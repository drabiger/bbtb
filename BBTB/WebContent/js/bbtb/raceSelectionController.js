define([ 'angular' ], function(angular) {
	var app = angular.module('raceSelection', [ 'ui.bootstrap', 'ngColorPicker' ]);

	var controller = function($http, $scope, $uibModalInstance, currentSelection) {
		$scope.items = [];

		$scope.selectedItem = currentSelection.item;

		$scope.preSelection = currentSelection.item;

		$scope.setting = false;

		$scope.selectedColor = currentSelection.color;
		
		$http.get('bbtb/api/races/all').success(
				function(data, status, headers, config) {
					$scope.items = data;
					console.log("get races", data);
				}).error(function(data, status, headers, config) {
			// log error
			console.error("error");
		});
		
		$scope.getItems = function() {
			return $scope.items;
		};

		$scope.getSelectedItem = function() {
			return $scope.selectedItem;
		};

		$scope.setSelectedItem = function(item) {
			$scope.selectedItem = item;
		};

		$scope.ok = function() {
			$uibModalInstance.close({item: $scope.selectedItem, color: $scope.selectedColor });
		};

		$scope.cancel = function() {
			$uibModalInstance.dismiss('cancel');
		};
	};

	app.controller('raceSelectionController', [ '$http', '$scope', 
			'$uibModalInstance', 'currentSelection', controller ]);
});
