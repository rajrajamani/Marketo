angular.module('mkto', ['ionic'])

.controller('MktoCtrl', function($scope, Modal) {
  $scope.leads = [
    { first: 'Mark' },
    { first: 'Shaun' },
    { first: 'Glen' },
    { first: 'Fil' }
  ];
  
 $scope.modules = [
	{ name: 'Event Checkin', imgUrl:'http://placehold.it/40/0033cc&text=checkin' },
    { name: 'Best Bets', imgUrl:'http://placehold.it/40/66ffff&text=bestbets' },
	{ name: 'Interesting Moments', imgUrl:'http://placehold.it/40/ff9966&text=moments' },
	{ name: 'Website Monitor', imgUrl:'http://placehold.it/40/ccff33&text=web' },
	{ name: 'Lead Search', imgUrl:'http://placehold.it/40/996633&text=lead' },
    { name: 'Landing Pages', imgUrl:'http://placehold.it/40/9933ff&text=LPs' },
    { name: 'Emails', imgUrl:'http://placehold.it/40/ffffcc&text=emails' },
    { name: 'Smart Lists', imgUrl:'http://placehold.it/40/990033&text=SLs' }
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
    $scope.leads.push(lead);
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
});

