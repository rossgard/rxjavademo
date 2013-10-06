'use strict';

/* Controllers */

var app = angular.module('myApp.controllers', []);

app.controller('MyCtrl1', function ($scope, $http) {
    $scope.name = 'World';

    $scope.search = function (searchString) {
        $scope.results = "Searching...";

        /*  $http.get('/webapi/myresource').success(function(data, status, headers, config) {
            $scope.results = data
        });*/

        $http.get('/webapi/myresource/service1').success(function(data, status, headers, config) {
            $scope.results1 = data
        });

        $http.get('/webapi/myresource/service2').success(function(data, status, headers, config) {
            $scope.results2 = data
        });

        $http.get('/webapi/myresource/service3').success(function(data, status, headers, config) {
            $scope.results3 = data
        });
    }
});
app.controller('MyCtrl2', [function () {

}]);