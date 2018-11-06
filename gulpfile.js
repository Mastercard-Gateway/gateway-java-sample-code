/**
 *  Available options are:
 *
 *  port - The port for the server to bind with.
 *  context - The server context for the application.
 *  path - The path to be invoked in lighthouse to run the tests.
 *  performance - The minimum performance score accepted for a successful run.
 *  accessibility - The minimum accessibility score accepted for a successful run.
 *
 *  Ex 1: Using port 1026 for the server and min scores of 98 performance and 99 accessibility
 *
 *  gulp lighthouse --port 1026 --performance 98 --accessibility 99
 *
 *  Ex 2: Using port 9999 for the server and min scores of 1 performance and 2 accessibility
 *
 *  gulp lighthouse --port=9999 --performance=1 --accessibility=2
 */

const gulp = require('gulp');
const run = require('gulp-run-command').default;
const webserver = require('gulp-webserver');
const http = require('http');
const lighthouse = require('lighthouse');
const chromeLauncher = require('chrome-launcher');

let config = {
  port: 1025,
  context: '/account',
  path: '/account/en_US/index.html',
  min_scores: {
    performance: 50,
    accessibility: 70
  }
};

(argList => {
  let args = {}, opt, curOpt;

  argList.forEach(function (thisOpt) {
    opt = thisOpt.trim().replace(/^-+/, '');

    if (opt === thisOpt) {
      // argument value
      if (curOpt) {
        args[curOpt] = opt;
      }
      curOpt = null;
    }
    else {
      // argument name
      curOpt = opt;
      if (curOpt.indexOf('=')) {
        let optParts = curOpt.split('=');
        args[optParts[0]] = optParts[1];
      } else {
        args[curOpt] = true;
      }
    }
  });

  config.port = +(args.port || config.port);
  config.context = args.context || config.context;
  config.path = args.path || config.path;
  config.min_scores.accessibility = +(args.accessibility || config.min_scores.accessibility);
  config.min_scores.performance = +(args.performance || config.min_scores.performance);

  console.log(JSON.stringify(config, null, 4));
})(process.argv);

gulp.task('build', run('npm run build:prod'));

gulp.task('startServer', ['build'], function () {
  var stream = gulp.src('dist').pipe(webserver({
    port: config.port,
    directoryListing: true,
    path: config.context,
    middleware: function (req, res, next) {
      if (/_kill_\/?/.test(req.url)) {
        res.end();
        stream.emit('kill');
      }
      next();
    }
  }));
});

gulp.task('stopServer', function (cb) {
  http.request(`http://localhost:${config.port}/_kill_`).on('close', function () {
    cb();
    process.exit(0);
  }).end();
});

gulp.task('lighthouse', ['startServer'], function (cb) {
  chromeLauncher.launch({chromeFlags: ['--headless']}).then(function (chrome) {
    lighthouse(
      `http://localhost:${config.port}${config.path}`,
      {port: chrome.port},// available options - https://github.com/GoogleChrome/lighthouse/#cli-options
      {
        "extends": "lighthouse:default",
        "settings": {
          "onlyCategories": ["performance", "accessibility"]
        }
      }
    ).then(function (results) {
      chrome.kill().then(function () {
        var errors = [];
        results.reportCategories.forEach(cat => {
          console.log(cat.id, cat.score, `(${config.min_scores[cat.id]})`, cat.score > config.min_scores[cat.id]);
          if (config.min_scores[cat.id] && cat.score < config.min_scores[cat.id]) {
            errors.push(`Failure: Score for ${cat.id} (${cat.score}) is under the allowed score`);
          }
        });
        //output the html report using lighthouse's cli printer
        require('./node_modules/lighthouse/lighthouse-cli/printer').write(results, 'html', 'lighthouse-report.html');
        if (errors.length) {
          cb(errors);
        } else {
          cb();
        }
        gulp.start('stopServer');
      })
    }).catch(function (e) {
      cb(e);
      gulp.start('stopServer');
    });
  });
});

gulp.task('default', ['lighthouse']);
