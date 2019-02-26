angular.module('app.routes', [])

.config(function($stateProvider, $urlRouterProvider) {

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider
    

      .state('tabsController.poolMonitor', {
    url: '/page2',
    views: {
      'tab1': {
        templateUrl: 'templates/poolMonitor.html',
        controller: 'poolMonitorCtrl'
      }
    }
  })

  .state('tabsController.archiveVideoRetrieval', {
    url: '/page4',
    views: {
      'tab3': {
        templateUrl: 'templates/archiveVideoRetrieval.html',
        controller: 'archiveVideoRetrievalCtrl'
      }
    }
  })

  .state('tabsController', {
    url: '/page1',
    templateUrl: 'templates/tabsController.html',
    abstract:true
  })

  .state('tabsController.settings', {
    url: '/page5',
    views: {
      'tab4': {
        templateUrl: 'templates/settings.html',
        controller: 'settingsCtrl'
      }
    }
  })

  .state('tabsController.alertAndNotificationSettings', {
    url: '/page7',
    views: {
      'tab4': {
        templateUrl: 'templates/alertAndNotificationSettings.html',
        controller: 'alertAndNotificationSettingsCtrl'
      }
    }
  })

  .state('tabsController.videoSettings', {
    url: '/page8',
    views: {
      'tab4': {
        templateUrl: 'templates/videoSettings.html',
        controller: 'videoSettingsCtrl'
      }
    }
  })

  .state('tabsController.cameraMonitoringSettings', {
    url: '/page9',
    views: {
      'tab4': {
        templateUrl: 'templates/cameraMonitoringSettings.html',
        controller: 'cameraMonitoringSettingsCtrl'
      }
    }
  })

$urlRouterProvider.otherwise('/page1/page2')


});