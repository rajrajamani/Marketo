
angular.module('mkto', ['ionic', 'ngRoute', 'ngAnimate'])

.controller('MktoCtrl', function($scope, Modal, $routeParams) {

  $scope.leadId = $routeParams.leadId;
  $scope.leads = [
    { id: 12, firstName: 'Raj' , lastName: 'Rajamani', email:'raj@mkto', imgUrl:'http://m.c.lnkd.licdn.com/mpr/mpr/shrink_200_200/p/2/000/03c/0e3/010f950.jpg'},
    { id: 23, firstName: 'Shaun', lastName: 'Klopfenstein', email:'sklop@mkto', imgUrl:'http://m.c.lnkd.licdn.com/media/p/6/000/1ee/17b/30e7016.jpg' },
    { id: 34, firstName: 'Glen',lastName: 'Lipka', email:'glen@mkto', imgUrl:'http://m.c.lnkd.licdn.com/mpr/mpr/shrink_200_200/p/4/000/182/34e/1ae3653.jpg' },
    { id: 45, firstName: 'Phil',lastName: 'Fernandez', email:'pmf@mkto', imgUrl:'http://m.c.lnkd.licdn.com/media/p/3/000/006/059/31a002e.jpg' }
  ];

  
 $scope.modules = [
	{ parent: 'top', name: 'Event Checkin', url:'checkin', imgUrl:'http://placehold.it/40/0033cc&text=checkin' },
	{ parent: 'top', name: 'Leads', url:'lead', imgUrl:'http://placehold.it/40/996633&text=lead' },
    { parent: 'msi', name: 'Best Bets', url:'bestbets', imgUrl:'http://placehold.it/40/66ffff&text=bestbets' },
	{ parent: 'msi', name: 'Interesting Moments', url:'moments', imgUrl:'http://placehold.it/40/ff9966&text=moments' },
	{ parent: 'msi', name: 'Website Monitor', url:'webmonitor', imgUrl:'http://placehold.it/40/ccff33&text=web' },
    { parent: 'assets', name: 'Landing Pages', url:'lps', imgUrl:'http://placehold.it/40/9933ff&text=LPs' },
    { parent: 'assets',name: 'Emails', url:'emails', imgUrl:'http://placehold.it/40/ffffcc&text=emails' },
    { parent: 'assets',name: 'Smart Campaigns', url:'scs', imgUrl:'http://placehold.it/40/990033&text=SCs' }
  ];


  
  // Create and load the Modal
  Modal.fromTemplateUrl('new-lead.html', function(modal) {
     $scope.leadModal = modal;
  }, {
    scope: $scope,
    animation: 'slide-in-up'
  });
  
  // Called when the form is submitted
  $scope.createLead = function(lead) {
    $scope.savedLead = {};
	angular.copy(lead, $scope.savedLead);
    $scope.leads.push($scope.savedLead);
    $scope.leadModal.hide();	
  };

  // Open our new lead modal
  $scope.newLead = function() {
    $scope.leadModal.show();
  };

  // Close the new lead modal
  $scope.closeNewLead = function() {
    $scope.leadModal.hide();
  };  
  
  $scope.toggleMenu = function() {
  $scope.sideMenuController.toggleLeft();
 };
 
 
})

.controller('LPCtrl', function($scope, $http) {
 $http.get('data/lps.json').success(function(data) {
    $scope.lps = data;
  });
})

.controller('LPDetailCtrl', function($scope, $routeParams, $http) {
 $scope.lpId = $routeParams.lpId;
 $http.get('data/lps.json').success(function(data) {
    $scope.lps = data;
  });
})


.config(function($routeProvider) {

  // Set up the initial routes that our app will respond to.
  // These are then tied up to our nav router which animates and
  // updates a navigation bar
  $routeProvider.when('/home', {
    templateUrl: 'templates/apps.html',
    controller: 'MktoCtrl'
  });

  $routeProvider.when('/checkin', {
    templateUrl: 'templates/checkin.html',
    controller: 'MktoCtrl'
  });

  $routeProvider.when('/lead', {
    templateUrl: 'templates/lead.html',
	title: 'Lead Database',
    controller: 'MktoCtrl'
  });

  $routeProvider.when('/lead/:leadId', {
    templateUrl: 'templates/leaddetails.html',
	title: 'Lead Database',
    controller: 'MktoCtrl'
  });
  
  $routeProvider.when('/lps', {
    templateUrl: 'templates/lps.html',
    controller: 'LPCtrl'
  });

  $routeProvider.when('/lp/:lpId', {
    templateUrl: 'templates/lpdetails.html',
    controller: 'LPDetailCtrl'
  });

  $routeProvider.when('/emails', {
    templateUrl: 'templates/emails.html',
    controller: 'MktoCtrl'
  });
  
  $routeProvider.when('/scs', {
    templateUrl: 'templates/scs.html',
    controller: 'MktoCtrl'
  });

  $routeProvider.when('/bestbets', {
    templateUrl: 'templates/bestbets.html',
    controller: 'MktoCtrl'
  });
  
  $routeProvider.when('/moments', {
    templateUrl: 'templates/moments.html',
    controller: 'MktoCtrl'
  });
  
  $routeProvider.when('/webmonitor', {
    templateUrl: 'templates/webmonitor.html',
    controller: 'MktoCtrl'
  });
  
  $routeProvider.otherwise({
    redirectTo: '/home'
  });

});



