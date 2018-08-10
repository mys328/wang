<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
  <script data-ionic="inject">
    (function(w){var i=w.Ionic=w.Ionic||{};i.version='3.7.1';i.angular='4.4.3';i.staticDir='build/';})(window);
  </script>
  <meta charset="UTF-8">
  <title>企云会</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <meta name="format-detection" content="telephone=no">
  <meta name="msapplication-tap-highlight" content="no">

  <link rel="icon" type="image/x-icon" href="assets/icon/favicon.ico">
  <link rel="manifest" href="manifest.json">
  <meta name="theme-color" content="#4e8ef7">
  
  <!-- add to homescreen for ios -->
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">

  <!-- cordova.js required for cordova apps -->
 <%-- <script src="cordova.js"></script>--%>

  <!-- un-comment this code to enable service worker
  <script>
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker.register('service-worker.js')
        .then(() => console.log('service worker installed'))
        .catch(err => console.error('Error', err));
    }
  </script>-->
  <style type="text/css">
    .loader {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 48px;
      height: 100%;
      margin: 0 auto;
    }
    .loader span {
      display: inline-block;
      width: 12px;
      height: 12px;
      border-radius: 6px;
      opacity: 0;
      background-color: rgba(47,162,245,1);
      transform: scale(0);
    }
    .loader span:nth-child(1) {
      animation: pulse 1s 0s infinite linear;
    }
    .loader span:nth-child(2) {
      animation: pulse 1s 0.2s infinite linear;
    }
    .loader span:nth-child(3) {
      animation: pulse 1s 0.4s infinite linear;
    }
    @keyframes pulse {
      0%, 100% {
        opacity: 0.5;
        transform: scale(0.5);
      }
      50% {
        opacity: 1;
        transform: scale(1);
      }
    }
  </style>
  <link href="build/main.css" rel="stylesheet">

</head>
<body>

  <!-- Ionic's root component and where the app will load -->
  <ion-app>
    <div class="loader">
      <span></span>
      <span></span>
      <span></span>
    </div>
  </ion-app>

  <!-- The polyfills js is generated during the build process -->
  <script src="build/polyfills.js"></script>

  <!-- The vendor js is generated during the build process
       It contains all of the dependencies in node_modules -->
  <script src="build/vendor.js"></script>

  <!-- The main bundle js is generated during the build process -->
  <script src="build/main.js"></script>

</body>
</html>