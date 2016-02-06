define(['angular'], function(angular) {
	var app = angular.module('board', ['user']);

	app.controller('BoardController', [ '$http', '$scope', '$q', '$timeout',
			function($http, $scope, $q, $timeout) {
				var thiz = this;
				
				var board = [['o','o','o','o','|','o','o','o','o','o','o','o','|','o','o','o','o'],
				             ['-','-','-','-','-','-','-','-','-','-','-','-','-','-','-','-','-'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
				             ['=','=','=','=','=','=','=','=','=','=','=','=','=','=','=','=','='],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['x','x','x','x','|','x','x','x','x','x','x','x','|','x','x','x','x'],
							 ['-','-','-','-','-','-','-','-','-','-','-','-','-','-','-','-','-'],
							 ['o','o','o','o','|','o','o','o','o','o','o','o','|','o','o','o','o']
				             ];
				
				var boardModel;
				
				var race1Model;
				
				var race2Model;
				
				var boardModelIdx = new Array;	// (x,y) of board -> (xx,yy) of boardModel
				
				var boardCells = new Array; 	// Koordinaten des models
				
				var boardCellStyle = new Array; // Koordinaten des models
				
				var uuid2positions = undefined;
				
				var boardModelDirty = false;
				
				// public
				this.getBoard = function() {
					return board;
				};
				
				// private
				this.initBoardModel = function() {
					 $http.get('bbtb/api/boards/TEST').
					 	success(function(data, status, headers, config) {
					 		thiz.boardModel = data;
					 		console.log("get test board", thiz.boardModel);
					 		thiz.setBoardCells();
					 		
					 		var promise1 = $http.get('bbtb/api/races/' + thiz.boardModel.race1.uuid).error(function(data, status, headers, config) {
					 			console.log("Could not fetch data for race1");
					 		});
					 		var promise2 = $http.get('bbtb/api/races/' + thiz.boardModel.race2.uuid).error(function(data, status, headers, config) {
					 			console.log("Could not fetch data for race2");
					 		});
					 		$q.all({race1Promise : promise1, race2Promise: promise2 }).then(function(data) {
					 			thiz.race1Model = data.race1Promise.data;
					 			thiz.race2Model = data.race2Promise.data;
					 			console.log("race1 ", thiz.race1Model);
					 			console.log("race2 ", thiz.race2Model);
					 			thiz.setBoardCellStyle();
					 			$timeout(function () {
					 				thiz.initPlacements();
					 			}, 0, false);
					 		});
					 		
					    }).
					    error(function(data, status, headers, config) {
					    	// log error
					    	console.log("error fetching board. status=" + status);
					    });
				};
				
				// private
				this.initBoardModelIdx = function() {
					// modelRow: 0..24
					var modelRow = undefined;
					
					// row: 0...24
					for (var row = 0; row < board.length; ++row) {
						boardModelIdx[row] = new Array;
						boardCells[row] = new Array(board[0].length);
						boardCellStyle[row] = new Array(board[0].length);

						// modelCol: 0...14
						var modelCol = undefined;
						
						// col: 0...14
						for (var col = 0; col < board[0].length; ++col) {
							boardModelIdx[row][col] = new Array;

							if(board[row][col] === 'x' || board[row][col] === 'o') {
								if(modelCol == undefined) {
									modelCol = 0;
									if(modelRow == undefined) {
										modelRow = 0;
									} else {
										modelRow++;
									}
								} else {
									modelCol++;
								}
								// console.log("col="+col+", row="+row + " -> ("+modelRow + "," + modelCol+")");
								// modelRow = Y value on server, modelCol = X value on server
								boardModelIdx[row][col] = [modelCol, modelRow];
							}
						}
					}
				};
				
				// public: (xx,yy) of board -> (x,y) of boardModel
				this.getModelXValue = function(row, col) {
					// console.log("getModel! xx="+boardModelIdx[x][y][0] + ", yy="+boardModelIdx[x][y][1]);
					if(!boardModelIdx[row][col]) {
//						console.log("undefined boardModelIdx for row=" + row + ", col="+ col);
						return undefined;
					} else {
//						console.log("("+row+","+col+") -> (" + boardModelIdx[row][col][0] + ", "+boardModelIdx[row][col][1] + ")");
						return boardModelIdx[row][col][0];
					}
				};
				
				// public
				this.getModelYValue = function(row, col) {
					// console.log("getModel! xx="+boardModelIdx[x][y][0] + ", yy="+boardModelIdx[x][y][1]);
					if(!boardModelIdx[row][col]) {
//						console.log("undefined boardModelIdx for row=" + row + ", col="+ col);
						return undefined;
					}
					return boardModelIdx[row][col][1];
				};
				
				// private
				this.setBoardCells = function() {
					for (var i = 0; i < thiz.boardModel.placements.length; ++i) {
						var p = thiz.boardModel.placements[i];
//						console.log("placement: ", p);
						boardCells[p.x][p.y] = p;
					}
				};
				
				
				// public
				this.getBoardCells = function(row, col) {
					var modelX = thiz.getModelXValue(row,col);
					var modelY = thiz.getModelYValue(row,col);
					if (typeof modelX !== 'undefined' && typeof modelY !== 'undefined' && boardCells[modelX][modelY]) {
						return boardCells[modelX][modelY].position.abbreviation;
					}
				};
				
				// public: get placement with (x,y) of board coordinates which will be mapped to boardModel coordinates
				this.getBoardPlacement = function(row, col) {
					var modelRow = thiz.getModelXValue(row,col);
					var modelCol = thiz.getModelYValue(row,col);
					if (typeof modelRow !== 'undefined' && typeof modelCol !== 'undefined' && boardCells[modelRow][modelCol]) {
						return boardCells[modelRow][modelCol].uuid;
					}
				};				
				
				// private
				this.setBoardCellStyle = function() {
					if(!uuid2positions) {
						uuid2positions = new Object;
						for(var i = 0; i < thiz.race1Model.positions.length; ++i) {
							uuid2positions[thiz.race1Model.positions[i].uuid] = {positions: thiz.race1Model.positions[i], race: thiz.race1Model };
						}
						for(var i = 0; i < thiz.race2Model.positions.length; ++i) {
							uuid2positions[thiz.race2Model.positions[i].uuid] = {positions: thiz.race2Model.positions[i], race: thiz.race2Model };
						}
					}
					
					for (var i = 0; i < thiz.boardModel.placements.length; ++i) {
						var p = thiz.boardModel.placements[i];
						thiz.setCellStyle(p);
					}
				};
				
				// private
				this.setCellStyle = function(placement) {
//					console.log("placement: ", placement);
					
					var positionsMap = uuid2positions[placement.position.uuid];
					if(!positionsMap) {
						console.log("error: cannot find position with uuid " + placement.position.uuid + " in uuid2positions");
						return;
					}
					var raceOfPlacement = positionsMap.race;
//					console.log("setting cell style for (" + placement.x + "," + placement.y + ")");
					if(raceOfPlacement.uuid === thiz.race1Model.uuid) {
						boardCellStyle[placement.x][placement.y] = thiz.getRace1CellStyle();
					}
					if(raceOfPlacement.uuid === thiz.race2Model.uuid) {
						boardCellStyle[placement.x][placement.y] = thiz.getRace2CellStyle();
					}
				};
				
				// public
				this.getRace1CellStyle = function() {
					return { "background-color" : thiz.boardModel.colorRace1, "cursor" : "move" };
				};

				// public
				this.getRace2CellStyle = function() {
					return { "background-color" : thiz.boardModel.colorRace2, "cursor" : "move" };
				};
				
				// private
				this.clearCellStyle = function(x, y) {
					console.log("clearing cell style for (" + x + "," + y + ")");
					boardCellStyle[x][y] = null;
				};
				
				// public
				this.getBoardCellStyle = function(x, y) {
					var modelX = thiz.getModelXValue(x,y);
					var modelY = thiz.getModelYValue(x,y);
					if (typeof modelX !== 'undefined' && typeof modelY !== 'undefined') {
						return boardCellStyle[modelX][modelY];
					}
				};
				
				// private
				this.initPlacements = function() {
					console.log("initPlacements");
					// set event handlers based on 'draggable' property
					
					// dragstart 1) for all placements on board 2) for all positions of race tables
					$("td.bbtb-boardCell").off('dragstart');
					$("td.bbtb-boardCell").on('dragstart', thiz.dragStart);
					
					// drop only for all placements on board
					$("table.bbtb-board td.bbtb-boardCell").off('drop');
					$("table.bbtb-board td.bbtb-boardCell").on('drop', thiz.dragStop);
					
					// dragover only for all placements on board
					$("table.bbtb-board td.bbtb-boardCell").off('dragover');
					$("table.bbtb-board td.bbtb-boardCell").on('dragover', function(event) {
						if(event.originalEvent.target.getAttribute("x-bbtb-placement").length == 0) {
							// allow drop if target is not occupied by placement
							event.preventDefault();
						}
					});
					
					// add drop and dragover for removing placements from board
					$("#bbtb-race1").off('drop');
					$("#bbtb-race1").on('drop', thiz.dragStopRemoveFromBoard);
					$("#bbtb-race1").on('dragover', function(event) {
						event.preventDefault();
					});
					$("#bbtb-race2").off('drop');
					$("#bbtb-race2").on('drop', thiz.dragStopRemoveFromBoard);
					$("#bbtb-race2").on('dragover', function(event) {
						event.preventDefault();
					});
				};
				
				// public, reference to this function is injected in initPlacements()
				this.dragStart = function(event, ui) {
					console.log("dd start (" + event.originalEvent.target.getAttribute("x-bbtb-board-x") + "," + event.originalEvent.target.getAttribute("x-bbtb-board-y") + ")");
					event.originalEvent.dataTransfer.setData("placement", event.originalEvent.target.getAttribute("x-bbtb-placement"));
					event.originalEvent.dataTransfer.setData("position", event.originalEvent.target.getAttribute("x-bbtb-position"));
					event.originalEvent.dataTransfer.setData("source-x", event.originalEvent.target.getAttribute("x-bbtb-board-x"));
					event.originalEvent.dataTransfer.setData("source-y", event.originalEvent.target.getAttribute("x-bbtb-board-y"));
				};
				
				// public, reference to this function is injected in initPlacements()
				this.dragStop = function(event) {
					var sourceX = event.originalEvent.dataTransfer.getData("source-x");
					if(sourceX === 'new') {
						var positionUuid = event.originalEvent.dataTransfer.getData("position");
						thiz.createNewPlacement(positionUuid, event.target);
					} else {
						var placementUuid = event.originalEvent.dataTransfer.getData("placement");
						thiz.movePlacement(placementUuid, event.target);
					}
				};
				
				// public, reference to this function is injected in initPlacements()
				this.dragStopRemoveFromBoard = function (event) {
					var sourceX = event.originalEvent.dataTransfer.getData("source-x");
					if(sourceX === 'new') {
						// do nothing if a new placement has been dropped into the race tables
						return;
					} else {
						var placementUuid = event.originalEvent.dataTransfer.getData("placement");
						thiz.removePlacement(placementUuid);
					}
				};
				
				// private
				// called by dragStop()
				this.movePlacement = function(placementUuid, targetElement) {
					for (var i = 0; i < thiz.boardModel.placements.length; ++i) {
						var p = thiz.boardModel.placements[i];
						if(p.uuid === placementUuid) {
							thiz.persistPlacement(p, targetElement);
						}
					}
				};
				
				this.removePlacement = function(placementUuid) {
					for (var i = 0; i < thiz.boardModel.placements.length; ++i) {
						var p = thiz.boardModel.placements[i];
						if(p.uuid === placementUuid) {
							$http.delete('/bbtb/api/boards/' + thiz.boardModel.uuid + '/placements/' + p.uuid).
							 then(function(response) {
								  console.log('delete success. location=', response.headers('Location'));
								  
								  // TODO
								  // actually, we would only need to remove the placement from boardModel.
								  // this call is simpler but triggers some network overhead
								  boardCells[p.x][p.y] = undefined;
								  boardCellStyle[p.x][p.y] = undefined;
								  thiz.initBoardModel();
							  },
							  function(response) {
								  console.log('error deleting placement');
							  });
						};
					};
				};
				
				// private
				// called by dragStop()
				this.createNewPlacement = function(positionUuid, targetElement) {
					var position = thiz.getPositionByUuid(positionUuid);
					if (position) {
						var placement = {
								'position' : position,
								'x' : 'new',
								'y' : 'new'
						};
						thiz.persistPlacement(placement, targetElement);
					} else {
						console.log('Error. Cannot find position by uuid');
					};
				};
				
				// private
				// called by createNewPlacement
				this.getPositionByUuid = function(positionUuid) {
					var racePositions = thiz.getRace1Model().positions;
					for(var i = 0; i < racePositions.length; ++i) {
						if(racePositions[i].uuid === positionUuid) {
							return racePositions[i];
						};
					}
					racePositions = thiz.getRace2Model().positions;
					for(var i = 0; i < racePositions.length; ++i) {
						if(racePositions[i].uuid === positionUuid) {
							return racePositions[i];
						};
					}
					return undefined;
				};
				
				this.uiMovePlacement = function(p, oldCoordinates) {
					if(oldCoordinates.x !== 'new') {
						boardCells[oldCoordinates.x][oldCoordinates.y] = undefined;
						thiz.clearCellStyle(oldCoordinates.x, oldCoordinates.y);
					}
					boardCells[p.x][p.y] = p;

					thiz.setCellStyle(p);
					// $scope.$digest();
				};
				
				// private, called by movePlacement()
				this.persistPlacement = function(placement, targetElement) {
					oldX = placement.x;
					oldY = placement.y;
					console.log("persistPlacement.  (" + oldX + "," + oldY + ")");
					placement.x = parseInt(targetElement.getAttribute("x-bbtb-board-x"));
					placement.y = parseInt(targetElement.getAttribute("x-bbtb-board-y"));
					
					if(oldX === 'new') {
						$http.post('/bbtb/api/boards/' + thiz.boardModel.uuid + '/placements/', placement).
						 success(function(data, status, headers, config) {
							  console.log('post success. location=', headers('Location'));
							  console.log("call with OLD (" + oldX + "," + oldY + ")");
							  thiz.uiMovePlacement(placement, {x : oldX, y: oldY });
							  console.log("getBoardPlacement for ("+placement.x+","+placement.y+"): ", boardCells[placement.x][placement.y]);
							  
							  // TODO
							  // actually, we would only need to 1) update the new placement with its new uuid and 2) add it to boardModel.
							  // this call is simpler but triggers some network overhead
							  thiz.initBoardModel();
						  }).
						  error(function(data, status, headers, config) {
							  console.log('error posting placement');
						  });						
					} else {
						$http.put('/bbtb/api/boards/' + thiz.boardModel.uuid + '/placements/' + placement.uuid, placement).
						 success(function(data, status, headers, config) {
							  console.log('put success. location=', headers('Location'));
							  console.log("call with OLD (" + oldX + "," + oldY + ")");
							  thiz.uiMovePlacement(placement, {x : oldX, y: oldY });
							  console.log("getBoardPlacement for ("+placement.x+","+placement.y+"): ", boardCells[placement.x][placement.y]);
						  }).
						  error(function(data, status, headers, config) {
							  console.log('error putting placement');
							  placement.x = oldX;
							  placement.y = oldY;
						  });
					};
				};
				
				// private, called by saveBoardModelName()
				this.persistBoardModel = function() {
					$http.put('/bbtb/api/boards/' + thiz.boardModel.uuid, thiz.boardModel).
					 then(function(response) {
						  console.log('put success. location=', response.headers('Location'));
						  thiz.boardModelDirty = false;
					  },
					  function(response) {
						  console.log('error putting board, response: ' + response);
					  });
				};
				
				// public
				this.getRace1Model = function() {
					return thiz.race1Model;
				};
				
				// public
				this.getRace2Model = function() {
					return thiz.race2Model;
				};
				
				// public
				this.getRacePositionRemainingQuantity = function(position) {
					var numOnBoard = 0;
					for (var i = 0; i < thiz.boardModel.placements.length; ++i) {
						var p = thiz.boardModel.placements[i];
						if(p.position.uuid === position.uuid) {
							++numOnBoard;
						};
					}
					return position.quantity - numOnBoard;
				};
				
				// public
				this.boardModelName = function(nameIfSetting) {
					if(thiz.boardModel) {
						if(typeof nameIfSetting !== 'undefined') {
							var trimmedName = nameIfSetting.trim();
							if(thiz.boardModel.name != trimmedName) {
								thiz.boardModel.name = trimmedName;
								thiz.boardModelDirty = true;
							};
						} else {
							return thiz.boardModel.name;
						};
					};
				};
				
				// public
				this.boardModelDescription = function(nameIfSetting) {
					if(thiz.boardModel) {
						if(typeof nameIfSetting !== 'undefined') {
							console.log("nameIfSetting="+nameIfSetting);
							var trimmedDescription = nameIfSetting.trim();
							if(thiz.boardModel.description != trimmedDescription) {
								thiz.boardModel.description = trimmedDescription;
								thiz.boardModelDirty = true;
							};
							console.log("modelDescription="+thiz.boardModel.description);
						} else {
							return thiz.boardModel.description;
						};
					};
				};
				
				// public
				this.saveBoardModel = function() {
					if(thiz.boardModelDirty) {
						thiz.persistBoardModel();
					}
				};
				
				this.initBoardModelIdx();
				this.initBoardModel();
				
			} ]);

	  app.directive('showFocus', function($timeout) {
		  return function(scope, element, attrs) {
		    scope.$watch(attrs.showFocus, 
		      function (newValue) { 
		        $timeout(function() {
		            newValue && element[0].focus();
		        });
		      },true);
		  };    
		});
	
});

/*
  to call a function inside the controller from the console, example:
  angular.element(document.getElementById('TABLE')).scope().controller.initPlacements()
*/